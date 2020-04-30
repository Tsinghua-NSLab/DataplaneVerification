package interfaces;

import java.util.HashMap;
import java.util.HashSet;

import bean.basis.BasicTF;

public interface Parser{
	static int PORT_ID_MULTIPLIER = 1;
	static int INTERMEDIATE_PORT_TYPE_CONST = 1;
	static int OUTPUT_PORT_TYPE_CONST = 2;
	static int PORT_TYPE_MULTIPLIER = 10000;
	static int SWITCH_ID_MULTIPLIER = 100000;
	int get_port_id(String port);
	HashMap<String,Integer> getPortToID();
	void read_arp_table_file(String filename);
	void read_mac_table_file(String filename);
	void read_config_file(String filename);
	void read_spanning_tree_file(String filename);
	void read_route_file(String filename);
	void generate_port_ids(HashSet<String> additionalPorts);
	void optimize_forwarding_table();
	void generate_transfer_function(BasicTF tf);
}