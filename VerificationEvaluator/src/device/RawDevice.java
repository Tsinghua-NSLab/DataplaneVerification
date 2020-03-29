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
}