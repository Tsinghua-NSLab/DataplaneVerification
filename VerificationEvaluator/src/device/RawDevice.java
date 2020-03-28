package device;

import java.util.ArrayList;
import java.util.HashMap;

import bean.Node;
import hassel.bean.HS;
import hassel.bean.Wildcard;
import interfaces.Device;
import rules.Influence;
import rules.Rule;
import tfFunction.tfFunction;

/**
 * @author skingFD
 * Device in raw topology network
 */
public class RawDevice implements Device{
	String deviceID = "";
	ArrayList<String> iface = new ArrayList<String>();
	HashMap<String, Integer> portToID = new HashMap<String, Integer>(); 
	tfFunction tfFuncs = null;
	public RawDevice() {	
	}
	public RawDevice(String deviceID, ArrayList<String> iface, tfFunction tfFuncs){
		this.deviceID = deviceID;
		this.iface = iface;
		this.tfFuncs = tfFuncs;
	}
	public RawDevice(HashMap<String, Integer> portToID, tfFunction tfFuncs) {
		this.portToID.putAll(portToID);
		this.tfFuncs = tfFuncs;
	}
	public HashMap<String, Integer> getPortToID() {
		return portToID;
	}
	public void setPortToID(HashMap<String, Integer> portToID) {
		this.portToID = portToID;
	}
	public ArrayList<String> getIface() {
		return iface;
	}
	public void setIface(ArrayList<String> iface) {
		this.iface = iface;
	}
	public tfFunction getTfFuncs() {
		return tfFuncs;
	}
	public void setTfFuncs(tfFunction tfFuncs) {
		this.tfFuncs = tfFuncs;
	}
	public ArrayList<Node> applyFwdRule(Node input) {
		int inport = this.portToID.get(input.getPort());
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<String> appliedRules = new ArrayList<String>();
		if(!this.iface.contains(input.getPort())) {
			System.out.println("Error, no such interface");
			return null;
		}
		for(Rule rule:this.getTfFuncs().getRulesForInport(inport)) {
			//check if match pattern and port is in inports
			ArrayList<Integer> modOutports = new ArrayList<Integer>();
			modOutports.addAll(rule.getOutPorts());
			//TODO change to general interface
			HS newHs = input.getHdr().copyAnd(rule.getMatch());
			if(!newHs.isEmpty()&&rule.getInPorts().contains(inport)) {
				for(Influence affect:rule.getAffectedBy()) {
					if(affect.getPorts().contains(inport)&&(appliedRules.contains(rule.getId()))) {
						newHs.diffHS(affect.getIntersect());
					}
				}
				newHs.cleanUp();
				if(newHs.count()==0) {
					appliedRules.add(rule.getId());
					continue;
				}
				newHs.pushAppliedTfRule(this.deviceID, rule.getId(), inport);
				appliedRules.add(rule.getId());
				for(int outport: modOutports) {
					result.add(new Node(newHs,outport));
				}
			}else {
				continue;
			}
		}
		return result;
	}
}