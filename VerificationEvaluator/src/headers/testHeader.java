package headers;

import java.util.HashMap;

public class testHeader{
	HashMap<String,Integer> format = new HashMap<String,Integer>();
	
	public testHeader() {
		init();
	}
	
	public void init() {
		// length of bytes
		format.put("vlan_pos", 0*8);
		format.put("vlan_len", 2*8);
		format.put("length", 2*8);
	}

	public HashMap<String, Integer> getFormat() {
		return format;
	}

	public void setFormat(HashMap<String, Integer> format) {
		this.format = format;
	}
}