package device;

import java.util.ArrayList;
import java.util.HashMap;

import bean.basis.Influence;
import bean.basis.Node;
import bean.basis.tfFunction;
import hassel.bean.HS;
import hassel.bean.Wildcard;
import interfaces.Device;
import parser.CiscoParser;

/**
 * @author skingFD
 * Device in raw topology network
 */
public class RawDevice implements Device{
	int deviceID = -1;
	public HashMap<String, Integer> portToID = new HashMap<String, Integer>(); 
	tfFunction tfFuncs = null;
	CiscoParser parser = null;
	public RawDevice() {	
	}
	public RawDevice(int deviceID, tfFunction tfFuncs){
		this.deviceID = deviceID;
		this.tfFuncs = tfFuncs;
	}
	public RawDevice(int deviceID, HashMap<String, Integer> portToID, tfFunction tfFuncs) {
		this.portToID.putAll(portToID);
		this.tfFuncs = tfFuncs;
	}
	public HashMap<String, Integer> getPortToID() {
		return portToID;
	}
	public void setPortToID(HashMap<String, Integer> portToID) {
		this.portToID = portToID;
	}
	public tfFunction getTfFuncs() {
		return tfFuncs;
	}
	public void setTfFuncs(tfFunction tfFuncs) {
		this.tfFuncs = tfFuncs;
	}
	public int getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}
	public CiscoParser getParser() {
		return parser;
	}
	public void setParser(CiscoParser parser) {
		this.parser = parser;
	}
}