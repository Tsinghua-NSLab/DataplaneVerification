package bean;

public class Ip{
	int IP = -1;
	//int IPMask = 0;
	int Subnet = -1;
	
	public Ip() {
	}
	
	//public Ip(int iP, int iPMask, int subnet) {
	public Ip(int iP, int subnet) {
		super();
		this.IP = iP;
		//this.IPMask = iPMask;
		this.Subnet = subnet;//Because setfield uses right mask, so here is right mask too
	}
	public Ip(String dotted_subnet) {
		Subnet = 0;
		String[] arrs = dotted_subnet.split("/");
		if(arrs.length>1) {
			try {
				Subnet = 32-Integer.parseInt(arrs[1]);
			}catch(Exception e) {
			}
		}
		String[] dotted_ip = arrs[0].split("\\.");
		for(int i = 0; i < 4;i++) {
			IP = IP << 8;
			IP = IP|Integer.parseInt(dotted_ip[i]);
		}
		
	}
	
	public int getIP() {
		return IP;
	}
	public void setIP(int iP) {
		IP = iP;
	}
	public void setIP(String iP) {	
		setIP(dottedIPtoInt(iP));
	}
	/*public int getIPMask() {
		return IPMask;
	}
	public void setIPMask(int iPMask) {
		IPMask = iPMask;
	}
	public void setIPMask(String iP) {	
		setIPMask(dottedIPtoInt(iP));
	}*/
	public int getSubnet() {
		return Subnet;
	}
	public void setSubnet(int subnet) {
		Subnet = subnet;
	}
	
	public void setACLSubnet(String subnet) {
		int mask = dottedIPtoInt(subnet);
		Subnet = 0;
		while(true) {
			if((mask&1)==1) {
				mask = mask>>>1;
				Subnet++;
			}else {
				break;
			}
		}
	}
	
	public void setIfaceSubnet(String subnet) {
		int mask = dottedIPtoInt(subnet);
		Subnet = 0;
		while(true) {
			if((mask&1)==0) {
				mask = mask>>>1;
				Subnet++;
			}else {
				break;
			}
		}
	}
	
	public String intToDottedIp(int iP) {
		int ip3 = iP&0xff;
		iP = iP>>>8;
		int ip2 = iP&0xff;
		iP = iP>>>8;
		int ip1 = iP&0xff;
		iP = iP>>>8;
		return iP+"."+ip1+"."+ip2+"."+ip3;
	}
	
	public int dottedIPtoInt(String iP) {
		int result = 0;
		try {
			String[] arrs = iP.split("\\.");
			for(int i = 0; i < 4; i++) {
				result = result<<8;
				result += Integer.parseInt(arrs[i]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}