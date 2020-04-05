package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

import bean.FIB;
import bean.Subnet;
import bean.basis.Ip;

public class Helper{
	public static boolean is_ip_address(String str) {
		String pattern = "(?:[\\d]{1,3})\\.(?:[\\d]{1,3})\\.(?:[\\d]{1,3})\\.(?:[\\d]{1,3})";
		return Pattern.matches(pattern, str);
	}
	
	public static boolean is_ip_subset(String str) {
		String pattern = "(?:[\\d]{1,3})\\.(?:[\\d]{1,3})\\.(?:[\\d]{1,3})\\.(?:[\\d]{1,3})/(?:[\\d]{1,2})";
		return Pattern.matches(pattern, str);
	}
	
	public static HashMap<Integer,Integer> range_to_wildcard(int r_s, int r_e, int length) {
		HashMap<Integer,Integer> result = new HashMap<Integer,Integer>();
		int vals = r_s;
		int vale = r_e;
		while(vals <= vale) {
			for(int i = 1; i< length+2;i++) {
				if(!((vals|(int)(Math.pow(2, i)-1))<=vale&&(vals%Math.pow(2, i)==0))) {
					result.put(vals, i-1);
					vals = (vals|(int)(Math.pow(2, (i-1))-1))+1;
					break;
				}
			}
		}
		return result;
	}
	
	public static int find_num_mask_bits_right_mak(int mask) {
		int count = 0;
		while(true) {
			if((mask&1)==1) {
				mask = mask>>>1;
				count++;
			}else {
				break;
			}
		}
		return count;
	}
	
	/*public static Ip dotted_subnet_to_int(String dotted_subnet) {
		int intip = 0;
		int subnet = 32;
		String[] arrs = dotted_subnet.split("/");
		if(arrs.length>1) {
			try {
				subnet = Integer.parseInt(arrs[1]);
			}catch(Exception e) {
			}
		}
		String[] dotted_ip = arrs[0].split("\\.");
		for(int i = 0; i < 4;i++) {
			intip = intip << 8;
			intip = intip|Integer.parseInt(dotted_ip[i]);
		}
		return new Ip(intip, subnet);
	}*/
	
	public static ArrayList<FIB> compress_ip_list(ArrayList<FIB> IPList) {
		//TODO node and tri
		ArrayList<FIB> result = new ArrayList<FIB>();
		Node root = new Node();
		for(FIB elem: IPList) {
			Node cur = root;
			for(int i = 31; i > 31-elem.getIP().getSubnet();i--) {
				int nextBit = (elem.getIP().getIP()>>i)&0x1;
				if(nextBit == 0) {
					if(cur.zero == null) {
						cur.zero = new Node();
					}
					cur = cur.zero;
				}else if(nextBit == 1){
					if(cur.one == null) {
						cur.one = new Node();
					}
					cur = cur.one;
				}
			}
			if(cur.IPs.size() == 0) {
				//TODO
				cur.IPs.add(elem);
				cur.action = elem.getPort();
			}
		}
		root.optimize(null);
		root.outputCompressed(32, 0, result);
		return result;
	}
	
	public static boolean arrayContains(String[] arrs, String word) {
		for(int i = 0; i< arrs.length; i++) {
			if(arrs[i].equals(word)) {
				return true;
			}
		}
		return false;
	}
}