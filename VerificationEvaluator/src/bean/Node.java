package bean;

import java.util.ArrayList;

import hassel.bean.HS;

public class Node {
	HS hdr = null;
	String deviceName;
	int port = -1;
	boolean isOut;
	ArrayList<String> visits = new ArrayList<String>();
	ArrayList<HS> hsHistory = new ArrayList<HS>();
	public Node(HS hdr, int port) {
		this.hdr = hdr.copy();
		this.port = port;
	}
	
	public HS getHdr() {
		return hdr;
	}
	public void setHdr(HS hdr) {
		this.hdr = hdr;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public ArrayList<String> getVisits() {
		return visits;
	}
	public void setVisits(ArrayList<String> visits) {
		this.visits = visits;
	}
	public ArrayList<HS> getHsHistory() {
		return hsHistory;
	}
	public void setHsHistory(ArrayList<HS> hsHistory) {
		this.hsHistory = hsHistory;
	}
	public void printSelf() {
		System.out.println("-----");
		System.out.println(this.hdr.toString());
		System.out.println(this.port);
		System.out.println(this.visits);
		System.out.println("-----");
	}
}