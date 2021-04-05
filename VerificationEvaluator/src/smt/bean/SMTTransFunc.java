package smt.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import bean.basis.BasicTF;
import bean.basis.Influence;
import bean.basis.Node;
import bean.basis.Rule;
import factory.HeaderFactory;
import interfaces.Header;
import interfaces.TransferFunc;
import utils.General;

public class SMTTransFunc implements TransferFunc{
	BasicTF NTF;
	BasicTF TTF;
	Context ctx;
	Solver solver;
	public SMTTransFunc(BasicTF NTF, BasicTF TTF) {
		this.NTF = NTF;
		this.TTF = TTF;
		HashMap<String,String> cfg = new HashMap<String,String>();
		cfg.put("model","true");
		this.ctx = new Context(cfg);
		this.solver = this.ctx.mkSolver();
	}
	public ArrayList<Node> solve(Node inputPkt, ArrayList<Integer> outPorts, HashSet<Integer> edgePorts) {
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<Integer> arrivedPorts = new ArrayList<Integer>();
		//TODO the definition of header length
		//BitVecExpr pkt = this.ctx.mkBVConst("pkt", inputPkt.getHdr().getLength());
		Expr pkt = HeaderFactory.generateZ3Header(ctx, inputPkt.getHdr().getLength());
		//if(!inputPkt.getHdr().isEmpty()) {
			//TODO init pktValue from inputPkt
		//IntExpr pktValue = this.ctx.mkInt(7);//(0xaaab, 16);
		//BoolExpr pkt_rule = this.ctx.mkEq(pkt, pktValue);
		//this.solver.add(pkt_rule);
		//}
		
		// not empty constraints
		this.solver.add(HeaderFactory.generateNotEmptyRule(ctx, inputPkt.getHdr().getLength()));
		
		// sum the reachability booleans by port
		HashMap<Integer, ArrayList<BoolExpr>> z3_ntf_rules_sum = new HashMap<Integer, ArrayList<BoolExpr>>();
		HashMap<Integer, ArrayList<BoolExpr>> z3_ttf_rules_sum = new HashMap<Integer, ArrayList<BoolExpr>>();
		for(Rule rule : this.NTF.getRules()) {
			HashMap<Integer, ArrayList<BoolExpr>> z3_rules = rule.generate_z3_rule(ctx, pkt);
			for(int port : z3_rules.keySet()) {
				if(!z3_ntf_rules_sum.containsKey(port)) {
					z3_ntf_rules_sum.put(port, new ArrayList<BoolExpr>());
				}
				z3_ntf_rules_sum.get(port).addAll(z3_rules.get(port));
				//this.solver.add(z3_rule);
			}
		}
		for(Rule rule : this.TTF.getRules()) {
			HashMap<Integer, ArrayList<BoolExpr>> z3_rules = rule.generate_z3_rule(ctx, pkt);
			for(int port : z3_rules.keySet()) {
				if(!z3_ttf_rules_sum.containsKey(port)) {
					z3_ttf_rules_sum.put(port, new ArrayList<BoolExpr>());
				}
				z3_ttf_rules_sum.get(port).addAll(z3_rules.get(port));
				//this.solver.add(z3_rule);	
			}
		}
		// generate z3 solver rules by port
		for(int port : z3_ntf_rules_sum.keySet()) {
			BoolExpr z3_rule_sum = ctx.mkBool(false);
			for(BoolExpr z3_rule : z3_ntf_rules_sum.get(port)) {
				z3_rule_sum = ctx.mkOr(z3_rule_sum, z3_rule);
			}
			if(port == inputPkt.getPort()) {
				continue;
			}else if(outPorts.contains(port)) {
				z3_rule_sum = ctx.mkEq(z3_rule_sum, ctx.mkBoolConst("at_outport_" + String.valueOf(port)));
			}else {
				z3_rule_sum = ctx.mkEq(z3_rule_sum, ctx.mkBoolConst("at_outport_" + String.valueOf(port)));
			}
			this.solver.add(z3_rule_sum);
		}
		// generate z3 solver rules by port
		for(int port : z3_ttf_rules_sum.keySet()) {
			BoolExpr z3_rule_sum = ctx.mkBool(false);
			for(BoolExpr z3_rule : z3_ttf_rules_sum.get(port)) {
				z3_rule_sum = ctx.mkOr(z3_rule_sum, z3_rule);
			}
			if(port == inputPkt.getPort()) {
				continue;
			}else if(outPorts.contains(port)) {
				z3_rule_sum = ctx.mkEq(z3_rule_sum, ctx.mkBoolConst("at_inport_" + String.valueOf(port)));
			}else {
				z3_rule_sum = ctx.mkEq(z3_rule_sum, ctx.mkBoolConst("at_inport_" + String.valueOf(port)));
			}
			this.solver.add(z3_rule_sum);
		}
		// initialize for test
		for(int port : edgePorts) {
			if(port != inputPkt.getPort() && !outPorts.contains(port)) {
				this.solver.add(ctx.mkEq(ctx.mkBoolConst("at_inport_" + String.valueOf(port)), ctx.mkBool(false)));
			}
		}
		
		// initialize input port
		this.solver.add(ctx.mkEq(ctx.mkBoolConst("at_inport_" + String.valueOf(inputPkt.getPort())), ctx.mkBool(true)));
		// initialize output port
		for(int outPort : outPorts) {
		 	this.solver.add(ctx.mkEq(ctx.mkBoolConst("at_outport_" + String.valueOf(outPort)), ctx.mkBool(true)));
		}
		System.out.println(this.solver);
		Status res = solver.check();
		if(res == Status.SATISFIABLE) {
			Model m = solver.getModel();
			Header resultHdr = null;
			//FuncDecl getBit = IPSort.getFieldDecls()[1];
			//Expr test = m.eval(getBit.apply(portToHeader.get(4)), false);
			for(FuncDecl constant : m.getConstDecls()) {
				Expr value = m.getConstInterp(constant);
				String keyString = constant.toString().split(" ")[1];
				String valueString = value.toString();
				System.out.println(keyString + ":" + valueString);
				if(keyString.startsWith("at_outport_")) {
					int port = Integer.parseInt(keyString.substring(11));
					if(valueString.equals("true") && outPorts.contains(port)) {
						arrivedPorts.add(port);
					}
				}else if(keyString.equals("pkt")) {
					resultHdr = HeaderFactory.generateOutputHeader(Integer.parseInt(value.toString()), inputPkt.getHdr().getLength());
				}
			}
			for(int arrivedPort : arrivedPorts) {
				result.add(new Node(resultHdr, arrivedPort));
			}
			System.out.println("Satisfied");
			//System.out.println(test);
		}else if(res == Status.UNSATISFIABLE) {
			System.out.println("Unsatisfied");
		}
		return result;
	}
}