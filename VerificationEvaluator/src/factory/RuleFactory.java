package factory;

import java.util.ArrayList;
import java.util.HashMap;

import bean.basis.Influence;
import bean.basis.Rule;
import interfaces.AbstractIP;

public class RuleFactory{
	public static ArrayList<Rule> ruleDecouple(ArrayList<Rule> rules){
		ArrayList<Rule> result = new ArrayList<Rule>();
		HashMap<String, ArrayList<Influence>> idToAffectedBy = new HashMap<String, ArrayList<Influence>>();
		HashMap<String, ArrayList<Rule>> idToInfluenceOn = new HashMap<String, ArrayList<Rule>>();
		return result;
	}
	public static ArrayList<Rule> ruleDecouple(HashMap<String, Rule> idToRule, HashMap<String, ArrayList<Influence>> idToAffectedBy){
		ArrayList<Rule> result = new ArrayList<Rule>();
		HashMap<String, Rule> newIdToRule = new HashMap<String, Rule>();
		for(String id: idToRule.keySet()) {
			ArrayList<Rule> ruleList = new ArrayList<Rule>();
			ruleList.add(idToRule.get(id));
			for(Influence inf:idToAffectedBy.get(id))
			{
				ArrayList<Rule> tempList = new ArrayList<Rule>();
				for(Rule calculatingRule: ruleList) {
					tempList.addAll(ruleDecoupleSingle(calculatingRule,inf));
				}
				ruleList = tempList;
			}
			for(int i = 0; i< ruleList.size(); i++) {
				ruleList.get(i).setId(ruleList.get(i).getId()+i);
				newIdToRule.put(ruleList.get(i).getId(), ruleList.get(i));
			}
			result.addAll(ruleList);
		}
		return result;
	}
	public static ArrayList<Rule> ruleDecoupleSingle(Rule rule, Influence influence){
		ArrayList<Rule> result = new ArrayList<Rule>();
		if(rule.getInPorts().equals(influence.getPorts())) {
			ArrayList<AbstractIP> minusIPs = rule.getMatch().minus(influence.getIntersect());
			for(AbstractIP minusIP:minusIPs) {
				result.add(new Rule(rule,minusIP));
			}
		}else {
			ArrayList<Integer> otherPorts = new ArrayList<Integer>();//influenced ports
			otherPorts.addAll(rule.getInPorts());
			otherPorts.removeAll(influence.getPorts());
			result.add(new Rule(rule, otherPorts));
			rule.getInPorts().removeAll(otherPorts);
			ArrayList<AbstractIP> minusIPs = rule.getMatch().minus(influence.getIntersect());
			for(AbstractIP minusIP:minusIPs) {
				result.add(new Rule(rule,minusIP));
			}
		}
			
		return result;
	}
	public static void findInfluences(ArrayList<Rule> rules, HashMap<String, ArrayList<Influence>> idToAffectedBy, HashMap<String, ArrayList<Rule>> idToInfluenceOn) {
		for(int i = 0; i< rules.size();i++) {
			findInfluences(rules, idToAffectedBy, idToInfluenceOn, i);
		}
	}
	public static void findInfluences(ArrayList<Rule> rules, HashMap<String, ArrayList<Influence>> idToAffectedBy, HashMap<String, ArrayList<Rule>> idToInfluenceOn, int position) {
		Rule newRule = rules.get(position);
		// higher position rules
		for (int i = 0; i < position; i++) {
			if (rules.get(i).getAction().equals("rw") || rules.get(i).getAction().equals("fwd")) {
				ArrayList<Integer> commonPorts = new ArrayList<Integer>();
				for (int inPort : newRule.getInPorts()) {
					if (rules.get(i).getInPorts().contains(inPort))
						;
					commonPorts.add(inPort);
				}
				AbstractIP intersect = AbstractIPFactory.generateAbstractIP(rules.get(i).getMatch());
				intersect.and(newRule.getMatch());
				if (!intersect.isEmpty() && commonPorts.size() > 0) {
					idToAffectedBy.get(newRule.getId()).add(new Influence(rules.get(i), intersect, commonPorts));
					idToInfluenceOn.get(rules.get(i).getId()).add(rules.get(position));
					// newRule.getAffectedBy().add(new
					// Influence(rules.get(i),intersect,commonPorts));
					// rules.get(i).getInfluenceOn().add(rules.get(position));
				}
			}
		}
		for (int i = position + 1; i < rules.size(); i++) {
			if (rules.get(i).getAction().equals("rw") || rules.get(i).getAction().equals("fwd")) {
				ArrayList<Integer> commonPorts = new ArrayList<Integer>();
				for (int inPort : newRule.getInPorts()) {
					if (rules.get(i).getInPorts().contains(inPort))
						;
					commonPorts.add(inPort);
				}
				AbstractIP intersect = AbstractIPFactory.generateAbstractIP(rules.get(i).getMatch());
				intersect.and(newRule.getMatch());
				if (!intersect.isEmpty() && commonPorts.size() > 0) {
					idToInfluenceOn.get(newRule.getId()).add(rules.get(i));
					idToAffectedBy.get(rules.get(i).getId())
							.add(new Influence(rules.get(position), intersect, commonPorts));
					// newRule.getInfluenceOn().add(rules.get(i));
					// rules.get(i).getAffectedBy().add(new
					// Influence(rules.get(position),intersect,commonPorts));
				}
			}
		}
	}
}