package hassel.bean;

import java.util.ArrayList;

public class HS{
	//hsList: list of all wildcards included
	//hsDiff: a list of wildcard not included in the headerspace.
	ArrayList<Wildcard> hsList = new ArrayList<Wildcard>();
	ArrayList<ArrayList<Wildcard>> hsDiff = new ArrayList<ArrayList<Wildcard>>();
	int length = 0;
	//ArrayList<Rule> lazyRules;//list of (tf,rule_id,port) that has been lazy evaluated
	//ArrayList<Rule> appliedRules;//list of (tf,rule_id,port) that has been evaluated on this headerspace
	ArrayList<String> appliedDevices = new ArrayList<String>();
	ArrayList<String> appliedRuleIDs = new ArrayList<String>();
	ArrayList<Integer> appliedInport = new ArrayList<Integer>();
	
	public HS() {		
	}
	
	public HS(int length) {
		this.length = length;
	}
	
	public ArrayList<Wildcard> getHsList() {
		return hsList;
	}
	public void setHsList(ArrayList<Wildcard> hsList) {
		this.hsList = hsList;
	}
	public ArrayList<ArrayList<Wildcard>> getHsDiff() {
		return hsDiff;
	}
	public void setHsDiff(ArrayList<ArrayList<Wildcard>> hsDiff) {
		this.hsDiff = hsDiff;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public void addHS(Wildcard wc) {
		if(wc.getLength()==this.length) {
			this.hsList.add(new Wildcard(wc));
		}else {
			System.out.println("Wildcard length mismatch");
		}
	}
	public void addHS(HS hs) {
		if(hs.getLength()==this.length) {
			for(Wildcard wc: hs.getHsList()) {
				this.hsList.add(new Wildcard(wc));
			}
			for(ArrayList<Wildcard> wcList: hs.getHsDiff()) {
				ArrayList<Wildcard> tempList = new ArrayList<Wildcard>();
				for(Wildcard wc: wcList) {
					tempList.add(new Wildcard(wc));
				}
				this.hsDiff.add(tempList);
			}
		}else {
			System.out.println("HS length mismatch");
		}
	}
	
	public void addHsList(ArrayList<HS> hses) {
		for(HS hs: hses) {
			this.addHS(hs);
		}
	}
	
	public void addWcList(ArrayList<Wildcard> hses) {
		for(Wildcard hs: hses) {
			this.addHS(hs);
		}
	}
	
	public void diffHS(Wildcard wc) {
		if(wc.getLength()==this.length) {
			for(int i = 0; i< this.hsList.size();i++) {
				Wildcard selfHs = this.hsList.get(i);
				Wildcard insect = new Wildcard(wc);
				insect.and(selfHs);
				if(!insect.isEmpty()) {
					this.hsDiff.get(i).add(insect);
				}
			}
		}else {
			System.out.println("Wildcard length mismatch");
		}
	}
	
	public void diffHSList(ArrayList<Wildcard> wcs) {
		for(Wildcard wc: wcs) {
			if(wc.getLength()==this.length) {
				this.diffHS(wc);
			}else {
				System.out.println("Wildcard length mismatch");
			}
		}
	}
	
	public int count() {
		return this.hsList.size();
	}
	
	public int countDiff() {
		int total = 0;
		for(ArrayList<Wildcard> diffList: this.hsDiff) {
			total += diffList.size();
		}
		return total;
	}
	
	public HS copy() {
		HS deepCopy = new HS(this.length);
		for(int i = 0; i < this.hsList.size(); i++) {
			deepCopy.getHsList().add(new Wildcard(this.getHsList().get(i)));
		}
		for(int i = 0; i < this.hsDiff.size(); i++) {
			deepCopy.hsDiff.add(new ArrayList<Wildcard>());
			for(Wildcard wc: this.hsDiff.get(i)) {
				deepCopy.getHsDiff().get(i).add(new Wildcard(wc));
			}
		}
		return deepCopy;
	}
	
	public String toString() {
		String result = "";
		ArrayList<String> strings = new ArrayList<String>();
		for(int i = 0; i< this.hsList.size();i++) {
			String expression = "";
			expression = expression + this.hsList.get(i).getString();
			for(Wildcard wc: this.hsDiff.get(i)) {
				expression = expression + "-" + wc.getString();
			}
		}
		for(String string: strings) {
			result = "(" + result + ")+";
		}
		return result;
	}
	
	public void and(HS other) {
		if(this.length != other.getLength()) {
			System.out.println("HS length mismatch");
			return;
		}
		ArrayList<Wildcard> newHSList = new ArrayList<Wildcard>();
		ArrayList<ArrayList<Wildcard>> newHSDiff = new ArrayList<ArrayList<Wildcard>>();
		for(int i = 0; i< this.hsList.size(); i++) {
			for(int j = 0; j < other.hsList.size(); j++) {
				Wildcard isect = new Wildcard(this.hsList.get(i));
				isect.and(other.getHsList().get(j));
				if(!isect.isEmpty()) {
					newHSList.add(isect);
					ArrayList<Wildcard> diffs = new ArrayList<Wildcard>();
					for(Wildcard diffHS:this.hsDiff.get(i)) {
						Wildcard diffIsect = new Wildcard(isect);
						diffIsect.and(diffHS);
						if(!diffIsect.isEmpty()) {
							diffs.add(diffIsect);
						}
					}
					for(Wildcard diffHS:other.hsDiff.get(j)) {
						Wildcard diffIsect = new Wildcard(isect);
						diffIsect.and(diffHS);
						if(!diffIsect.isEmpty()) {
							diffs.add(diffIsect);
						}
					}
					newHSDiff.add(diffs);
				}
			}
		}
		this.hsList = newHSList;
		this.hsDiff = newHSDiff;
	}
	
	public void and(Wildcard wc) {
		if(this.length != wc.getLength()) {
			System.out.println("HS length mismatch");
			return;
		}
		ArrayList<Wildcard> newHSList = new ArrayList<Wildcard>();
		ArrayList<ArrayList<Wildcard>> newHSDiff = new ArrayList<ArrayList<Wildcard>>();
		for(int i =0; i<this.hsList.size(); i++) {
			Wildcard isect = new Wildcard(wc);
			isect.and(this.hsList.get(i));
			if(!isect.isEmpty()) {
				newHSList.add(isect);
				newHSDiff.add(new ArrayList<Wildcard>());
				for(Wildcard diffHS:this.hsDiff.get(i)) {
					Wildcard diffIsect = new Wildcard(isect);
					diffIsect.and(diffHS);
					if(!diffIsect.isEmpty()) {
						newHSDiff.get(i).add(diffIsect);
					}
				}
			}
		}
		this.hsList = newHSList;
		this.hsDiff = newHSDiff;
	}
	
	public HS copyAnd(HS other) {
		HS cpy = this.copy();
		cpy.and(other);
		return cpy;
	}
	
	public HS copyAnd(Wildcard other) {
		HS cpy = this.copy();
		cpy.and(other);
		return cpy;
	}
	
	public void complement() {
		HS result = null;
		//if empty, make it all x
		if(this.hsList.size() == 0) {
			this.hsList.add(new Wildcard(this.length,'x'));
			this.hsDiff = new ArrayList<ArrayList<Wildcard>>();
		}else {
			ArrayList<HS> cHSList = new ArrayList<HS>();
			for(int i = 0; i< this.hsList.size(); i++) {
				HS tmp = new HS(this.length);
				ArrayList<Wildcard> cSet = this.getHsList().get(i).complement();
				tmp.addWcList(cSet);
				tmp.addWcList(this.getHsDiff().get(i));
				cHSList.add(tmp);
			}
			result = cHSList.get(0);
			for(int i = 1; i< cHSList.size();i++) {
				result.and(cHSList.get(i));
			}
		}
		this.hsList = result.getHsList();
		this.hsDiff = result.getHsDiff();
	}
	
	public HS copyComplement() {
		HS cpy = this.copy();
		cpy.complement();
		return cpy;
	}
	
	public void minus(HS other) {
		HS cpy = other.copyComplement();
		this.and(cpy);
		this.cleanUp();
	}
	
	public HS copyMinus(HS other) {
		HS cpy = this.copy();
		cpy.minus(other);
		return cpy;
	}
	
	public void selfDiff() {
		if(this.hsDiff.size() == 0) {
			return;
		}
		ArrayList<Wildcard> newHSList = new ArrayList<Wildcard>();
		for(int i = 0; i< this.getHsList().size();i++) {
			HS incs = new HS(this.length);
			incs.addHS(this.getHsList().get(i));
			HS difs = new HS(this.length); 
			difs.addWcList(this.getHsDiff().get(i));
			incs.minus(difs);
			newHSList.addAll(incs.getHsList());
		}
		this.hsDiff.clear();
		this.hsList.clear();
		this.addWcList(newHSList);
	}
	
	public boolean isEmpty() {
		return this.getHsList().size() == 0;
	}
	
	/**
	 * mathmatically
	 * @return
	 */
	public boolean isSubsetOf(HS other) {
		HS cpy = this.copy();
		cpy.minus(other);
		cpy.selfDiff();
		return cpy.isEmpty();
	}
	
	/**
	 * literally
	 * @param other
	 * @return
	 */
	public boolean isContainedIn(HS other) {
		//TODO logically error
		for(int i = 0; i < this.getHsList().size(); i++) {
			Wildcard h1 = this.getHsList().get(i);
			boolean foundHsEq = false;
			for(int j = 0; j< other.getHsList().size(); j++) {
				Wildcard h2 = other.getHsList().get(j);
				if(h1.equals(h2)) {
					foundHsEq = true;
					for(Wildcard d1:other.hsDiff.get(j)) {
						boolean foundDiffEq = false;
						for(Wildcard d2:this.hsDiff.get(i)) {
							if(d1.equals(d2)) {
								foundDiffEq = true;
								break;
							}
						}
						if(!foundDiffEq) {
							return false;
						}
					}
				}
			}
			if(!foundHsEq) {
				return false;
			}
		}
		return true;
	}
	
	public void cleanUp() {
		ArrayList<Wildcard> newHSList = new ArrayList<Wildcard>();
		ArrayList<ArrayList<Wildcard>> newHSDiff = new ArrayList<ArrayList<Wildcard>>();
		//removes all objects in hs_list that will subtract out to empty
		for(int i = 0; i< this.hsList.size();i++) {
			boolean flag = false;
			for(Wildcard dh: this.hsDiff.get(i)) {
				if(dh.contains(this.getHsList().get(i))) {
					flag = true;
				}
			}
			if(!flag) {
				newHSList.add(this.getHsList().get(i));
				newHSDiff.add(Wildcard.compressWildCardList(this.getHsDiff().get(i)));
			}
		}
		this.hsList = newHSList;
		this.hsDiff = newHSDiff;
	}
	
	public void pushAppliedTfRule(String rulsID, int inPort) {
		appliedRuleIDs.add(rulsID);
		appliedInport.add(inPort);
		
	}
}