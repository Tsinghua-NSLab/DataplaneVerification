package headers;

import java.io.Serializable;
import java.util.HashMap;

public class ciscoHeader implements Serializable{
	HashMap<String,Integer> format = new HashMap<String,Integer>();
	
	public ciscoHeader() {
		init();
	}
	
	public void init() {
		// length of bytes
		format.put("vlan_pos", 0*8);
		format.put("ip_src_pos", 2*8);
		format.put("ip_dst_pos", 6*8);
		format.put("ip_proto_pos", 10*8);
		format.put("transport_src_pos", 11*8);
		format.put("transport_dst_pos", 13*8);
		format.put("transport_ctrl_pos", 15*8);
		format.put("vlan_len", 2*8);
		format.put("ip_src_len", 4*8);
		format.put("ip_dst_len", 4*8);
		format.put("ip_proto_len", 1*8);
		format.put("transport_src_len", 2*8);
		format.put("transport_dst_len", 2*8);
		format.put("transport_ctrl_len", 1*8);
		format.put("length", 16*8);
	}

	public HashMap<String, Integer> getFormat() {
		return format;
	}

	public void setFormat(HashMap<String, Integer> format) {
		this.format = format;
	}
}