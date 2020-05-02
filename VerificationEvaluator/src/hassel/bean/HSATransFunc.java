package hassel.bean;

import java.util.ArrayList;

import bean.basis.BasicTF;
import bean.basis.Influence;
import bean.basis.Node;
import bean.basis.Rule;
import interfaces.AbstractIP;
import interfaces.Header;

public class HSATransFunc{
	BasicTF TF;
	
	public HSATransFunc(BasicTF TF) {
		this.TF = TF;
	}
	
	public ArrayList<Node> applyRewriteRule(Rule rule, Node input, ArrayList<String> appliedRules){
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<Integer> modOutPorts = new ArrayList<Integer>();
		modOutPorts.addAll(rule.getOutPorts());
		//If not sedning on receiving port, remove it from outports.
		if(!TF.sendOnReceivingPort&&modOutPorts.contains(input.getPort())) {
			modOutPorts.remove(modOutPorts.indexOf(input.getPort()));
		}
		//If no outport, don't do anything.
		if(modOutPorts.size() == 0) {
			appliedRules.add(rule.getId());
			return result;
		}
		//check if match pattern matches and port is in in_ports.
		Header newHS = input.getHdr().copyAnd(rule.getMatch());
		if((!newHS.isEmpty())&&rule.getInPorts().contains(input.getPort())) {
			for(Influence inf:TF.idToAffectedBy.get(rule.getId())) {
				if(inf.getPorts().contains(input.getPort())&&(appliedRules==null||appliedRules.contains(inf.getInfluencedBy().getId()))) {
					newHS.minus(inf.getIntersect());
				}
			}
			//apply mask,rewrite to all elements in hs_list and hs_diff, 
            //considering the cardinality.
			newHS.rewrite(rule.getMask(), rule.getRewrite());
			if(newHS.isEmpty()) {
				appliedRules.add(rule.getId());
				return result;
			}
			newHS.pushAppliedTfRule(rule.getId(), input.getPort());
			appliedRules.add(rule.getId());
		}
		return result;
	}
	
	public ArrayList<Node> applyFwdRule(Rule rule, Node input, ArrayList<String> appliedRules){
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<Integer> modOutPorts = new ArrayList<Integer>();
		modOutPorts.addAll(rule.getOutPorts());
		//If not sedning on receiving port, remove it from outports.
		if(!TF.sendOnReceivingPort&&modOutPorts.contains(input.getPort())) {
			modOutPorts.remove(modOutPorts.indexOf(input.getPort()));
		}
		//If no outport, don't do anything.
		if(modOutPorts.size() == 0) {
			appliedRules.add(rule.getId());
			return result;
		}
		//check if match pattern matches and port is in in_ports.
		Header newHS = input.getHdr().copyAnd(rule.getMatch());
		if((!newHS.isEmpty())&&rule.getInPorts().contains(input.getPort())) {
			if(!TF.isUncovered) {
				for(Influence inf:TF.idToAffectedBy.get(rule.getId())) {
					if(inf.getPorts().contains(input.getPort())&&(appliedRules==null||appliedRules.contains(inf.getInfluencedBy().getId()))) {
						newHS.minus(inf.getIntersect());
					}
				}
			}
			newHS.cleanUp();
			if(newHS.isEmpty()) {
				appliedRules.add(rule.getId());
				return result;
			}
			newHS.pushAppliedTfRule(rule.getId(), input.getPort());
			appliedRules.add(rule.getId());
			for(int outPort:modOutPorts) {
				result.add(new Node(newHS,outPort));
			}
		}
		return result;
	}
	
	public ArrayList<Node> applyLinkRule(Rule rule, Node input){
		ArrayList<Node> result = new ArrayList<Node>();
		if(rule.getInPorts().contains(input.getPort())) {
			Header ohs = input.getHdr().copy();
			ohs.pushAppliedTfRule(rule.getId(), input.getPort());
			for(int outPort: rule.getOutPorts()) {
				result.add(new Node(ohs,outPort));
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
			ruleSet = TF.getRulesForInport(node.getPort());
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