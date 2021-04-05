package hassel.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import bean.basis.Ip;
import factory.AbstractIPFactory;
import interfaces.AbstractIP;
import interfaces.Header;

import com.microsoft.z3.*;

public class HS implements Header,Serializable{
	//hsList: list of all wildcards included
	//hsDiff: a list of wildcard not included in the headerspace.
	ArrayList<AbstractIP> hsList = new ArrayList<AbstractIP>();
	ArrayList<ArrayList<AbstractIP>> hsDiff = new ArrayList<ArrayList<AbstractIP>>();
	int length = 0;
	//ArrayList<Rule> lazyRules;//list of (tf,rule_id,port) that has been lazy evaluated
	//ArrayList<Rule> appliedRules;//list of (tf,rule_id,port) that has been evaluated on this headerspace
	//ArrayList<String> appliedDevices = new ArrayList<String>();
	ArrayList<String> appliedRuleIDs = new ArrayList<String>();
	ArrayList<Integer> appliedInport = new ArrayList<Integer>();
	
	public HS() {		
	}
	
	public HS(int length) {
		this.length = length;
	}
	
	public ArrayList<AbstractIP> getHsList() {
		return hsList;
	}
	public void setHsList(ArrayList<AbstractIP> hsList) {
		this.hsList = hsList;
	}
	public ArrayList<ArrayList<AbstractIP>> getHsDiff() {
		return hsDiff;
	}
	public void setHsDiff(ArrayList<ArrayList<AbstractIP>> hsDiff) {
		this.hsDiff = hsDiff;
	}
	@Override
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	@Override
	public void setHeader(Ip ip) {
		
	}
	
	public void add(AbstractIP wc) {
		//if(abstractIP.getClass().getName()=="hassel.bean.Wildcard"){
			//Wildcard wc = (Wildcard)abstractIP;
		if(wc.getLength()==this.length) {
			this.hsList.add(AbstractIPFactory.generateAbstractIP(wc));
			this.hsDiff.add(new ArrayList<AbstractIP>());
		}else {
			System.out.println("Wildcard length mismatch");
		}
		//}else {
			//System.out.println("HS add operation type unmatched.");
		//}
	}
	
	@Override
	public void add(Header header) {
		if(header.getClass().getName()=="hassel.bean.HS") {
			HS hs = (HS)header;
			if(hs.getLength()==this.length) {
				for(AbstractIP wc: hs.getHsList()) {
					this.hsList.add(AbstractIPFactory.generateAbstractIP(wc));
				}
				for(ArrayList<AbstractIP> wcList: hs.getHsDiff()) {
					ArrayList<AbstractIP> tempList = new ArrayList<AbstractIP>();
					for(AbstractIP wc: wcList) {
						tempList.add(AbstractIPFactory.generateAbstractIP(wc));
					}
					this.hsDiff.add(tempList);
				}
			}else {
				System.out.println("HS length mismatch");
			}
		}else {
			System.out.println("HS add operation type unmatched.");
		}
	}
	
	public void addHsList(ArrayList<Header> hses) {
		for(Header hs: hses) {
			this.add(hs);
		}
	}
	
	public void addWcList(ArrayList<AbstractIP> hses) {
		for(AbstractIP hs: hses) {
			this.add(hs);
		}
	}
	
	public void diffHS(AbstractIP wc) {
		if(wc.getLength()==this.length) {
			for(int i = 0; i< this.hsList.size();i++) {
				AbstractIP selfHs = this.hsList.get(i);
				AbstractIP insect = AbstractIPFactory.generateAbstractIP(wc);
				insect.and(selfHs);
				if(!insect.isEmpty()) {
					this.hsDiff.get(i).add(insect);
				}
			}
		}else {
			System.out.println("Wildcard length mismatch");
		}
	}
	
	public void diffHSList(ArrayList<AbstractIP> wcs) {
		for(AbstractIP wc: wcs) {
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
		for(ArrayList<AbstractIP> diffList: this.hsDiff) {
			total += diffList.size();
		}
		return total;
	}
	
	@Override
	public Header copy() {
		HS deepCopy = new HS(this.length);
		for(int i = 0; i < this.hsList.size(); i++) {
			deepCopy.getHsList().add(AbstractIPFactory.generateAbstractIP(this.getHsList().get(i)));
		}
		for(int i = 0; i < this.hsDiff.size(); i++) {
			deepCopy.hsDiff.add(new ArrayList<AbstractIP>());
			for(AbstractIP wc: this.hsDiff.get(i)) {
				deepCopy.getHsDiff().get(i).add(AbstractIPFactory.generateAbstractIP(wc));
			}
		}
		deepCopy.appliedRuleIDs.addAll(this.appliedRuleIDs);
		deepCopy.appliedInport.addAll(this.appliedInport);
		return deepCopy;
	}
	
	@Override
	public String toString() {
		String result = "";
		for(int i = 0; i< this.hsList.size();i++) {
			String expression = "";
			expression = expression + this.hsList.get(i).getString();
			for(AbstractIP wc: this.hsDiff.get(i)) {
				expression = expression + "-" + wc.getString();
			}
			result = result + "(" + expression + ")";
		}
		return result;
	}
	
	@Override
	public void and(Header header) {
		if(header.getClass().getName()=="hassel.bean.HS") {
			HS other = (HS)header;
			if(this.length != other.getLength()) {
				System.out.println("HS length mismatch");
				return;
			}
			ArrayList<AbstractIP> newHSList = new ArrayList<AbstractIP>();
			ArrayList<ArrayList<AbstractIP>> newHSDiff = new ArrayList<ArrayList<AbstractIP>>();
			for(int i = 0; i< this.hsList.size(); i++) {
				for(int j = 0; j < other.hsList.size(); j++) {
					AbstractIP isect = AbstractIPFactory.generateAbstractIP(this.hsList.get(i));
					isect.and(other.getHsList().get(j));
					if(!isect.isEmpty()) {
						newHSList.add(isect);
						ArrayList<AbstractIP> diffs = new ArrayList<AbstractIP>();
						for(AbstractIP diffHS:this.hsDiff.get(i)) {
							AbstractIP diffIsect = AbstractIPFactory.generateAbstractIP(isect);
							diffIsect.and(diffHS);
							if(!diffIsect.isEmpty()) {
								diffs.add(diffIsect);
							}
						}
						for(AbstractIP diffHS:other.hsDiff.get(j)) {
							AbstractIP diffIsect = AbstractIPFactory.generateAbstractIP(isect);
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
		this.cleanUp();
	}
	
	public void and(AbstractIP wc) {
		if(this.length != wc.getLength()) {
			System.out.println("HS length mismatch");
			return;
		}
		ArrayList<AbstractIP> newHSList = new ArrayList<AbstractIP>();
		ArrayList<ArrayList<AbstractIP>> newHSDiff = new ArrayList<ArrayList<AbstractIP>>();
		for(int i =0; i<this.hsList.size(); i++) {
			AbstractIP isect = AbstractIPFactory.generateAbstractIP(wc);
			isect.and(this.hsList.get(i));
			if(!isect.isEmpty()) {
				newHSList.add(isect);
				newHSDiff.add(new ArrayList<AbstractIP>());
				for(AbstractIP diffHS:this.hsDiff.get(i)) {
					AbstractIP diffIsect = AbstractIPFactory.generateAbstractIP(isect);
					diffIsect.and(diffHS);
					if(!diffIsect.isEmpty()) {
						newHSDiff.get(i).add(diffIsect);
					}
				}
			}
		}
		this.hsList = newHSList;
		this.hsDiff = newHSDiff;
		this.cleanUp();
		System.out.print("");
	}
	
	@Override
	public BoolExpr z3Match(Context ctx, Expr pkt) {
		BoolExpr result = ctx.mkBool(false);
		for(int i = 0; i < this.getHsList().size(); i++) {
			AbstractIP tempIP = this.getHsList().get(i);
			BoolExpr tempExpr = tempIP.z3Match(ctx, pkt);
			for(AbstractIP tempDiffIP : this.getHsDiff().get(i)) {
				tempExpr = ctx.mkAnd(tempExpr, ctx.mkNot(tempDiffIP.z3Match(ctx, pkt)));
			}
			result = ctx.mkOr(result, tempExpr);
		}
		return result;
	}
	
	@Override
	public Header copyAnd(Header other) {
		Header cpy = this.copy();
		cpy.and(other);
		return cpy;
	}
	
	public HS copyAnd(AbstractIP other) {
		HS cpy = (HS)this.copy();
		cpy.and(other);
		return cpy;
	}
	@Override
	public void complement() {
		HS result = null;
		//if empty, make it all x
		if(this.hsList.size() == 0) {
			this.hsList.add(AbstractIPFactory.generateAbstractIP(this.length,'x'));
			this.hsDiff = new ArrayList<ArrayList<AbstractIP>>();
			this.hsDiff.add(new ArrayList<AbstractIP>());
		}else {
			ArrayList<HS> cHSList = new ArrayList<HS>();
			for(int i = 0; i< this.hsList.size(); i++) {
				HS tmp = new HS(this.length);
				ArrayList<AbstractIP> cSet = this.getHsList().get(i).complement();
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
	@Override
	public Header copyComplement() {
		Header cpy = this.copy();
		cpy.complement();
		return cpy;
	}
	@Override
	public void minus(Header other) {
		Header cpy = other.copyComplement();
		this.and(cpy);
		this.cleanUp();
	}
	@Override
	public Header copyMinus(Header other) {
		Header cpy = this.copy();
		cpy.minus(other);
		return cpy;
	}
	
	@Override
	public void rewrite(Header mask, Header rewrite) {
		if((mask.getClass().getName()=="hassel.bean.HS")&&(rewrite.getClass().getName()=="hassel.bean.HS")) {
			HS maskHS = (HS)mask;
			HS rewriteHS = (HS)rewrite;
			if(!(maskHS.getLength()==1&&rewriteHS.getLength()==1)) {
				System.out.println("Warning, mask and rewrite may not correct");
			}
			AbstractIP maskWC = maskHS.getHsList().get(0);
			AbstractIP rewriteWC = rewriteHS.getHsList().get(0);
			for(int i = 0; i<this.getLength();i++) {
				int card = this.getHsList().get(i).rewrite(maskWC, rewriteWC);
				ArrayList<AbstractIP> newDiffList = new ArrayList<AbstractIP>();
				for(int j = 0; j< this.getHsDiff().get(i).size();j++) {
					int diffCard = this.getHsDiff().get(i).get(j).rewrite(maskWC, rewriteWC);
					if(diffCard==card) {
						newDiffList.add(this.getHsDiff().get(i).get(j));
					}
				}
				this.getHsDiff().set(i, newDiffList);
			}
			this.cleanUp();
		}else {
			System.out.println("Error, Header type unmatch");
			return;
		}
	}
	
	@Override 
	public void setField(HashMap<String, Integer> hsFormat, String field, long value, int rightMask) {
		for(AbstractIP abstractIP: hsList) {
			abstractIP.setField(hsFormat, field, value, rightMask);
		}
		for(ArrayList<AbstractIP> abstractIPs: hsDiff) {
			for(AbstractIP abstractIP: abstractIPs) {
				abstractIP.setField(hsFormat, field, value, rightMask);
			}
		}
	}
	
	public void selfDiff() {
		if(this.hsDiff.size() == 0) {
			return;
		}
		ArrayList<AbstractIP> newHSList = new ArrayList<AbstractIP>();
		for(int i = 0; i< this.getHsList().size();i++) {
			HS incs = new HS(this.length);
			incs.add(this.getHsList().get(i));
			HS difs = new HS(this.length); 
			difs.addWcList(this.getHsDiff().get(i));
			incs.minus(difs);
			newHSList.addAll(incs.getHsList());
		}
		this.hsDiff.clear();
		this.hsList.clear();
		this.addWcList(newHSList);
	}
	@Override
	public boolean isEmpty() {
		return this.getHsList().size() == 0;
	}
	
	/**
	 * mathmatically
	 * @return
	 */
	@Override
	public boolean isSubsetOf(Header other) {
		HS cpy = (HS)this.copy();
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
		for(int i = 0; i < this.getHsList().size(); i++) {
			AbstractIP h1 = this.getHsList().get(i);
			boolean foundHsEq = false;
			for(int j = 0; j< other.getHsList().size(); j++) {
				AbstractIP h2 = other.getHsList().get(j);
				if(h1.equals(h2)) {
					foundHsEq = true;
					for(AbstractIP d1:other.hsDiff.get(j)) {
						boolean foundDiffEq = false;
						for(AbstractIP d2:this.hsDiff.get(i)) {
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
		ArrayList<AbstractIP> newHSList = new ArrayList<AbstractIP>();
		ArrayList<ArrayList<AbstractIP>> newHSDiff = new ArrayList<ArrayList<AbstractIP>>();
		//removes all objects in hs_list that will subtract out to empty
		for(int i = 0; i< this.hsList.size();i++) {
			boolean flag = false;
			for(AbstractIP dh: this.hsDiff.get(i)) {
				if(dh.contains(this.getHsList().get(i))) {
					flag = true;
				}
			}
			if(!flag) {
				newHSList.add(this.getHsList().get(i));
				newHSDiff.add(AbstractIP.compressList(this.getHsDiff().get(i)));
			}
		}
		this.hsList = newHSList;
		this.hsDiff = newHSDiff;
		//Simple merge
		newHSList = new ArrayList<AbstractIP>();
		newHSDiff = new ArrayList<ArrayList<AbstractIP>>();
		for(int i = 0; i< this.hsList.size(); i++) {
			boolean flag = false;
			for(int j = 0; j < this.hsList.size(); j++) {
				if(i == j)continue;
				boolean diffFlag = false;
				for(AbstractIP iDiff: this.getHsDiff().get(i)) {
					for(AbstractIP jDiff: this.getHsDiff().get(j)) {
						if(!iDiff.contains(jDiff)) {
							diffFlag = true;
						}
					}
				}
				if(diffFlag) {
					continue;
				}
				if(this.getHsList().get(j).contains(this.getHsList().get(i))) {
					if((!this.getHsList().get(i).equals(this.getHsList().get(j)))||j<i) {
					//if(j<i) {
						flag = true;
						break;
					}
				}
			}
			if(!flag) {
				newHSList.add(this.getHsList().get(i));
				newHSDiff.add(this.getHsDiff().get(i));
			}
		}
		this.hsList = newHSList;
		this.hsDiff = newHSDiff;
	}
	
	public void pushAppliedTfRule(String ruleID, int inPort) {
		appliedRuleIDs.add(ruleID);
		appliedInport.add(inPort);
		
	}
}