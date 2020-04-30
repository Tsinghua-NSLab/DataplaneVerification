package bean.basis;

import java.util.ArrayList;

import factory.AbstractIPFactory;
import interfaces.AbstractIP;

public class Rule{
	String id = null;
	ArrayList<Integer> inPorts = new ArrayList<Integer>();
	ArrayList<Integer> outPorts = new ArrayList<Integer>();
	AbstractIP match = null;
	AbstractIP mask = null;
	AbstractIP rewrite = null;
	AbstractIP inverseMatch = null;
	AbstractIP inverseRewrite = null;
	//String filename = "";
	String Action = "";
	//ArrayList<Integer> lines = new ArrayList<Integer>();
	//ArrayList<Influence> affectedBy = new ArrayList<Influence>();
	//ArrayList<Rule> influenceOn = new ArrayList<Rule>();
	
	public Rule() {
		
	}
	
	public Rule(Rule proto, ArrayList<Integer> inPorts) {
		this.id = proto.getId();
		this.inPorts.addAll(inPorts);
		this.outPorts.addAll(proto.getOutPorts());
		this.match = AbstractIPFactory.generateAbstractIP(proto.getMatch());
		this.mask = AbstractIPFactory.generateAbstractIP(proto.getMask());
		this.rewrite = AbstractIPFactory.generateAbstractIP(proto.getRewrite());
		this.inverseMatch = AbstractIPFactory.generateAbstractIP(proto.getInverseMatch());
		this.inverseRewrite = AbstractIPFactory.generateAbstractIP(proto.getInverseRewrite());
		this.Action = proto.getAction();
	}
	
	public Rule(Rule proto, AbstractIP newMatch) {
		this.id = proto.getId();
		this.inPorts.addAll(proto.getInPorts());
		this.outPorts.addAll(proto.getOutPorts());
		this.match = AbstractIPFactory.generateAbstractIP(newMatch);
		this.mask = AbstractIPFactory.generateAbstractIP(proto.getMask());
		this.rewrite = AbstractIPFactory.generateAbstractIP(proto.getRewrite());
		this.inverseMatch = AbstractIPFactory.generateAbstractIP(proto.getInverseMatch());
		this.inverseRewrite = AbstractIPFactory.generateAbstractIP(proto.getInverseRewrite());
		this.Action = proto.getAction();
	}
	
	//public Rule(ArrayList<Integer> inPorts, Wildcard match, ArrayList<Integer> outPorts, Wildcard mask, Wildcard rewrite, String filename, ArrayList<Integer> lines) {
	public Rule(ArrayList<Integer> inPorts, AbstractIP match, ArrayList<Integer> outPorts, AbstractIP mask, AbstractIP rewrite) {
		this.inPorts.addAll(inPorts);
		this.outPorts.addAll(outPorts);
		if(match != null) {
			this.match = AbstractIPFactory.generateAbstractIP(match);
		}
		if(mask != null) {
			this.mask = AbstractIPFactory.generateAbstractIP(mask);
		}
		if(rewrite != null) {
			this.rewrite = AbstractIPFactory.generateAbstractIP(rewrite);
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

	public AbstractIP getMatch() {
		return match;
	}

	public void setMatch(AbstractIP match) {
		this.match = match;
	}

	public AbstractIP getMask() {
		return mask;
	}

	public void setMask(AbstractIP mask) {
		this.mask = mask;
	}

	public AbstractIP getRewrite() {
		return rewrite;
	}

	public void setRewrite(AbstractIP rewrite) {
		this.rewrite = rewrite;
	}

	public AbstractIP getInverseMatch() {
		return inverseMatch;
	}

	public void setInverseMatch(AbstractIP inverseMatch) {
		this.inverseMatch = inverseMatch;
	}

	public AbstractIP getInverseRewrite() {
		return inverseRewrite;
	}

	public void setInverseRewrite(AbstractIP inverseRewrite) {
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

	//public ArrayList<Influence> getAffectedBy() {
	//	return affectedBy;
	//}

	//public void setAffectedBy(ArrayList<Influence> affectedBy) {
	//	this.affectedBy = affectedBy;
	//}

	//public ArrayList<Rule> getInfluenceOn() {
	//	return influenceOn;
	//}

	//public void setInfluenceOn(ArrayList<Rule> influenceOn) {
	//	this.influenceOn = influenceOn;
	//}
	
}