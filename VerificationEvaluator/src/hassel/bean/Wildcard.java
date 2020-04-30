package hassel.bean;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import interfaces.AbstractIP;

public class Wildcard implements AbstractIP{
	int length = 0;
	BitSet wcBit = new BitSet();
	
	public Wildcard() {
//		length = 0;
//		wcString = "";
//		wcBit = new BitSet();
	}
	
	public Wildcard(Wildcard wc) {
		length = wc.length;
		wcBit.or(wc.wcBit);
	}
	
	public Wildcard(int Length) {
		length = Length;
		wcBit.set(0, 2*length);
	}
	
	public Wildcard(String wcString) {
		length = wcString.length();
		wcBit.clear();
		for(int i = 0; i < length; i++) {
			if(wcString.charAt(length-i-1)=='x') {
				wcBit.set(2*i);
				wcBit.set(2*i+1);
			}else if(wcString.charAt(length-i-1)=='1') {
				wcBit.set(2*i+1);
			}else if(wcString.charAt(length-i-1)=='0') {
				wcBit.set(2*i);
			}
		}
	}
	
	public Wildcard(int Length, int value) {
		length = Length;
		for(int i = 0; i< Length; i++) {
			wcBit.set(2*i + (value%2));
			value = value>>1;
		}
	}
	
	public Wildcard(int Length, char Bit) {
	//0: all z, 1: all 0, 2: all 1, 3: all x
		length = Length;
		if(Bit == 'x') {
			wcBit.set(0, 2*length);
		}else if(Bit == '0') {
			for(int i = 0; i< length;i++) {
				wcBit.set(2*i);
			}
		}else if(Bit == '1') {
			for(int i = 0; i< length;i++) {
				wcBit.set(2*i+1);
			}
		}else {
			wcBit.clear();
		}
	}
	
	@Override
	public void setMask(int rightMask) {
		wcBit.set(2*(length-rightMask), 2*length);
	}
	
	@Override
	public void setField(HashMap<String,Integer> hsFormat, String field, int value, int rightMask) {
		//TODO need test
		int fieldLength = hsFormat.get(field+"_len");
		int startPos = hsFormat.get(field + "_pos");
		for(int i = startPos; i<startPos+fieldLength-rightMask; i++) {
			wcBit.set(2*i + (value%2));
			wcBit.clear(2*i + ((value%2)^0x1));
			value = value>>>1;
		}
		wcBit.set(2*(startPos+fieldLength-rightMask), 2*(startPos+fieldLength));
	}
	
	@Override
	public void and(AbstractIP other) {
		if(other.getClass().getName()=="hassel.bean.Wildcard") {
			Wildcard otherWC = (Wildcard)other;
			if(this.length == otherWC.length) {
				this.wcBit.and(otherWC.wcBit);
			}else {
				System.out.println("Error: length mismatch");
			}
		}else {
			System.out.println("hassel.bean.Wildcard: bit operation type mismatch:" + other.getClass().getName());
		}
	}
	
	@Override
	public void or(AbstractIP other) {
		if(other.getClass().getName()=="hassel.bean.Wildcard") {
			Wildcard otherWC = (Wildcard)other;
			if(this.length == otherWC.length) {
				this.wcBit.or(otherWC.wcBit);
			}else {
				System.out.println("Error: length mismatch");
			}
		}else {
			System.out.println("hassel.bean.Wildcard: bit operation type mismatch:" + other.getClass().getName());
		}
	}
	
	@Override
	public void xor(AbstractIP other) {
		if(other.getClass().getName()=="hassel.bean.Wildcard") {
			Wildcard otherWC = (Wildcard)other;
			if(this.length == otherWC.length) {
				this.wcBit.xor(otherWC.getWcBit());
			}else {
				System.out.println("Error: length mismatch");
			}
		}else {
			System.out.println("hassel.bean.Wildcard: bit operation type mismatch:" + other.getClass().getName());
		}
	}
	
	@Override
	public void not() {
		this.wcBit.flip(0, length);
	}
	
	@Override
	public boolean equals(AbstractIP other) {
		Wildcard tmp = new Wildcard(this);
		tmp.xor(other);
		return tmp.getWcBit().isEmpty();
	}
	
	@Override
	public boolean contains(AbstractIP other) {
		Wildcard tmp = new Wildcard(this);
		tmp.and(other);
		tmp.xor(other);
		return tmp.getWcBit().isEmpty();
	}
	
	@Override
	public int getLength() {
		return length;
	}
	
	@Override
	public void setLength(int length) {
		this.length = length;
	}
	public BitSet getWcBit() {
		return wcBit;
	}
	public void setWcBit(BitSet wcBit) {
		this.wcBit = wcBit;
	}
	
	@Override
	public boolean isEmpty() {
		for(int i = 0; i< this.length;i++) {
			if(!(wcBit.get(2*i)||wcBit.get(2*i+1))) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getString() {
		String result = "";
		for(int i = 0; i < this.length; i++) {
			if(this.wcBit.get(2*i)) {
				if(this.wcBit.get(2*i+1)) {
					result = "x" + result;
				}else {
					result = "0" + result;
				}
			}else {
				if(this.wcBit.get(2*i+1)) {
					result = "1" + result;
				}else {
					result = "z" + result;
				}
			}
		}
		return result;
	}
	
	@Override
	public ArrayList<AbstractIP> complement(){
		ArrayList<AbstractIP> result = new ArrayList<AbstractIP>();
		for(int i = 0; i < this.length;i++) {
			if(this.getWcBit().get(2*i)) {
				if(this.getWcBit().get(2*i+1)) {
					continue;
				}else {
					Wildcard tmp = new Wildcard(this.length, 'x');
					tmp.getWcBit().clear(2*i);
					result.add(tmp);
				}
			}else {
				if(this.getWcBit().get(2*i+1)) {
					Wildcard tmp = new Wildcard(this.length, 'x');
					tmp.getWcBit().clear(2*i+1);
					result.add(tmp);
				}else {
					result = new ArrayList<AbstractIP>();
					result.add(new Wildcard(this.length,'x'));
				}
			}
		}
		return result;
	}
	
	@Override 
	public ArrayList<AbstractIP> minus(AbstractIP other){
		ArrayList<AbstractIP> result = new ArrayList<AbstractIP>();
		AbstractIP temp = new Wildcard(this.length);
		temp.or(this);
		temp.and(other);
		if(temp.isEmpty()) {
			result.add(this);
		}else {
			ArrayList<AbstractIP> otherComples = other.complement();
			for(AbstractIP otherComple: otherComples) {
				otherComple.and(this);
				if(!otherComple.isEmpty()) {
					result.add(otherComple);
				}
			}
		}
		return result;
	}
	
	@Override
	public int rewrite(AbstractIP mask, AbstractIP rewrite) {
		if(mask.getClass().getName()=="hassel.bean.Wildcard"&&rewrite.getClass().getName()=="hassel.bean.Wildcard") {
			Wildcard maskWC = (Wildcard)mask;
			Wildcard rewriteWC = (Wildcard)rewrite;
			int result = 0;
			BitSet tmp1 = new BitSet();
			tmp1.or(wcBit);
			BitSet tmp2 = new BitSet();
			tmp2.or(wcBit);
			BitSet oddMask = new BitSet();
			BitSet evenMask = new BitSet();
			for(int i = 0; i < length; i++) {
				evenMask.set(2*i+1);
			}
			oddMask.set(0, 2*length);
			oddMask.xor(evenMask);
			tmp1.or(maskWC.getWcBit());
			tmp1.and(rewriteWC.getWcBit());
			tmp1.and(oddMask);
			tmp2.and(maskWC.getWcBit());
			tmp2.or(maskWC.getWcBit());
			tmp2.and(evenMask);
			tmp1.or(tmp2);
			wcBit=tmp1;
			for(int i = 0; i<length;i++) {
				if(wcBit.get(2*i)&&wcBit.get(2*i+1)) {
				result++;
				}
			}
			return result;
		}else {
			System.out.println("hassel.bean.Wildcard: bit operation type mismatch:" + mask.getClass().getName() +","+ rewrite.getClass().getName());
		}
		return -1;
	}
	
	@Override
	public String toString() {
		return getString();
	}
	
	/*public static ArrayList<Wildcard> compressWildCardList(ArrayList<Wildcard> l) {
		ArrayList<Integer> popIndex = new ArrayList<Integer>();
		for(int i = 0; i< l.size(); i++) {
			for(int j = i+1; j < l.size(); j++) {
				if(l.get(i).contains(l.get(j))) {
					popIndex.add(j);
				}
				if(l.get(j).contains(l.get(i))) {
					popIndex.add(i);
				}
			}
		}
		ArrayList<Wildcard> result = new ArrayList<Wildcard>();
		for(int i = 0; i < l.size(); i++) {
			if(!popIndex.contains(i)) {
				result.add(l.get(i));
			}
		}
		return result;
	}*/
	
}