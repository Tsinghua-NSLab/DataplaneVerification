package bean;

import java.io.Serializable;

public class Access implements Serializable{
	String iface = null;
	boolean inout=true;// true: in, false: out
	int vlan = -1;
	String file = null;
	int line = -1;
	
	public Access(String iface, boolean inout, int vlan, String file, int line) {
		super();
		this.iface = iface;
		this.inout = inout;
		this.vlan = vlan;
		this.file = file;
		this.line = line;
	}
	public String getIface() {
		return iface;
	}
	public void setIface(String iface) {
		this.iface = iface;
	}
	public boolean isInout() {
		return inout;
	}
	public void setInout(boolean inout) {
		this.inout = inout;
	}
	public int getVlan() {
		return vlan;
	}
	public void setVlan(int vlan) {
		this.vlan = vlan;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
}