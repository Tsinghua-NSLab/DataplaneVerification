package bean.basis;

import java.util.ArrayList;

import interfaces.AbstractIP;
import interfaces.Header;

public class Influence{
	Rule influencedBy = null;
	Header intersect = null;
	ArrayList<Integer> ports = null;
	public Influence(Rule influencedBy, Header intersect, ArrayList<Integer> ports) {
		super();
		this.influencedBy = influencedBy;
		this.intersect = intersect;
		this.ports = ports;
	}
	public Rule getInfluencedBy() {
		return influencedBy;
	}
	public void setInfluencedBy(Rule influencedBy) {
		this.influencedBy = influencedBy;
	}
	public Header getIntersect() {
		return intersect;
	}
	public void setIntersect(Header intersect) {
		this.intersect = intersect;
	}
	public ArrayList<Integer> getPorts() {
		return ports;
	}
	public void setPorts(ArrayList<Integer> ports) {
		this.ports = ports;
	}
	
	
}