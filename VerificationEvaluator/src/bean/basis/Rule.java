package bean.basis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import factory.AbstractIPFactory;
import interfaces.AbstractIP;
import interfaces.Header;
import com.microsoft.z3.*;

public class Rule implements Serializable{
	String id = null;
	ArrayList<Integer> inPorts = new ArrayList<Integer>();
	ArrayList<Integer> outPorts = new ArrayList<Integer>();
	Header match = null;
	Header mask = null;
	Header rewrite = null;
	Header inverseMatch = null;
	Header inverseRewrite = null;
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
		if(proto.getMatch()!=null)this.match = proto.getMatch().copy();
		if(proto.getMask()!=null)this.mask = proto.getMask().copy();
		if(proto.getRewrite()!=null)this.rewrite = proto.getRewrite().copy();
		if(proto.getInverseMatch()!=null)this.inverseMatch = proto.getInverseMatch().copy();
		if(proto.getInverseRewrite()!=null)this.inverseRewrite = proto.getInverseRewrite().copy();
		this.Action = proto.getAction();
	}
	
	public Rule(Rule proto, Header newMatch) {
		this.id = proto.getId();
		this.inPorts.addAll(proto.getInPorts());
		this.outPorts.addAll(proto.getOutPorts());
		if(proto.getMatch()!=null)this.match = newMatch.copy();
		if(proto.getMask()!=null)this.mask = proto.getMask().copy();
		if(proto.getRewrite()!=null)this.rewrite = proto.getRewrite().copy();
		if(proto.getInverseMatch()!=null)this.inverseMatch = proto.getInverseMatch().copy();
		if(proto.getInverseRewrite()!=null)this.inverseRewrite = proto.getInverseRewrite().copy();
		this.Action = proto.getAction();
	}
	
	//public Rule(ArrayList<Integer> inPorts, Wildcard match, ArrayList<Integer> outPorts, Wildcard mask, Wildcard rewrite, String filename, ArrayList<Integer> lines) {
	public Rule(ArrayList<Integer> inPorts, Header match, ArrayList<Integer> outPorts, Header mask, Header rewrite) {
		this.inPorts.addAll(inPorts);
		this.outPorts.addAll(outPorts);
		if(match != null) {
			this.match = match.copy();
		}
		if(mask != null) {
			this.mask = mask.copy();
		}
		if(rewrite != null) {
			this.rewrite = rewrite.copy();
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

	public Header getMatch() {
		return match;
	}

	public void setMatch(Header match) {
		this.match = match;
	}

	public Header getMask() {
		return mask;
	}

	public void setMask(Header mask) {
		this.mask = mask;
	}

	public Header getRewrite() {
		return rewrite;
	}

	public void setRewrite(Header rewrite) {
		this.rewrite = rewrite;
	}

	public Header getInverseMatch() {
		return inverseMatch;
	}

	public void setInverseMatch(Header inverseMatch) {
		this.inverseMatch = inverseMatch;
	}

	public Header getInverseRewrite() {
		return inverseRewrite;
	}

	public void setInverseRewrite(Header inverseRewrite) {
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
	
	public HashMap<Integer, ArrayList<BoolExpr>> generate_z3_rule(Context ctx, Expr pkt) {
		HashMap<Integer, ArrayList<BoolExpr>> result = new HashMap<Integer, ArrayList<BoolExpr>>();
		if(this.match != null) {
			BoolExpr input = this.match.z3Match(ctx, pkt);
			BoolExpr forward = ctx.mkBool(false);
			for(int inPort : this.inPorts) {
				forward = ctx.mkOr(forward, ctx.mkBoolConst("at_inport_" + String.valueOf(inPort)));
			}
			forward = ctx.mkAnd(forward, input);
			for(int outPort : this.outPorts) {
				if(!result.containsKey(outPort)) {
					result.put(outPort, new ArrayList<BoolExpr>());
				}
				result.get(outPort).add(forward);
			}
		}else {
			BoolExpr input = ctx.mkBool(false);
			for(int port : this.inPorts) {
				input = ctx.mkOr(input, ctx.mkBoolConst("at_outport_" + String.valueOf(port)));
			}
			for(int port : this.outPorts) {
				if(!result.containsKey(port)) {
					result.put(port, new ArrayList<BoolExpr>());
				}
				result.get(port).add(input);
			}
		}
		return result;
	}
	public String toString() {
		String result = "";
		result += this.inPorts.toString() + "$";
		if(match != null) {
			result += this.match.toString() + "$";
		}
		result += this.outPorts.toString() + "$";
		return result;
	}
	
	public void clear() {
		this.match = null;
		this.mask = null;
		this.rewrite = null;
		this.inverseMatch = null;
		this.inverseRewrite = null;
	}
}