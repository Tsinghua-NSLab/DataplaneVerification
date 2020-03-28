package tfFunction;

import java.util.ArrayList;
import java.util.HashMap;

import bean.Node;
import hassel.bean.Wildcard;
import rules.Influence;
import rules.Rule;

public class tfFunction{
	
	int nextID = 0;
	String prefixID = "";
	ArrayList<Rule> rules = new ArrayList<Rule>();
	HashMap<Integer, ArrayList<Rule>> inportToRule= new HashMap<Integer, ArrayList<Rule>>();
	HashMap<Integer, ArrayList<Rule>> outportToRule= new HashMap<Integer, ArrayList<Rule>>();
	HashMap<String, Rule> idToRule = new HashMap<String,Rule>();
	boolean hashTableActive = false;
	
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
		}else {
			rules.add(position, rule);
		}
		this.findInfluences(position);
		this.setFastLookupPointers(position);
	}
	
	public void addRewriteRule(Rule rule) {
		addRewriteRule(rule,-1);
	}
	public void addRewriteRule(Rule rule, int position) {
		//Mask rewrite
		Wildcard temp = new Wildcard(rule.getMask());
		temp.not();
		rule.getRewrite().and(temp);
		rule.setAction("rw");
		//Finding inverse
		rule.setInverseMatch(new Wildcard(rule.getMask()));
		rule.getInverseMatch().and(rule.getMatch());
		rule.getInverseMatch().or(rule.getRewrite());
		rule.setInverseRewrite(new Wildcard(rule.getMask()));
		rule.getInverseRewrite().not();
		rule.getInverseRewrite().and(rule.getMatch());
		//Setting up id
		rule.setId(this.generateNextID());
		//Inserting rule at correct position
		if(position == -1 || position>this.rules.size()) {
			rules.add(rule);
			position = rules.size()-1;
		}else {
			rules.add(position, rule);
		}
		//Setting up fast lookups and influences
		this.findInfluences(position);
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
				Wildcard intersect = new Wildcard(rules.get(i).getMatch());
				intersect.and(newRule.getMatch());
				if(!intersect.isEmpty()&&commonPorts.size()>0) {
					newRule.getAffectedBy().add(new Influence(rules.get(i),intersect,commonPorts));
					rules.get(i).getInfluenceOn().add(rules.get(position));
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
				Wildcard intersect = new Wildcard(rules.get(i).getMatch());
				intersect.and(newRule.getMatch());
				if(!intersect.isEmpty()&&commonPorts.size()>0) {
					newRule.getInfluenceOn().add(rules.get(i));
					rules.get(i).getAffectedBy().add(new Influence(rules.get(position),intersect,commonPorts));
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

}