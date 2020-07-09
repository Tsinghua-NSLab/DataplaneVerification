package bean.basis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import bean.Link;
import bean.Network;
import bean.basis.Influence;
import bean.basis.Node;
import bean.basis.Rule;
import factory.AbstractIPFactory;
import interfaces.AbstractIP;
//import hassel.bean.HS;
//import hassel.bean.Wildcard;
import interfaces.Header;
import interfaces.Parser;
import interfaces.TransferFunc;

public class BasicTF implements Serializable{	
	public int nextID = 0;
	public String prefixID = "";
	public ArrayList<Rule> rules = new ArrayList<Rule>();
	public HashMap<String, Parser> parserList = new HashMap<String, Parser>();
	public HashMap<Integer, ArrayList<Rule>> inportToRule= new HashMap<Integer, ArrayList<Rule>>();
	public HashMap<Integer, ArrayList<Rule>> outportToRule= new HashMap<Integer, ArrayList<Rule>>();
	public HashMap<String, Rule> idToRule = new HashMap<String,Rule>();
	public HashMap<String, ArrayList<Influence>> idToAffectedBy = new HashMap<String, ArrayList<Influence>>();
	public HashMap<String, ArrayList<Rule>> idToInfluenceOn = new HashMap<String, ArrayList<Rule>>();
	public boolean hashTableActive = false;
	public boolean sendOnReceivingPort = false;
	public boolean isUncovered = false;
	
	/**
	 * Generate Rule ID
	 * @return
	 */
	public String generateNextID() {
		nextID++;
		return prefixID+nextID;
	}
	
	public ArrayList<Rule> getRules(){
		return this.rules;
	}
	
	public HashMap<String, Rule> getIdToRule(){
		return this.idToRule;
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
		//TODO need test
		rule.setInverseMatch(rule.getMask().copy());
		rule.getInverseMatch().and(rule.getMatch());
		rule.getInverseMatch().add(rule.getRewrite());
		rule.setInverseRewrite(rule.getMask().copy());
		rule.getInverseRewrite().complement();;
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
					if(rules.get(i).getInPorts().contains(inPort)) {
						commonPorts.add(inPort);
					}
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
		ArrayList<Rule> testInfluences = this.idToInfluenceOn.get(newRule.getId());
		ArrayList<Influence> testAffectedBy = this.idToAffectedBy.get(newRule.getId());
		System.out.print("");
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
		System.out.print("");
		//TODO: hash table set up
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
	
	//for uncovered rules
	public void ruleDecouple(){
		ArrayList<Rule> result = new ArrayList<Rule>();
		HashMap<String, Rule> newIdToRule = new HashMap<String, Rule>();
		for(String id: idToRule.keySet()) {
			ArrayList<Rule> ruleList = new ArrayList<Rule>();
			ruleList.add(idToRule.get(id));
			for(Influence inf: idToAffectedBy.get(id)) {
				ArrayList<Rule> tempList = new ArrayList<Rule>();
				for(Rule calculatingRule: ruleList) {
					tempList.addAll(ruleDecoupleSingle(calculatingRule,inf));
				}
				ruleList = tempList;
			}
			for(int i = 0; i < ruleList.size(); i++) {
				ruleList.get(i).setId(ruleList.get(i).getId()+i);
				newIdToRule.put(ruleList.get(i).getId(), ruleList.get(i));
			}
			result.addAll(ruleList);
		}
		rules = result;
		idToRule = newIdToRule;
		isUncovered = true;
		//inport and outport
		this.inportToRule.clear();
		this.outportToRule.clear();
		for(Rule rule:rules) {
			for(int inport:rule.getInPorts()) {
				if(!this.inportToRule.containsKey(inport)) {
					this.inportToRule.put(inport, new ArrayList<Rule>());
				}
				this.inportToRule.get(inport).add(rule);
			}
			for(int outport:rule.getOutPorts()) {
				if(!this.outportToRule.containsKey(outport)) {
					this.outportToRule.put(outport, new ArrayList<Rule>());
				}
				this.outportToRule.get(outport).add(rule);
			}
		}
		this.idToAffectedBy.clear();
		this.idToInfluenceOn.clear();
		for(int i = 0; i < this.rules.size(); i++) {
			this.rules.get(i).match.cleanUp();
		}
	}
	
	public ArrayList<Rule> ruleDecoupleSingle(Rule rule, Influence influence){
		ArrayList<Rule> result = new ArrayList<Rule>();
		if(rule.getInPorts().equals(influence.getPorts())) {
			Header minusIP = rule.getMatch().copyMinus(influence.getIntersect());
			result.add(new Rule(rule,minusIP));
		}else {
			ArrayList<Integer> otherPorts = new ArrayList<Integer>();
			otherPorts.addAll(rule.getInPorts());
			otherPorts.removeAll(influence.getPorts());
			result.add(new Rule(rule, otherPorts));
			rule.getInPorts().removeAll(otherPorts);
			Header minusIP = rule.getMatch().copyMinus(influence.getIntersect());
			result.add(new Rule(rule,minusIP));
		}
		return result;
	}
}