package utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.math.*;

public class General{
	public static String int2WC(int input, int length) {
		String result = "";
		//while(input != 0) {
		for(int i = 0; i<length; i++) {
			result = (input&1) + result;
			input = input >>> 1;
		}
		return result;
	}
	
	public static String bitset2hex(BitSet bitset, int length) {
		String result = "";
		for(int i = 0; i < length; i++) {
			
		}
		return result;
	}
	
	public static ArrayList<Integer> string2Array(String input){
		assert input.startsWith("[") && input.endsWith("]");
		ArrayList<Integer> result = new ArrayList<Integer>();
		if(input.equals("[]")) {
			return result;
		}
		String subString = input.substring(1, input.length()-1);
		String[] numbers = subString.split(", ");
		for(String number: numbers) {
			result.add(Integer.parseInt(number));
		}
		return result;
	}
	
	public static void main(String args[]) {
		System.out.print(int2WC(1,3));
	}
}