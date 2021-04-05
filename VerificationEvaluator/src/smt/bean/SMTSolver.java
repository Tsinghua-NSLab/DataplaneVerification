package smt.bean;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

import com.microsoft.z3.*;

import bean.basis.Rule;
import config.TypeConfig;
import headers.testHeader;

public class SMTSolver{
	public Context ctx;
	public TupleSort IPSort = null;
	public testHeader cs = new testHeader();
	public HashMap<Integer, Expr> portToHeader = new HashMap<Integer, Expr>();
	public HashSet<Integer> ports = new HashSet<Integer>();
	public HashMap<Integer, BoolExpr> portToArrive = new HashMap<Integer, BoolExpr>();
	//Rule
	public HashMap<String, Expr> idToRule = new HashMap<String, Expr>();
	public HashMap<Integer, ArrayList<String>> portToRule = new HashMap<Integer, ArrayList<String>>();
	public HashMap<String, ArrayList<Integer>> ruleToPort = new HashMap<String, ArrayList<Integer>>();

	public Solver solver;
	
	public void init() {
		HashMap<String,String> cfg = new HashMap<String,String>();
		cfg.put("model","true");
		ctx = new Context(cfg);
		solver = ctx.mkSolver();
		if(TypeConfig.ABSTRACT_IP_TYPE=="Wildcard") {
			Sort BVType = ctx.mkBitVecSort(cs.getFormat().get("length")*2);
			Sort intType = ctx.getIntSort();
			IPSort = ctx.mkTupleSort(ctx.mkSymbol("Wildcard"),
					new Symbol[] {ctx.mkSymbol("length"),ctx.mkSymbol("wcBit")},
					new Sort[] {intType, BVType});
		}
	}

	public void initTest() {
		//ports
		ports.add(1);
		ports.add(2);
		ports.add(3);
		ports.add(4);
		//portToArrive
		for(int port:ports) {
			portToArrive.put(port, ctx.mkBoolConst(Integer.toString(port)));
		}
		//idToRule
		IntExpr lengthExpr = ctx.mkInt(cs.getFormat().get("length")*2);
		BitVecExpr rule1Expr = ctx.mkBV(0xFFFFFF55, cs.getFormat().get("length")*2);
		BitVecExpr rule2Expr = ctx.mkBV(0xFFFFFFFF, cs.getFormat().get("length")*2); 
		BitVecExpr rule3Expr = ctx.mkBV(0x5555FFF6, cs.getFormat().get("length")*2);
		Expr rule1 = IPSort.mkDecl().apply(lengthExpr, rule1Expr);
		Expr rule2 = IPSort.mkDecl().apply(lengthExpr, rule2Expr);
		Expr rule3 = IPSort.mkDecl().apply(lengthExpr, rule3Expr);
		idToRule.put("rule1", rule1);
		idToRule.put("rule2", rule2);
		idToRule.put("rule3", rule3);
		//portToRule
		portToRule.put(1, new ArrayList<String>());
		portToRule.get(1).add("rule1");
		portToRule.put(2, new ArrayList<String>());
		portToRule.get(2).add("rule2");
		portToRule.put(3, new ArrayList<String>());
		portToRule.get(3).add("rule3");
		//ruleToPort
		ruleToPort.put("rule1", new ArrayList<Integer>());
		ruleToPort.get("rule1").add(2);
		ruleToPort.put("rule2", new ArrayList<Integer>());
		ruleToPort.get("rule2").add(3);
		ruleToPort.put("rule3", new ArrayList<Integer>());
		ruleToPort.get("rule3").add(4);
		//header in port 1
		BitVecExpr headerExpr = ctx.mkBV(0xFFFFFFFF, cs.getFormat().get("length")*2);
		portToHeader.put(1, IPSort.mkDecl().apply(lengthExpr,headerExpr)); 
		//header in port 2,3,4
		portToHeader.put(2, IPSort.mkDecl().apply(lengthExpr,ctx.mkBVConst("rule2bit", 32)));
		portToHeader.put(3, IPSort.mkDecl().apply(lengthExpr,ctx.mkBVConst("rule3bit", 32)));
		portToHeader.put(4, IPSort.mkDecl().apply(lengthExpr,ctx.mkBVConst("rule4bit", 32)));
	}
	public void addRule(int inPort){
		FuncDecl getBit = IPSort.getFieldDecls()[1];
		for(String ruleID: portToRule.get(inPort)) {
			Expr rule = idToRule.get(ruleID);
			BitVecExpr ruleIP = (BitVecExpr)getBit.apply(rule);
			BitVecExpr headerIP = (BitVecExpr)getBit.apply(portToHeader.get(inPort));
			BoolExpr match = isEmpty(ctx.mkBVAND(ruleIP, headerIP));
			BoolExpr matchArrive = ctx.mkAnd(portToArrive.get(inPort),match);
			for(int outPort: ruleToPort.get(ruleID)) {
				solver.add(ctx.mkImplies(matchArrive, portToArrive.get(outPort)));
				solver.add(ctx.mkImplies(matchArrive, ctx.mkEq((BitVecExpr)getBit.apply(portToHeader.get(outPort)),ctx.mkBVAND(ruleIP, headerIP))));
			}
		}
		//init startport
		solver.add(portToArrive.get(1));
		solver.add(portToArrive.get(4));
	}
	public void Solve() {
		Status res = solver.check();
		if(res == Status.SATISFIABLE) {
			Model m = solver.getModel();
			FuncDecl getBit = IPSort.getFieldDecls()[1];
			Expr test = m.eval(getBit.apply(portToHeader.get(4)), false);
			System.out.println("Satisfied");
			System.out.println(test);
		}else if(res == Status.UNSATISFIABLE) {
			System.out.println("Unsatisfied");
		}
	}
	public BoolExpr isEmpty(BitVecExpr test) {
		BitVecExpr testRight = ctx.mkBVRotateRight(1, test);
		BitVecExpr findZ = ctx.mkBVOR(test, testRight);
		BitVecExpr Helper = ctx.mkBV(0x55555555,32);
		return ctx.mkEq(Helper, ctx.mkBVAND(findZ, Helper));
	}
	public static void main(String args[]) {
		SMTSolver testSMT = new SMTSolver();
		testSMT.init();
		testSMT.initTest();
		for(int i = 1; i< 4;i++) {
			testSMT.addRule(i);
		}
		testSMT.Solve();
	}
}