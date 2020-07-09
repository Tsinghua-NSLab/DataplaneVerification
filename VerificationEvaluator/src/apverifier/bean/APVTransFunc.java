package apverifier.bean;

import java.util.ArrayList;
import java.util.HashMap;

import bean.basis.BasicTF;
import bean.basis.Influence;
import bean.basis.Rule;
import interfaces.Header;

public class APVTransFunc{
	ArrayList<Predicate> predicates = new ArrayList<Predicate>();
	BasicTF TF;
	//TODO use hashmap to replace arrayList?
	public int nextID = 0;
	public static int length = 3;
	
	public static void transferBasicTF(BasicTF TF) {
		new APVTransFunc(TF);
	}
	
	public APVTransFunc(BasicTF TF) {
		this.TF = TF;
		generatePredicates();
		setRules();
		System.out.print("");
	}
	public int generateNextID() {
		return nextID++;
	}
	
	public void setLength(int length) {
		APVTransFunc.length = predicates.size();
	}
	
	public void generatePredicates() {
		for(String key: TF.getIdToRule().keySet())
		{		
			Rule rule = TF.getIdToRule().get(key); 
			if(rule.getAction()=="fwd") {
				Predicate temp = new Predicate(rule.getMatch());
				temp.getContainedRuleIDs().add(key);
				addPredicate(temp);
			}
			//TODO link
			//TODO rewrite
		}
	}
	
	public void setRules() {
		HashMap<String, Atom> tempHeaders = new HashMap<String, Atom>();
		for(String key: TF.getIdToRule().keySet()) {
			if(TF.getIdToRule().get(key).getAction()=="fwd") {
				tempHeaders.put(key, new Atom(length));
			}
		}
		for(int i = 0; i<predicates.size() ; i++) {
			for(String key:predicates.get(i).getContainedRuleIDs()) {
				tempHeaders.get(key).getAtomIndexs().add(i);
			}
		}
		for(String key: tempHeaders.keySet()) {
			TF.getIdToRule().get(key).setMatch(tempHeaders.get(key));
		}
		if(!TF.isUncovered) {
			for(String key: TF.idToAffectedBy.keySet()){
				for(Influence influence: TF.idToAffectedBy.get(key)) {
					influence.setIntersect(headerToAtom(influence.getIntersect()));
				}
			}
		}
	}
	
	public Atom headerToAtom(Header header) {
		if(header.getClass().getName()=="apverifier.bean.Atom") {
			System.out.println("noNeedToChange");
		}else {
			Atom result = new Atom(length);
			for(int i = 0; i < predicates.size(); i++) {
				Predicate predicate = predicates.get(i);
				Header temp = header.copyAnd(predicate.getHeader());
				if(!temp.isEmpty()) {
					result.getAtomIndexs().add(i);
				}
			}
			return result;
		}
		return null;
	}
	
	public void addPredicate(Predicate predicate) {
		//Use formula (3) to calculate predicates
		if(predicate.isEmpty()) {
			return;
		}else if(predicates.isEmpty()) {
			Predicate temp = new Predicate(predicate);
			temp.setId(generateNextID());
			Predicate tempComp = temp.complement();
			tempComp.setId(generateNextID());
			predicates.add(temp);
			predicates.add(tempComp);
		}else{
			Predicate temp = new Predicate(predicate);
			Predicate tempComp = temp.complement();
			ArrayList<Predicate> newPredicates = new ArrayList<Predicate>();
			for(Predicate existPredi: predicates) {
				//A^!B
				Predicate comp = tempComp.copyAnd(existPredi);
				if(!comp.isEmpty()) {
					comp.setId(generateNextID());
					newPredicates.add(comp);
				}
				//A^B
				existPredi.and(temp);
				if(!existPredi.isEmpty()) {
					newPredicates.add(existPredi);
				}
			}
			predicates = newPredicates;
		}
	}
}