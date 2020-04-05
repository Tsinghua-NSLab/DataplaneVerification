package bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import device.RawDevice;
import parser.CiscoParser;
import tfFunction.tfFunction;

public class Network{
	ArrayList<Link> links = new ArrayList<Link>();
	HashMap<String, RawDevice> routers = new HashMap<String, RawDevice>();
	tfFunction NTF = new tfFunction();
	tfFunction TTF = new tfFunction();
	String configDict = "examples\\";
	public Network() {
		
	}
	public void initStanford() {
		//Add routers
		this.getRouters().put("bbra_rtr", new RawDevice(1,this.NTF));
		this.getRouters().put("bbrb_rtr", new RawDevice(2,this.NTF));
		this.getRouters().put("boza_rtr", new RawDevice(3,this.NTF));
		this.getRouters().put("bozb_rtr", new RawDevice(4,this.NTF));
		this.getRouters().put("coza_rtr", new RawDevice(5,this.NTF));
		this.getRouters().put("cozb_rtr", new RawDevice(6,this.NTF));
		this.getRouters().put("goza_rtr", new RawDevice(7,this.NTF));
		this.getRouters().put("gozb_rtr", new RawDevice(8,this.NTF));
		this.getRouters().put("poza_rtr", new RawDevice(9,this.NTF));
		this.getRouters().put("pozb_rtr", new RawDevice(10,this.NTF));
		this.getRouters().put("roza_rtr", new RawDevice(11,this.NTF));
		this.getRouters().put("rozb_rtr", new RawDevice(12,this.NTF));
		this.getRouters().put("soza_rtr", new RawDevice(13,this.NTF));
		this.getRouters().put("sozb_rtr", new RawDevice(14,this.NTF));
		this.getRouters().put("yoza_rtr", new RawDevice(15,this.NTF));
		this.getRouters().put("yozb_rtr", new RawDevice(16,this.NTF));
		for(String rtrName: this.getRouters().keySet()) {
			System.out.println("\n--- initializing " + rtrName + " ---");
			this.getRouters().get(rtrName).setParser(new CiscoParser(this.getRouters().get(rtrName).getDeviceID()));
			this.getRouters().get(rtrName).getParser().read_arp_table_file("examples\\"+rtrName+"_arp_table.txt");
			this.getRouters().get(rtrName).getParser().read_mac_table_file("examples\\"+rtrName+"_mac_table.txt");
			this.getRouters().get(rtrName).getParser().read_config_file("examples\\"+rtrName+"_config.txt");
			this.getRouters().get(rtrName).getParser().read_spanning_tree_file("examples\\"+rtrName+"_spanning_tree.txt");
			this.getRouters().get(rtrName).getParser().read_route_file("examples\\"+rtrName+"_route.txt");
			this.getRouters().get(rtrName).getParser().optimize_forwarding_table();
			this.getRouters().get(rtrName).getParser().generate_port_ids(new HashSet<String>());
			this.getRouters().get(rtrName).getParser().generate_transfer_function(NTF);
		}
		//Add links
		this.getLinks().add(new Link("bbra_rtr","te7/3","goza_rtr","te2/1"));
		this.getLinks().add(new Link("bbra_rtr","te7/3","pozb_rtr","te3/1"));
		this.getLinks().add(new Link("bbra_rtr","te1/3","bozb_rtr","te3/1"));
		this.getLinks().add(new Link("bbra_rtr","te1/3","yozb_rtr","te2/1"));
		this.getLinks().add(new Link("bbra_rtr","te1/3","roza_rtr","te2/1"));
		this.getLinks().add(new Link("bbra_rtr","te1/4","boza_rtr","te2/1"));
		this.getLinks().add(new Link("bbra_rtr","te1/4","rozb_rtr","te3/1"));
		this.getLinks().add(new Link("bbra_rtr","te6/1","gozb_rtr","te3/1"));
		this.getLinks().add(new Link("bbra_rtr","te6/1","cozb_rtr","te3/1"));
		this.getLinks().add(new Link("bbra_rtr","te6/1","poza_rtr","te2/1"));
		this.getLinks().add(new Link("bbra_rtr","te6/1","soza_rtr","te2/1"));
		this.getLinks().add(new Link("bbra_rtr","te7/2","coza_rtr","te2/1"));
		this.getLinks().add(new Link("bbra_rtr","te7/2","sozb_rtr","te3/1"));
		this.getLinks().add(new Link("bbra_rtr","te6/3","yoza_rtr","te1/3"));
		this.getLinks().add(new Link("bbra_rtr","te7/1","bbrb_rtr","te7/1"));
		this.getLinks().add(new Link("bbrb_rtr","te7/4","yoza_rtr","te7/1"));
		this.getLinks().add(new Link("bbrb_rtr","te1/1","goza_rtr","te3/1"));
		this.getLinks().add(new Link("bbrb_rtr","te1/1","pozb_rtr","te2/1"));
		this.getLinks().add(new Link("bbrb_rtr","te6/3","bozb_rtr","te2/1"));
		this.getLinks().add(new Link("bbrb_rtr","te6/3","roza_rtr","te3/1"));
		this.getLinks().add(new Link("bbrb_rtr","te6/3","yozb_rtr","te1/1"));
		this.getLinks().add(new Link("bbrb_rtr","te1/3","boza_rtr","te3/1"));
		this.getLinks().add(new Link("bbrb_rtr","te1/3","rozb_rtr","te2/1"));
		this.getLinks().add(new Link("bbrb_rtr","te7/2","gozb_rtr","te2/1"));
		this.getLinks().add(new Link("bbrb_rtr","te7/2","cozb_rtr","te2/1"));
		this.getLinks().add(new Link("bbrb_rtr","te7/2","poza_rtr","te3/1"));
		this.getLinks().add(new Link("bbrb_rtr","te7/2","soza_rtr","te3/1"));
		this.getLinks().add(new Link("bbrb_rtr","te6/1","coza_rtr","te3/1"));
		this.getLinks().add(new Link("bbrb_rtr","te6/1","sozb_rtr","te2/1"));
		this.getLinks().add(new Link("boza_rtr","te2/3","bozb_rtr","te2/3"));
		this.getLinks().add(new Link("coza_rtr","te2/3","cozb_rtr","te2/3"));
		this.getLinks().add(new Link("goza_rtr","te2/3","gozb_rtr","te2/3"));
		this.getLinks().add(new Link("poza_rtr","te2/3","pozb_rtr","te2/3"));
        this.getLinks().add(new Link("roza_rtr","te2/3","rozb_rtr","te2/3"));
        this.getLinks().add(new Link("soza_rtr","te2/3","sozb_rtr","te2/3"));
        this.getLinks().add(new Link("yoza_rtr","te1/1","yozb_rtr","te1/3"));
        this.getLinks().add(new Link("yoza_rtr","te1/2","yozb_rtr","te1/2"));
        
        this.TTF.writeTopology(this);
	}
	
	public Network(HashMap<String, RawDevice> routers, ArrayList<Link> links) {
		this.routers.putAll(routers);
		this.links.addAll(links);
	}
	
	public tfFunction getNTF() {
		return NTF;
	}
	public void setNTF(tfFunction nTF) {
		NTF = nTF;
	}
	public tfFunction getTTF() {
		return TTF;
	}
	public void setTTF(tfFunction tTF) {
		TTF = tTF;
	}
	public String getConfigDict() {
		return configDict;
	}
	public void setConfigDict(String configDict) {
		this.configDict = configDict;
	}
	public HashMap<String, RawDevice> getRouters() {
		return routers;
	}
	public void setRouters(HashMap<String, RawDevice> routers) {
		this.routers = routers;
	}
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	public int getport(String router, String Iface) {
		return routers.get(router).getParser().get_port_id(Iface);
	}
}