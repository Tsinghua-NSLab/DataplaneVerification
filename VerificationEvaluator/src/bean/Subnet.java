package bean;

import java.io.Serializable;

public class Subnet implements Serializable{
	int ip;
	int subnet;
	
	public Subnet(int ip, int subnet) {
		super();
		this.ip = ip;
		this.subnet = subnet;
	}
	public int getIp() {
		return ip;
	}
	public void setIp(int ip) {
		this.ip = ip;
	}
	public int getSubnet() {
		return subnet;
	}
	public void setSubnet(int subnet) {
		this.subnet = subnet;
	}
	
}