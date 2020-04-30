package bean.basis;

import java.util.ArrayList;

import interfaces.AbstractIP;

public class Influence{
	Rule influencedBy = null;
	AbstractIP intersect = null;
	ArrayList<Integer> ports = null;
	
	public Influence(Rule influencedBy, AbstractIP intersect, ArrayList<Integer> ports) {
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
	public AbstractIP getIntersect() {
		return intersect;
	}
	public void setIntersect(AbstractIP intersect) {
		this.intersect = intersect;
	}
	public ArrayList<Integer> getPorts() {
		return ports;
	}
	public void setPorts(ArrayList<Integer> ports) {
		this.ports = ports;
	}
	
	
}