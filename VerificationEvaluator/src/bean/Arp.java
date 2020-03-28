package bean;

public class Arp{
	String Mac;
	String Vlan;
	
	public Arp() {
		Mac = "";
		Vlan = "";
	}
	
	public Arp(String Mac, String Vlan) {
		this.Mac = Mac;
		this.Vlan = Vlan;
	}
	
	public String getMac() {
		return Mac;
	}
	public void setMac(String Mac) {
		this.Mac = Mac;
	}
	public String getVlan() {
		return Vlan;
	}
	public void setVlan(String Vlan) {
		this.Vlan = Vlan;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Arp) {
			Arp other = (Arp) obj;
			return Mac.equals(other.Mac)&&Vlan.equals(other.Vlan);
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		return (Mac+Vlan).hashCode();
	}
}