package hassel.bean;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class Wildcard{
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
	
	public void setMask(int rightMask) {
		wcBit.set(2*(length-rightMask), 2*length);
	}
	
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
	
	public void and(Wildcard other) {
		if(this.length == other.length) {
			this.wcBit.and(other.wcBit);
		}else {
			System.out.println("Error: length mismatch");
		}
	}
	
	public void or(Wildcard other) {
		if(this.length == other.length) {
			this.wcBit.or(other.wcBit);
		}else {
			System.out.println("Error: length mismatch");
		}
	}
	
	public void xor(Wildcard other) {
		if(this.length == other.length) {
			this.wcBit.xor(other.getWcBit());
		}else {
			System.out.println("Error: length mismatch");
		}
	}
	
	public void not() {
		this.wcBit.flip(0, length);
	}
	
	public boolean equals(Wildcard other) {
		Wildcard tmp = new Wildcard(this);
		tmp.xor(other);
		return tmp.isEmpty();
	}
	
	public boolean contains(Wildcard other) {
		Wildcard tmp = new Wildcard(this);
		tmp.and(other);
		tmp.xor(other);
		return tmp.isEmpty();
	}
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public BitSet getWcBit() {
		return wcBit;
	}
	public void setWcBit(BitSet wcBit) {
		this.wcBit = wcBit;
	}

	public boolean isEmpty() {
		return wcBit.isEmpty();
	}
	
	public String getString() {
		String result = "";
		for(int i = 0; i < this.length; i++) {
			if(this.wcBit.get(2*i)) {
				if(this.wcBit.get(2*i+1)) {
					result = result+"x";
				}else {
					result = result+"0";
				}
			}else {
				if(this.wcBit.get(2*i+1)) {
					result = result+"1";
				}else {
					result = result+"z";
				}
			}
		}
		return result;
	}
	
	public ArrayList<Wildcard> complement(){
		ArrayList<Wildcard> result = new ArrayList<Wildcard>();
		for(int i = 0; i < this.length;i++) {
			if(this.getWcBit().get(2*length)) {
				if(this.getWcBit().get(2*length+1)) {
					continue;
				}else {
					Wildcard tmp = new Wildcard(this.length, 'x');
					tmp.getWcBit().clear(2*length);
				}
			}else {
				if(this.getWcBit().get(2*length+1)) {
					Wildcard tmp = new Wildcard(this.length, 'x');
					tmp.getWcBit().clear(2*length+1);
				}else {
					result = new ArrayList<Wildcard>();
					result.add(new Wildcard(this.length,'x'));
				}
			}
		}
		return result;
	}
	
	public int rewrite(Wildcard mask, Wildcard rewrite) {
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
		tmp1.or(mask.getWcBit());
		tmp1.and(rewrite.getWcBit());
		tmp1.and(oddMask);
		tmp2.and(mask.getWcBit());
		tmp2.or(mask.getWcBit());
		tmp2.and(evenMask);
		tmp1.or(tmp2);
		wcBit=tmp1;
		for(int i = 0; i<length;i++) {
			if(wcBit.get(2*i)&&wcBit.get(2*i+1)) {
				result++;
			}
		}
		return result;
	}
	
	public static ArrayList<Wildcard> compressWildCardList(ArrayList<Wildcard> l) {
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
	}
	
	public static void main(String[] args) {
		Wildcard test = new Wildcard("10101010");
		System.out.println(test);
	}
}