package bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import bean.basis.BasicTF;
import bean.basis.Rule;
import factory.HeaderFactory;
import factory.ParserFactory;
import interfaces.Header;
import interfaces.Parser;
import utils.General;

public class Network implements Serializable{
	ArrayList<Link> links = new ArrayList<Link>();
	ArrayList<GraphNode> graph = new ArrayList<GraphNode>();
	HashMap<String, Parser> routers = new HashMap<String, Parser>();
	HashMap<String, Integer> portToID = new HashMap<String, Integer>(); 
	BasicTF NTF = new BasicTF();
	BasicTF TTF = new BasicTF();
	String configDict = "examples\\";
	
	public Network() {
		
	}
	
	public void initFattree() {
		//layer = 3
		int dNum = 2;
		int pNum = 4;
		int aNum = 4;
		
		int d2p_dNum = 2;
		int d2p_pNum = 4;
		int p2a_pNum = 2;
		int p2a_aNum = 2;
		
		//Init a fattree test network
		Rule rule;
		ArrayList<Integer> inPorts;
		ArrayList<Integer> outPorts;
		Header ip;
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		//Add TTF rules (transfer functions)
		//Add a rules
		for(int i = 0; i < 4; i++) {
			//South forward
			for(int j = 0; j < 4; j++) {
				rule = new Rule();
				inPorts = new ArrayList<Integer>();
				outPorts = new ArrayList<Integer>();
				for(int k = 0; k < 4; k++) {
					inPorts.add(1+i*10+k);	
				}
				for(int k = 0; k < 2; k++) {
					inPorts.add(2+i*10+k);
				}
				outPorts.add(100+i*10+j);
				rule.setInPorts(inPorts);
				rule.setOutPorts(outPorts);
				ip = HeaderFactory.generateHeader(General.int2WC(i, 2)+General.int2WC(j, 2));
				rule.setMatch(ip);
				this.NTF.addFwdRule(rule);
			}
		}
		
		//Add NTF rules (links)
		//Add p2a links
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				for(int k = 0; k < 2; k++) {
					//North Link
					rule = new Rule();
					inPorts = new ArrayList<Integer>();
					inPorts.add(200+i*2*10+j*10+k);
					outPorts = new ArrayList<Integer>();
					outPorts.add(300+i*2*10+j+k*10);
					rule.setInPorts(inPorts);
					rule.setOutPorts(outPorts);
					this.TTF.addLinkRule(rule);
				
					//South Link
					rule = new Rule();
					outPorts = new ArrayList<Integer>();
					outPorts.add(200+i*2*10+j*10+k);
					inPorts = new ArrayList<Integer>();
					inPorts.add(300+i*2*10+j+k*10);
					rule.setInPorts(inPorts);
					rule.setOutPorts(outPorts);
					this.TTF.addLinkRule(rule);
				}
			}
		}
		//Add d2p links
		for(int i = 0; i < 1; i++) {
			for(int j = 0; j < 4; j++) {
				for(int k = 0; k < 2; k++) {
					//North Link
					rule = new Rule();
					inPorts = new ArrayList<Integer>();
					inPorts.add(400+i*4*10+j*10+k);
					outPorts = new ArrayList<Integer>();
					outPorts.add(500+i*2*10+j+k*10);
					rule.setInPorts(inPorts);
					rule.setOutPorts(outPorts);
					this.TTF.addLinkRule(rule);
				
					//South Link
					rule = new Rule();
					outPorts = new ArrayList<Integer>();
					outPorts.add(400+i*4*10+j*10+k);
					inPorts = new ArrayList<Integer>();
					inPorts.add(500+i*2*10+j+k*10);
					rule.setInPorts(inPorts);
					rule.setOutPorts(outPorts);
					this.TTF.addLinkRule(rule);
				}
			}
		}
	}
	
	public void initStanford() {
		//Add routers
		this.getRouters().put("bbra_rtr", ParserFactory.generateParser(1));
		this.getRouters().put("bbrb_rtr", ParserFactory.generateParser(2));
		this.getRouters().put("boza_rtr", ParserFactory.generateParser(3));
		this.getRouters().put("bozb_rtr", ParserFactory.generateParser(4));
		this.getRouters().put("coza_rtr", ParserFactory.generateParser(5));
		this.getRouters().put("cozb_rtr", ParserFactory.generateParser(6));
		this.getRouters().put("goza_rtr", ParserFactory.generateParser(7));
		this.getRouters().put("gozb_rtr", ParserFactory.generateParser(8));
		this.getRouters().put("poza_rtr", ParserFactory.generateParser(9));
		this.getRouters().put("pozb_rtr", ParserFactory.generateParser(10));
		this.getRouters().put("roza_rtr", ParserFactory.generateParser(11));
		this.getRouters().put("rozb_rtr", ParserFactory.generateParser(12));
		this.getRouters().put("soza_rtr", ParserFactory.generateParser(13));
		this.getRouters().put("sozb_rtr", ParserFactory.generateParser(14));
		this.getRouters().put("yoza_rtr", ParserFactory.generateParser(15));
		this.getRouters().put("yozb_rtr", ParserFactory.generateParser(16));
		for(String rtrName: this.getRouters().keySet()) {
			System.out.println("\n--- initializing " + rtrName + " ---");
			this.getRouters().get(rtrName).read_arp_table_file("examples\\"+rtrName+"_arp_table.txt");
			this.getRouters().get(rtrName).read_mac_table_file("examples\\"+rtrName+"_mac_table.txt");
			this.getRouters().get(rtrName).read_config_file("examples\\"+rtrName+"_config.txt");
			this.getRouters().get(rtrName).read_spanning_tree_file("examples\\"+rtrName+"_spanning_tree.txt");
			this.getRouters().get(rtrName).read_route_file("examples\\"+rtrName+"_route.txt");
			this.getRouters().get(rtrName).optimize_forwarding_table();
			this.getRouters().get(rtrName).generate_port_ids(new HashSet<String>());
			this.getRouters().get(rtrName).generate_transfer_function(NTF);
			for(String portName : this.getRouters().get(rtrName).getPortToID().keySet()) {
				this.portToID.put(rtrName+"_"+portName, this.getRouters().get(rtrName).getPortToID().get(portName));
			}
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
	
	
	//TODO need change
	public void generateGraphFromLinks() {
		HashMap<String,GraphNode> existNode = new HashMap<String,GraphNode>();
		HashMap<String,HashSet<String>> routerToPort = new HashMap<String,HashSet<String>>();
		
		for(Link link: links) {
			if(routerToPort.containsKey(link.getDevice1())) {
				routerToPort.get(link.getDevice1()).add(link.getIface1());
			}else {
				routerToPort.put(link.getDevice1(), new HashSet<String>());
				routerToPort.get(link.getDevice1()).add(link.getIface1());
			}
			if(routerToPort.containsKey(link.getDevice2())) {
				routerToPort.get(link.getDevice2()).add(link.getIface2());
			}else {
				routerToPort.put(link.getDevice2(), new HashSet<String>());
				routerToPort.get(link.getDevice2()).add(link.getIface2());
			}
		}
		for(String routerName:routerToPort.keySet()) {
			for(String portName:routerToPort.get(routerName)) {
				existNode.put(routerName+"_"+portName, new GraphNode(this.portToID.get(routerName+"_"+portName)));
			}
			for(String portName:routerToPort.get(routerName)) {
				for(String otherPortName:routerToPort.get(routerName)) {
					if(portName.equals(otherPortName)) {
						continue;
					}else {
						existNode.get(routerName+"_"+portName).getAdjacent().add(existNode.get(routerName+"_"+otherPortName));
						existNode.get(routerName+"_"+otherPortName).getAdjacent().add(existNode.get(routerName+"_"+portName));
					}
				}
			}
		}
		for(Link link: links) {
			existNode.get(link.getDevice1()+"_"+link.getIface1()).getAdjacent().add(existNode.get(link.getDevice2()+"_"+link.getIface2()));
			existNode.get(link.getDevice2()+"_"+link.getIface2()).getAdjacent().add(existNode.get(link.getDevice1()+"_"+link.getIface1()));
		}
	}
	
	public Network(HashMap<String, Parser> routers, ArrayList<Link> links) {
		this.routers.putAll(routers);
		this.links.addAll(links);
	}
	
	public BasicTF getNTF() {
		return NTF;
	}
	public void setNTF(BasicTF nTF) {
		NTF = nTF;
	}
	public BasicTF getTTF() {
		return TTF;
	}
	public void setTTF(BasicTF tTF) {
		TTF = tTF;
	}
	public String getConfigDict() {
		return configDict;
	}
	public void setConfigDict(String configDict) {
		this.configDict = configDict;
	}
	public HashMap<String, Parser> getRouters() {
		return routers;
	}
	public void setRouters(HashMap<String, Parser> routers) {
		this.routers = routers;
	}
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	public int getport(String router, String Iface) {
		return routers.get(router).get_port_id(Iface);
	}
}