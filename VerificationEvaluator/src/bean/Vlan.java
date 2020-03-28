package bean;

import java.util.ArrayList;

public class Vlan{
	ArrayList<String> Access = new ArrayList<String>();
	ArrayList<String> Trunk = new ArrayList<String>();
	
	public ArrayList<String> getAccess() {
		return Access;
	}
	public void setAccess(ArrayList<String> access) {
		Access = access;
	}
	public ArrayList<String> getTrunk() {
		return Trunk;
	}
	public void setTrunk(ArrayList<String> trunk) {
		Trunk = trunk;
	}
}