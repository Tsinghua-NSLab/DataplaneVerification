package bean;

import java.io.Serializable;

/**
 * @author skingFD
 * A link in the network
 */
public class Link implements Serializable{
	String Device1 = null;
	String Device2 = null;
	String Iface1 = null;
	String Iface2 = null;
	public Link() {		
	}
	
	public Link(String device1, String iface1, String device2, String iface2) {
		super();
		Device1 = device1;
		Device2 = device2;
		Iface1 = iface1;
		Iface2 = iface2;
	}

	public String getDevice1() {
		return Device1;
	}
	public void setDevice1(String device1) {
		Device1 = device1;
	}
	public String getDevice2() {
		return Device2;
	}
	public void setDevice2(String device2) {
		Device2 = device2;
	}
	public String getIface1() {
		return Iface1;
	}
	public void setIface1(String iface1) {
		Iface1 = iface1;
	}
	public String getIface2() {
		return Iface2;
	}
	public void setIface2(String iface2) {
		Iface2 = iface2;
	}
	
}