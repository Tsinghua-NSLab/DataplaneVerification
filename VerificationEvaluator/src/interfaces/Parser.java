package interfaces;

public interface Parser{
	void read_arp_table_file(String filename);
	void read_mac_table_file(String filename);
	void read_config_file(String filename);
	void read_spanning_tree_file(String filename);
	void read_route_file(String filename);
}