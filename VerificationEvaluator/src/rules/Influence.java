package rules;

import java.util.ArrayList;

import hassel.bean.Wildcard;

public class Influence{
	Rule influencedBy = null;
	Wildcard intersect = null;
	ArrayList<Integer> ports = null;
	
	public Influence(Rule influencedBy, Wildcard intersect, ArrayList<Integer> ports) {
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
	public Wildcard getIntersect() {
		return intersect;
	}
	public void setIntersect(Wildcard intersect) {
		this.intersect = intersect;
	}
	public ArrayList<Integer> getPorts() {
		return ports;
	}
	public void setPorts(ArrayList<Integer> ports) {
		this.ports = ports;
	}
	
	
}