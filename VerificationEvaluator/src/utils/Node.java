package utils;

import java.util.ArrayList;

import bean.FIB;
import bean.Ip;

public class Node{
	Node zero = null;
	Node one = null;
	String action = null;
	ArrayList<FIB> IPs = new ArrayList<FIB>();
	
	public void printSelf(int indent) {
		String ind = "";
		for(int i = 0; i<indent;i++) {
			ind = ind + "\t";
		}
		String strIP = ind+"IPs";
		for(FIB i: this.IPs) {
			strIP = strIP + i.getIP().intToDottedIp(i.getIP().getIP())+"/"+i.getIP().getSubnet()+",";
		}
		System.out.println(strIP);
		System.out.println(ind+"Action: "+this.action);
		if(this.zero!=null) {
			System.out.println(ind+"Zero:");
			this.zero.printSelf(indent+1);
		}
		if(this.one!=null) {
			System.out.println(ind+"One:");
			this.one.printSelf(indent+1);
		}
	}
	
	public boolean isLeaf() {
		return (this.zero==null)&&(this.one==null);
	}
	
	public void optimize(String action) {
		String propagateAction = action;
		if(this.action!=null) {
			propagateAction = this.action;
		}
		if(this.zero!=null) {
			this.zero.optimize(propagateAction);
			if(this.zero.action!=null&&this.zero.action==propagateAction) {
				this.IPs.addAll(this.zero.IPs);
				this.action = propagateAction;
				this.zero.IPs = new ArrayList<FIB>();
				this.zero.action = null;
				if(zero.isLeaf()) {
					this.zero = null;
				}
			}
		}
		if(this.one!=null) {
			this.one.optimize(propagateAction);
			if(this.one.action!=null&&this.one.action==propagateAction) {
				this.IPs.addAll(this.one.IPs);
				this.action = propagateAction;
				this.one.IPs = new ArrayList<FIB>();
				this.one.action = null;
				if(one.isLeaf()) {
					this.one = null;
				}
			}
		}
		if((this.zero!=null)&&(this.one!=null)&&(this.zero.action==this.one.action)&&this.zero.action!=null) {
			this.action = this.zero.action;
			this.IPs.addAll(this.zero.IPs);
			this.IPs.addAll(this.one.IPs);
			this.zero.IPs = new ArrayList<FIB>();
			this.one.IPs = new ArrayList<FIB>();
			this.zero.action = null;
			this.one.action = null;
			if(this.zero.isLeaf())this.zero=null;
			if(this.one.isLeaf())this.one=null;
		}
	}
	
	public void outputCompressed(int power, int cip, ArrayList<FIB> result) {
		if(this.zero!=null)this.zero.outputCompressed(power-1, cip, result);
		if(this.one!=null)this.one.outputCompressed(power-1, cip+(1<<(power-1)), result);
		if(this.IPs.size()>0) {
			result.add(new FIB(new Ip(cip,32-power),this.action,this.IPs));
		}
	}
}