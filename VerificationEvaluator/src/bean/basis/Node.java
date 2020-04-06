package bean.basis;

import java.util.ArrayList;

import interfaces.Header;


public class Node {
	Header hdr = null;
	String deviceName;
	int port = -1;
	boolean isOut;
	ArrayList<Integer> visits = new ArrayList<Integer>();
	ArrayList<Header> hsHistory = new ArrayList<Header>();
	
	public Node() {
	}
	
	public Node(Node node) {
		this.hdr = node.getHdr().copy();
		this.port = node.getPort();
	}
	
	public Node(Header hdr, int port) {
		this.hdr = hdr.copy();
		this.port = port;
	}
	
	public Header getHdr() {
		return hdr;
	}
	public void setHdr(Header hdr) {
		this.hdr = hdr;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public ArrayList<Integer> getVisits() {
		return visits;
	}
	public void setVisits(ArrayList<Integer> visits) {
		this.visits = visits;
	}
	public ArrayList<Header> getHsHistory() {
		return hsHistory;
	}
	public void setHsHistory(ArrayList<Header> hsHistory) {
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