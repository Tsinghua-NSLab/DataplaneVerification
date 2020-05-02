package bean.basis;

import java.util.ArrayList;
import java.util.HashMap;

import bean.Link;
import bean.Network;
//import hassel.bean.HS;
//import hassel.bean.Wildcard;
import interfaces.Header;
import interfaces.Parser;
import interfaces.TransferFunc;

public class tfFunction implements TransferFunc{
	
	int nextID = 0;
	String prefixID = "";
	ArrayList<Rule> rules = new ArrayList<Rule>();
	HashMap<String, Parser> parserList = new HashMap<String, Parser>();
	HashMap<Integer, ArrayList<Rule>> inportToRule= new HashMap<Integer, ArrayList<Rule>>();
	HashMap<Integer, ArrayList<Rule>> outportToRule= new HashMap<Integer, ArrayList<Rule>>();
	HashMap<String, Rule> idToRule = new HashMap<String,Rule>();
	HashMap<String, ArrayList<Influence>> idToAffectedBy = new HashMap<String, ArrayList<Influence>>();
	HashMap<String, ArrayList<Rule>> idToInfluenceOn = new HashMap<String, ArrayList<Rule>>();
	boolean hashTableActive = false;
	boolean sendOnReceivingPort = false;
	
	/**
	 * Generate Rule ID
	 * @return
	 */
	public String generateNextID() {
		nextID++;
		return prefixID+nextID;
	}
	
	public void addFwdRule(Rule rule) {
		addFwdRule(rule,-1);
	}
	
	public void addFwdRule(Rule rule, int position) {
		rule.setAction("fwd");
		rule.setId(this.generateNextID());
		if(position == -1||position>=rules.size()) {
			position = rules.size();
			rules.add(rule);
			this.idToAffectedBy.put(rule.getId(), new ArrayList<Influence>());
			this.idToInfluenceOn.put(rule.getId(), new ArrayList<Rule>());
		}else {
			rules.add(position, rule);
			this.idToAffectedBy.put(rule.getId(), new ArrayList<Influence>());
			this.idToInfluenceOn.put(rule.getId(), new ArrayList<Rule>());
		}
		this.findInfluences(position);
		this.setFastLookupPointers(position);
	}
	
	public void addRewriteRule(Rule rule) {
		addRewriteRule(rule,-1);
	}
	public void addRewriteRule(Rule rule, int position) {
		//Mask rewrite
		Header temp = rule.getMask().copy();
		temp.complement();
		rule.getRewrite().and(temp);
		rule.setAction("rw");
		//Finding inverse
		rule.setInverseMatch(rule.getMask().copy());
		rule.getInverseMatch().and(rule.getMatch());
		rule.getInverseMatch().add(rule.getRewrite());
		rule.setInverseRewrite(rule.getMask().copy());
		rule.getInverseRewrite().complement();
		rule.getInverseRewrite().and(rule.getMatch());
		//Setting up id
		rule.setId(this.generateNextID());
		//Inserting rule at correct position
		if(position == -1 || position>this.rules.size()) {
			rules.add(rule);
			position = rules.size()-1;
			this.idToAffectedBy.put(rule.getId(), new ArrayList<Influence>());
			this.idToInfluenceOn.put(rule.getId(), new ArrayList<Rule>());
		}else {
			rules.add(position, rule);
			this.idToAffectedBy.put(rule.getId(), new ArrayList<Influence>());
			this.idToInfluenceOn.put(rule.getId(), new ArrayList<Rule>());
		}
		//Setting up fast lookups and influences
		this.findInfluences(position);
		this.setFastLookupPointers(position);
	}
	
	public void addLinkRule(Rule rule) {
		addLinkRule(rule,-1);
	}
	
	public void addLinkRule(Rule rule, int position) {
		rule.setAction("link");
		rule.setId(this.generateNextID());
		if(position == -1) {
			this.rules.add(rule);
			position = this.rules.size()-1;
			this.idToAffectedBy.put(rule.getId(), new ArrayList<Influence>());
			this.idToInfluenceOn.put(rule.getId(), new ArrayList<Rule>());
		}else {
			this.rules.add(position, rule);
			this.idToAffectedBy.put(rule.getId(), new ArrayList<Influence>());
			this.idToInfluenceOn.put(rule.getId(), new ArrayList<Rule>());
		}
		this.setFastLookupPointers(position);
	}
	
	public void findInfluences(int position) {
		Rule newRule = rules.get(position);
		//higher position rules
		for(int i = 0; i < position; i++) {
			if(rules.get(i).getAction().equals("rw")||rules.get(i).getAction().equals("fwd")) {
				ArrayList<Integer> commonPorts = new ArrayList<Integer>();
				for(int inPort:newRule.getInPorts()) {
					if(rules.get(i).getInPorts().contains(inPort));
					commonPorts.add(inPort);
				}
				Header intersect = rules.get(i).getMatch().copy();
				intersect.and(newRule.getMatch());
				if(!intersect.isEmpty()&&commonPorts.size()>0) {
					if(!this.idToAffectedBy.containsKey(newRule.getId())) {
						this.idToAffectedBy.put(newRule.getId(), new ArrayList<Influence>());
					}
					this.idToAffectedBy.get(newRule.getId()).add(new Influence(rules.get(i),intersect,commonPorts));
					if(!this.idToInfluenceOn.containsKey(rules.get(i).getId())) {
						this.idToInfluenceOn.put(rules.get(i).getId(), new ArrayList<Rule>());
					}
					this.idToInfluenceOn.get(rules.get(i).getId()).add(rules.get(position));
					//newRule.getAffectedBy().add(new Influence(rules.get(i),intersect,commonPorts));
					//rules.get(i).getInfluenceOn().add(rules.get(position));
				}
			}
		}
		for(int i = position + 1; i < rules.size(); i++) {
			if(rules.get(i).getAction().equals("rw")||rules.get(i).getAction().equals("fwd")) {
				ArrayList<Integer> commonPorts = new ArrayList<Integer>();
				for(int inPort:newRule.getInPorts()) {
					if(rules.get(i).getInPorts().contains(inPort));
					commonPorts.add(inPort);
				}
				Header intersect = rules.get(i).getMatch().copy();
				intersect.and(newRule.getMatch());
				if(!intersect.isEmpty()&&commonPorts.size()>0) {
					this.idToInfluenceOn.get(newRule.getId()).add(rules.get(i));
					this.idToAffectedBy.get(rules.get(i).getId()).add(new Influence(rules.get(position),intersect,commonPorts));
					//newRule.getInfluenceOn().add(rules.get(i));
					//rules.get(i).getAffectedBy().add(new Influence(rules.get(position),intersect,commonPorts));
				}
			}
		}
	}
	
	public void setFastLookupPointers(int position) {
		Rule newRule = rules.get(position);
		ArrayList<Integer> inPorts = rules.get(position).getInPorts();
		ArrayList<Integer> outPorts = rules.get(position).getOutPorts();
		//input port based lookup table
		for(int p: inPorts) {
			if(!this.inportToRule.containsKey(p)) {
				this.inportToRule.put(p, new ArrayList<Rule>());
			}
			this.inportToRule.get(p).add(newRule);
		}
		//output port based lookup table
		for(int p: outPorts) {
			if(!this.outportToRule.containsKey(p)) {
				this.outportToRule.put(p, new ArrayList<Rule>());
			}
			this.outportToRule.get(p).add(newRule);
		}
		//rule-id based lookup table
		this.idToRule.put(newRule.getId(), newRule);
		//TODO: hash table set up
	}
	
	public ArrayList<Node> applyRewriteRule(Rule rule, Node input, ArrayList<String> appliedRules){
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<Integer> modOutPorts = new ArrayList<Integer>();
		modOutPorts.addAll(rule.getOutPorts());
		//If not sedning on receiving port, remove it from outports.
		if(!this.sendOnReceivingPort&&modOutPorts.contains(input.getPort())) {
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
			for(Influence inf:this.idToAffectedBy.get(rule.getId())) {
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
		if(!this.sendOnReceivingPort&&modOutPorts.contains(input.getPort())) {
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
			for(Influence inf:this.idToAffectedBy.get(rule.getId())) {
				if(inf.getPorts().contains(input.getPort())&&(appliedRules==null||appliedRules.contains(inf.getInfluencedBy().getId()))) {
					newHS.minus(inf.getIntersect());
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
		
		if(this.hashTableActive) {
			//for(Wildcard w: node.getHdr().getHsList()) {
				//TODO implement hash table
			//}
		}else {
			ruleSet = this.getRulesForInport(node.getPort());
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
	
	public ArrayList<Rule> getRulesForInport(int inport){
		if(this.inportToRule.containsKey(inport)){
			return this.inportToRule.get(inport);
		}
		return new ArrayList<Rule>();
	}
	
	public ArrayList<Rule> getRulesForOutport(int outport){
		if(this.outportToRule.containsKey(outport)){
			return this.outportToRule.get(outport);
		}
		return new ArrayList<Rule>();
	}
	
	public void writeTopology(Network network) {
		System.out.println("===Generating Topology===");
		int outPortAddition = Parser.PORT_TYPE_MULTIPLIER*Parser.OUTPUT_PORT_TYPE_CONST;
		for(Link link:network.getLinks()) {
			Parser fromRtr = network.getRouters().get(link.getDevice1());
			Parser toRtr = network.getRouters().get(link.getDevice2());
			ArrayList<Integer> fromPorts = new ArrayList<Integer>();
			ArrayList<Integer> toPorts = new ArrayList<Integer>();
			fromPorts.add(fromRtr.get_port_id(link.getIface1())+outPortAddition);
			toPorts.add(toRtr.get_port_id(link.getIface2()));
			Rule rule = new Rule(fromPorts, null, toPorts, null, null);
			this.addLinkRule(rule);
			fromPorts.clear();
			toPorts.clear();
			fromPorts.add(toRtr.get_port_id(link.getIface2())+outPortAddition);
			toPorts.add(fromRtr.get_port_id(link.getIface1()));
			rule = new Rule(fromPorts, null, toPorts, null, null);
			this.addLinkRule(rule);
		}
		System.out.println("===Finished Generating Topology===");
	}

}