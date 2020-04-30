package smt.utils;

import java.util.BitSet;

public class BitSetUtils{
	public static long BitSet2Long(BitSet bitset) {
		return bitset.toLongArray()[0];
	}
}