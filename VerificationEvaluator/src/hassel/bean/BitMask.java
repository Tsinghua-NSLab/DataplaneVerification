package hassel.bean;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import bean.basis.Ip;
import interfaces.AbstractIP;

public class BitMask implements AbstractIP{
	int length = 0;
	BitSet mainBit = new BitSet();//represent the value of each bit
	BitSet maskBit = new BitSet();//0 represents this bit is not important, 1 represents this bit needs match
	boolean isEmpty = false;
	public BitMask() {
		
	}
	
	public BitMask(BitMask bitMask) {
		length = bitMask.length;
		mainBit.or(bitMask.mainBit);
		maskBit.or(bitMask.maskBit);
	}
	
	public BitMask(int Length) {
		length = Length;
		mainBit.set(0,length);
		maskBit.set(0,length);
	}
	
	public BitMask(String wcString) {
		length = wcString.length();
		mainBit.clear();
		maskBit.clear();
		for(int i = 0; i < length; i++) {
			if(wcString.charAt(length-i-1)=='x') {
				mainBit.clear(i);
			}else if(wcString.charAt(length-i-1)=='1') {
				mainBit.set(i);
				maskBit.set(i);
			}else if(wcString.charAt(length-i-1)=='0') {
				mainBit.clear(i);
				maskBit.set(i);
			}else {
				System.out.println("Error, invalid string initialization");
			}
		}
	}
	
	public BitMask(int Length, int value) {
		length = Length;
		maskBit.set(0, length);
		for(int i = 0; i < length; i++) {
			mainBit.set(i, (value%2==1));
			value = value >> 1;
		}
	}
	
	public BitMask(int Length, char Bit) {
		length = Length;
		if(Bit == 'x') {
			maskBit.clear(0, length);
		}else if(Bit == '0') {
			maskBit.set(0,length);
			mainBit.clear(0,length);
		}else if(Bit == '1') {
			maskBit.set(0,length);
			mainBit.set(0,length);
		}else {
			System.out.println("Error, invalid char initialization");
		}
	}
	
	@Override
	public void setMask(int rightMask) {
		maskBit.clear(length-rightMask, length);
	}

	@Override
	public void setField(HashMap<String, Integer> hsFormat, String field, long value, int rightMask) {
		int fieldLength = hsFormat.get(field+"_len");
		int startPos = hsFormat.get(field + "_pos");
		for(int i = startPos; i<startPos+fieldLength; i++) {
			if((value&1L)==0) {
				maskBit.set(i);
				mainBit.clear(i);
			}else {
				maskBit.set(i);
				mainBit.set(i);
			}
			value = value>>>1;
		}
		maskBit.clear(startPos, startPos+rightMask);
	}

	@Override
	public void and(AbstractIP other) {
		if(other.getClass().getName()=="hassel.bean.BitMask") {
			BitMask otherBM = (BitMask)other;
			if(this.length == otherBM.length) {
				BitSet totalMask = new BitSet(length);
				totalMask.set(0, length);
				totalMask.and(this.maskBit);
				totalMask.and(otherBM.maskBit);
				
				BitSet feature = new BitSet(length);
				feature.or(this.mainBit);
				feature.and(totalMask);
				
				BitSet otherFeature = new BitSet(length);
				otherFeature.or(otherBM.mainBit);
				otherFeature.and(totalMask);
				
				feature.xor(otherFeature);
				if(!feature.isEmpty()) {
					this.isEmpty = true;
					return;
				}
				feature.clear();
				feature.or(this.maskBit);
				feature.and(this.mainBit);
				otherFeature.clear();
				otherFeature.or(otherBM.maskBit);
				otherFeature.and(otherBM.mainBit);
				this.mainBit.clear();
				this.mainBit.or(feature);
				this.mainBit.or(otherFeature);
				this.maskBit.or(otherBM.maskBit);
			}else {
				System.out.println("Error: length mismatch");
			}
		}else {
			System.out.println("hassel.bean.BitMask: bit operation type mismatch:" + other.getClass().getName());
		}
	}

//	@Override
//	public void or(AbstractIP other) {
//		if(other.getClass().getName()=="hassel.bean.BitMask") {
//			BitMask otherBM = (BitMask)other;
//			if(this.length == otherBM.length) {
//				BitSet totalMask = new BitSet(length);
//				totalMask.and(this.maskBit);
//				totalMask.and(otherBM.maskBit);
//				
//				BitSet feature = new BitSet(length);
//				feature.or(this.mainBit);
//				feature.and(totalMask);
//				
//				BitSet otherFeature = new BitSet(length);
//				otherFeature.or(otherBM.mainBit);
//				otherFeature.and(totalMask);
//				
//				feature.xor(otherFeature);
//				if(!feature.isEmpty()) {
//					this.isEmpty = true;
//					return;
//				}
//				feature.clear();
//				feature.or(this.maskBit);
//				feature.and(this.mainBit);
//				otherFeature.clear();
//				otherFeature.or(otherBM.maskBit);
//				otherFeature.and(otherBM.mainBit);
//				this.mainBit.clear();
//				this.mainBit.or(feature);
//				this.mainBit.or(otherFeature);
//				this.maskBit.and(otherBM.maskBit);
//			}else {
//				System.out.println("Error: length mismatch");
//			}
//		}else {
//			System.out.println("hassel.bean.BitMask: bit operation type mismatch:" + other.getClass().getName());
//		}
//	}
//
//	@Override
//	public void xor(AbstractIP other) {
//		if(other.getClass().getName()=="hassel.bean.BitMask") {
//			BitMask otherBM = (BitMask)other;
//			if(this.length == otherBM.length) {
//				BitSet totalMask = new BitSet(length);
//				totalMask.and(this.maskBit);
//				totalMask.and(otherBM.maskBit);
//				
//				BitSet feature = new BitSet(length);
//				feature.or(this.mainBit);
//				feature.and(totalMask);
//				
//				BitSet otherFeature = new BitSet(length);
//				otherFeature.or(otherBM.mainBit);
//				otherFeature.and(totalMask);
//				
//				feature.xor(otherFeature);
//				if(!feature.isEmpty()) {
//					this.isEmpty = true;
//					return;
//				}
//				feature.clear();
//				feature.or(this.maskBit);
//				feature.and(this.mainBit);
//				otherFeature.clear();
//				otherFeature.or(otherBM.maskBit);
//				otherFeature.and(otherBM.mainBit);
//				this.mainBit.clear();
//				this.mainBit.or(feature);
//				this.mainBit.or(otherFeature);
//				this.maskBit.and(otherBM.maskBit);
//			}else {
//				System.out.println("Error: length mismatch");
//			}
//		}else {
//			System.out.println("hassel.bean.BitMask: bit operation type mismatch:" + other.getClass().getName());
//		}
//	}

	@Override
	public ArrayList<AbstractIP> complement() {
		ArrayList<AbstractIP> result = new ArrayList<AbstractIP>();
		for(int i = 0; i < this.length;i++) {
			if(this.maskBit.get(i)) {
				BitMask tmp = new BitMask(this.length, 'x');
				tmp.maskBit.set(i);
				tmp.mainBit.set(i, !this.mainBit.get(i));
				result.add(tmp);
			}
		}
		return result;
	}

	@Override
	public ArrayList<AbstractIP> minus(AbstractIP other) {
		ArrayList<AbstractIP> result = new ArrayList<AbstractIP>();
		BitMask temp = new BitMask(this);
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
		// TODO How rewrite?
		if(mask.getClass().getName()=="hassel.bean.BitMask"&&rewrite.getClass().getName()=="hassel.bean.BitMask") {
			BitMask maskBM = (BitMask)mask;
			BitMask rewriteBM = (BitMask)rewrite;
			int result = 0;
			BitSet tmp1 = new BitSet();
			return result;
		}else {
			System.out.println("hassel.bean.Wildcard: bit operation type mismatch:" + mask.getClass().getName() +","+ rewrite.getClass().getName());
		}
		return -1;
	}

	@Override
	public boolean equals(AbstractIP other) {
		if(other.getClass().getName()=="hassel.bean.BitMask") {
			BitMask otherBM = (BitMask)other;
			if(this.length == otherBM.length) {
				BitSet tmp = new BitSet(length);
				tmp.or(this.mainBit);
				tmp.and(this.maskBit);
				BitSet otherTmp = new BitSet(length);
				otherTmp.or(otherBM.mainBit);
				otherTmp.and(otherBM.maskBit);
				tmp.xor(otherTmp);
				return tmp.isEmpty();
			}else {
				System.out.println("Error: length mismatch");
			}
		}else {
			System.out.println("hassel.bean.BitMask: bit operation type mismatch:" + other.getClass().getName());
		}
		return false;
	}

	@Override
	public boolean contains(AbstractIP other) {
		if(other.getClass().getName()=="hassel.bean.BitMask") {
			BitMask otherBM = (BitMask)other;
			if(this.length == otherBM.length) {
				BitSet maskFeature = new BitSet(length);
				maskFeature.or(maskBit);
				maskFeature.or(maskBit);
				maskFeature.xor(otherBM.maskBit);
				if(!maskFeature.isEmpty()) {
					return false;
				}
				BitSet feature = new BitSet(length);
				feature.or(this.mainBit);
				feature.and(this.maskBit);
				BitSet otherFeature = new BitSet(length);
				otherFeature.or(otherBM.mainBit);
				otherFeature.and(otherBM.maskBit);
				
				otherFeature.and(feature);
				otherFeature.xor(feature);
				return otherFeature.isEmpty();
			}else {
				System.out.println("Error: length mismatch");
			}
		}else {
			System.out.println("hassel.bean.BitMask: bit operation type mismatch:" + other.getClass().getName());
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return this.isEmpty;
	}

	@Override
	public int getLength() {
		return this.length;
	}

	@Override
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String getString() {
		String result = "";
		for(int i = 0; i<this.length; i++) {
			if(this.maskBit.get(i)) {
				if(this.mainBit.get(i)) {
					result = "1" + result;
				} else {
					result = "0" + result;
				}
			}else {
				result = "x" + result;
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return getString();
	}
}