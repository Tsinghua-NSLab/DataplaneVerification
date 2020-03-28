package rules;

import java.util.ArrayList;

import hassel.bean.Wildcard;

public class Rule{
	String id = null;
	ArrayList<Integer> inPorts = new ArrayList<Integer>();
	ArrayList<Integer> outPorts = new ArrayList<Integer>();
	Wildcard match = null;
	Wildcard mask = null;
	Wildcard rewrite = null;
	Wildcard inverseMatch = null;
	Wildcard inverseRewrite = null;
	//String filename = "";
	String Action = "";
	//ArrayList<Integer> lines = new ArrayList<Integer>();
	ArrayList<Influence> affectedBy = new ArrayList<Influence>();
	ArrayList<Rule> influenceOn = new ArrayList<Rule>();
	
	public Rule() {
		
	}
	
	//public Rule(ArrayList<Integer> inPorts, Wildcard match, ArrayList<Integer> outPorts, Wildcard mask, Wildcard rewrite, String filename, ArrayList<Integer> lines) {
	public Rule(ArrayList<Integer> inPorts, Wildcard match, ArrayList<Integer> outPorts, Wildcard mask, Wildcard rewrite) {
		this.inPorts.addAll(inPorts);
		this.outPorts.addAll(outPorts);
		if(match != null) {
			this.match = new Wildcard(match);
		}
		if(mask != null) {
			this.mask = new Wildcard(mask);
		}
		if(rewrite != null) {
			this.rewrite = new Wildcard(rewrite);
		}
	//	this.filename = filename;
	//	this.lines.addAll(lines);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<Integer> getInPorts() {
		return inPorts;
	}
	public void setInPorts(ArrayList<Integer> inPorts) {
		this.inPorts = inPorts;
	}
	public ArrayList<Integer> getOutPorts() {
		return outPorts;
	}
	public void setOutPorts(ArrayList<Integer> outPorts) {
		this.outPorts = outPorts;
	}

	public Wildcard getMatch() {
		return match;
	}

	public void setMatch(Wildcard match) {
		this.match = match;
	}

	public Wildcard getMask() {
		return mask;
	}

	public void setMask(Wildcard mask) {
		this.mask = mask;
	}

	public Wildcard getRewrite() {
		return rewrite;
	}

	public void setRewrite(Wildcard rewrite) {
		this.rewrite = rewrite;
	}

	public Wildcard getInverseMatch() {
		return inverseMatch;
	}

	public void setInverseMatch(Wildcard inverseMatch) {
		this.inverseMatch = inverseMatch;
	}

	public Wildcard getInverseRewrite() {
		return inverseRewrite;
	}

	public void setInverseRewrite(Wildcard inverseRewrite) {
		this.inverseRewrite = inverseRewrite;
	}

//	public String getFilename() {
//		return filename;
//	}
//
//	public void setFilename(String filename) {
//		this.filename = filename;
//	}

	public String getAction() {
		return Action;
	}

	public void setAction(String action) {
		Action = action;
	}

//	public ArrayList<Integer> getLines() {
//		return lines;
//	}
//
//	public void setLines(ArrayList<Integer> lines) {
//		this.lines = lines;
//	}

	public ArrayList<Influence> getAffectedBy() {
		return affectedBy;
	}

	public void setAffectedBy(ArrayList<Influence> affectedBy) {
		this.affectedBy = affectedBy;
	}

	public ArrayList<Rule> getInfluenceOn() {
		return influenceOn;
	}

	public void setInfluenceOn(ArrayList<Rule> influenceOn) {
		this.influenceOn = influenceOn;
	}
	
}