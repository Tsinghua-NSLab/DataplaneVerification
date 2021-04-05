package apverifier.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import bean.basis.Ip;
import interfaces.Header;
import apverifier.bean.APVTransFunc;

public class Atom implements Header{
	HashSet<Integer> atomIndexs = new HashSet<Integer>();
	int length = -1;
	ArrayList<String> appliedRuleIDs = new ArrayList<String>();
	ArrayList<Integer> appliedInport = new ArrayList<Integer>();
	public Atom() {
		this.length = APVTransFunc.predicates.size();
	}
	
	public Atom(int length) {
		this.length = length;
	}
	
	public Atom(Atom atom) {
		this.atomIndexs.addAll(atom.getAtomIndexs());
		this.length = atom.getLength();
	}
	
	public HashSet<Integer> getAtomIndexs(){
		return this.atomIndexs;
	}
	
	public void setAtomIndexs(HashSet<Integer> atomIndexs) {
		this.atomIndexs = atomIndexs;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	@Override
	public void setHeader(Ip ip) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void add(Header header) {
		if(header.getClass().getName()=="apverifier.bean.Atom") {
			Atom atom = (Atom)header;
			atomIndexs.addAll(atom.getAtomIndexs());
		}else {
			System.out.println("Atom add operation type unmatched");
		}
	}
	
	public void add(int index) {
		this.getAtomIndexs().add(index);
	}

	@Override
	public void and(Header header) {
		if(header.getClass().getName()=="apverifier.bean.Atom") {
			Atom atom = (Atom)header;
			HashSet<Integer> newIndexs = new HashSet<Integer>();
			for(Integer atomIndex:this.atomIndexs) {
				if(atom.getAtomIndexs().contains(atomIndex)) {
					newIndexs.add(atomIndex);
				}
			}
			this.atomIndexs = newIndexs;
		}else {
			System.out.println("Atom and operation type unmatched");
		}
	}

	@Override
	public Header copyAnd(Header header) {
		Header result = this.copy();
		result.and(header);
		return result;
	}
	
	@Override
	public BoolExpr z3Match(Context ctx, Expr pkt) {
		// TODO Auto-generated method stub
		BoolExpr result = ctx.mkBool(false);
		for(int i : this.atomIndexs) {
			result = ctx.mkOr(result, ctx.mkEq(pkt, ctx.mkInt(i)));
		}
		return result;
	}

	@Override
	public void complement() {
		HashSet<Integer> newIndexs = new HashSet<Integer>();
		for(Integer i = 0; i<length; i++) {
			if(!this.atomIndexs.contains(i)) {
				newIndexs.add(i);
			}
		}
		this.atomIndexs = newIndexs;
	}

	@Override
	public Header copyComplement() {
		Atom result = new Atom();
		result.length = this.length;
		HashSet<Integer> newIndexs = new HashSet<Integer>();
		for(Integer i = 0; i<length; i++) {
			if(!this.atomIndexs.contains(i)) {
				newIndexs.add(i);
			}
		}
		result.setAtomIndexs(newIndexs);
		return result;
	}

	@Override
	public void minus(Header header) {
		if(header.getClass().getName()=="apverifier.bean.Atom") {
			Atom atom = (Atom)header;
			HashSet<Integer> newIndexs = new HashSet<Integer>();
			for(Integer index:this.atomIndexs) {
				if(!atom.getAtomIndexs().contains(index)) {
					newIndexs.add(index);
				}
			}
			this.atomIndexs = newIndexs;
		}else {
			System.out.println("Atom minus operation type unmatched");
		}
	}

	@Override
	public Header copyMinus(Header header) {
		Header result = this.copy();
		result.minus(header);
		return result;
	}

	@Override
	public void rewrite(Header mask, Header rewrite) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void setField(HashMap<String, Integer> hsFormat, String field, long value, int rightMask) {
		// TODO Auto-generated method stub
	}

	@Override
	public Header copy() {
		Atom deepCopy = new Atom();
		deepCopy.getAtomIndexs().addAll(this.getAtomIndexs());
		return deepCopy;
	}

	@Override
	public boolean isSubsetOf(Header other) {
		if(other.getClass().getName()=="apverifier.bean.Atom") {
			Atom atom = (Atom)other;
			return this.atomIndexs.containsAll(atom.getAtomIndexs());
		}else {
			System.out.println("Atom contain operation type unmatched");
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return this.getAtomIndexs().isEmpty();
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public void pushAppliedTfRule(String ruleID, int inPort) {
		appliedRuleIDs.add(ruleID);
		appliedInport.add(inPort);
	}
	
	@Override
	public String toString() {
		String result = "(";
		for(int index: this.atomIndexs) {
			result = result+index+" ";
		}
		result = result+")";
		return result;
	}
}