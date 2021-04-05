package inverse.bean;

import java.util.ArrayList;

import bean.basis.BasicTF;
import bean.basis.Influence;
import bean.basis.Node;
import bean.basis.Rule;
import interfaces.Header;
import interfaces.TransferFunc;

public class InverseTransFunc implements TransferFunc{
	BasicTF TF;
	public InverseTransFunc(BasicTF TF) {
		this.TF = TF;
	}
	public ArrayList<Node> applyRewriteRule(Rule rule, Node output, ArrayList<String> appliedRules){
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<Integer> modInPorts = new ArrayList<Integer>();
		modInPorts.addAll(rule.getInPorts());
		//If not sending on receiving port, remove it from inports.
		if(!TF.sendOnReceivingPort&&modInPorts.contains(output.getPort())) {
			modInPorts.remove(modInPorts.indexOf(output.getPort()));
		}
		//If no inport, don't do anything.
		if(modInPorts.size() == 0) {
			appliedRules.add(rule.getId());
			return result;
		}
		//check if match pattern matches and port is in out_ports.
		Header protoHS = output.getHdr().copyAnd(rule.getMatch());
		if(protoHS.isEmpty()||(!rule.getOutPorts().contains(output.getPort()))) {
			return result;
		}else {
			for(int inPort:modInPorts) {
				Header newHS = protoHS.copy();
				if(!TF.isUncovered) {
					for(Influence inf:TF.idToAffectedBy.get(rule.getId())) {
						if(inf.getPorts().contains(inPort)) {//&&(appliedRules==null||appliedRules.contains(inf.getInfluencedBy().getId()))) {
							newHS.minus(inf.getIntersect());
						}
					}
				}
				//apply mask,rewrite to all elements in hs_list and hs_diff, 
				//considering the cardinality.
				newHS.rewrite(rule.getMask(), rule.getInverseRewrite());
				if(newHS.isEmpty()) {
					appliedRules.add(rule.getId());
					return result;
				}
				newHS.pushAppliedTfRule(rule.getId(), inPort);
				appliedRules.add(rule.getId());
				result.add(new Node(newHS,inPort));
			}
		}
		return result;
	}
	
	public ArrayList<Node> applyFwdRule(Rule rule, Node output, ArrayList<String> appliedRules){
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<Integer> modInPorts = new ArrayList<Integer>();
		modInPorts.addAll(rule.getInPorts());
		//If not sending on receiving port, remove it from outports.
		if(!TF.sendOnReceivingPort&&modInPorts.contains(output.getPort())) {
			modInPorts.remove(modInPorts.indexOf(output.getPort()));
		}
		//If no outport, don't do anything.
		if(modInPorts.size() == 0) {
			appliedRules.add(rule.getId());
			return result;
		}
		//check if match pattern matches and port is in in_ports.
		Header protoHS = output.getHdr().copyAnd(rule.getMatch());
		if(protoHS.isEmpty()||(!rule.getOutPorts().contains(output.getPort()))) {
			return result;
		}else {
			for(int inPort:modInPorts) {
				Header newHS = protoHS.copy();
				if(!TF.isUncovered) {
					for(Influence inf:TF.idToAffectedBy.get(rule.getId())) {
						if(inf.getPorts().contains(inPort)) {//&&(appliedRules==null||appliedRules.contains(inf.getInfluencedBy().getId()))) {
							newHS.minus(inf.getIntersect());
						}
					}
				}
				newHS.cleanUp();
				if(newHS.isEmpty()) {
					appliedRules.add(rule.getId());
					return result;
				}
				newHS.pushAppliedTfRule(rule.getId(), inPort);
				appliedRules.add(rule.getId());
				result.add(new Node(newHS,inPort));
			}	
		}
		return result;
	}
	
	public ArrayList<Node> applyLinkRule(Rule rule, Node output){
		ArrayList<Node> result = new ArrayList<Node>();
		if(rule.getOutPorts().contains(output.getPort())) {
			Header ohs = output.getHdr().copy();
			for(int inPort: rule.getInPorts()) {
				ohs.pushAppliedTfRule(rule.getId(), inPort);
				result.add(new Node(ohs,inPort));
			}
		}
		return result;
	}
	
	public ArrayList<Node> T(Node node){
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<String> appliedRules = new ArrayList<String>();
		ArrayList<Rule> ruleSet = new ArrayList<Rule>();
		
		if(TF.hashTableActive) {
			//for(Wildcard w: node.getHdr().getHsList()) {
				//TODO implement hash table
			//}
		}else {
			ruleSet = TF.getRulesForOutport(node.getPort());
		}
		
		//lazyTfRuleIDs
		for(Rule rule: ruleSet) {
			//TODO check if this rule qualifies for lazy evaluation
			if(rule.getAction()=="link") {
				result.addAll(this.applyLinkRule(rule, node));
			}else if(rule.getAction()=="rw") {
				result.addAll(this.applyRewriteRule(rule, node, appliedRules));
			}else if(rule.getAction()=="fwd") {
				result.addAll(this.applyFwdRule(rule, node, appliedRules));
			}
		}
		//TODO lazy tf rules
		//TODO custom rules
		
		return result;
	}
}