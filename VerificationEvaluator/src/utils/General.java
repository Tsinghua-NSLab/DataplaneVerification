package utils;

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
	public static void main(String args[]) {
		System.out.print(int2WC(1,3));
	}
}