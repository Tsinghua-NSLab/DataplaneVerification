package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import bean.ACL;
import bean.Access;
import bean.Arp;
import bean.FIB;
import bean.Ip;
import bean.Port;
import bean.Subnet;
import bean.Vlan;
import device.RawDevice;
import hassel.bean.Wildcard;
import headers.ciscoHeader;
import interfaces.Device;
import interfaces.Parser;
import rules.Rule;
import tfFunction.tfFunction;
import utils.Helper;

public class CiscoParser implements Parser{
	int PORT_ID_MULTIPLIER = 1;
	int INTERMEDIATE_PORT_TYPE_CONST = 1;
	int OUTPUT_PORT_TYPE_CONST = 2;
	int PORT_TYPE_MULTIPLIER = 10000;
	int SWITCH_ID_MULTIPLIER = 100000;
	
	HashMap<Integer, ArrayList<ACL>> ACLList = new HashMap<Integer, ArrayList<ACL>>();
    HashMap<Integer, ArrayList<String>> vlanSpanPorts = new HashMap<Integer, ArrayList<String>>();
	ArrayList<FIB> fwdTable = new ArrayList<FIB>();
    HashMap<String, Arp> arpTable = new HashMap<String, Arp>();
    HashMap<String, ArrayList<String>> macTable = new HashMap<String, ArrayList<String>>();
    HashMap<Integer, ArrayList<Access>> ACLIface = new HashMap<Integer, ArrayList<Access>>();
    HashMap<Integer, Vlan> configedVlans = new HashMap<Integer, Vlan>();
    HashSet<String> configedPorts = new HashSet<String>();
    
    HashMap<String, Integer> portToID = new HashMap<String, Integer>();
    int switchID = -1;
    int defVlan = 1;
    //TODO remove temp instances
    ciscoHeader cHeader = new ciscoHeader();
    tfFunction tf = new tfFunction();
    
    public CiscoParser(int switchID) {
		super();
		this.switchID = switchID;
	}
    
    public void setDefaultVlan(int vlan) {
    	this.defVlan = vlan;
    }

	ArrayList<Integer> replacedVlan = new ArrayList<Integer>();
	@Override
	public void read_arp_table_file(String filename) {
		System.out.println("=== Reading Cisco Router ARP Table File ===");
	    FileReader fr;
		try {
			fr = new FileReader(filename);
			BufferedReader br=new BufferedReader(fr);
			String line=br.readLine();
			String[] arrs=null;
			while ((line=br.readLine())!=null) {
				arrs=line.split("\\s+");
				if(arrs.length >= 6&&arrs[4].toLowerCase().equals("arpa")) {
					arpTable.put(arrs[1].toLowerCase(), new Arp(arrs[3].toLowerCase(),arrs[5].toLowerCase()));
				}
				//System.out.println(arrs[0] + " : " + arrs[1] + " : " + arrs[2]);
			}
			br.close();
			fr.close();
	    } catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("=== DONE Reading Cisco Router ARP Table File ===");
	}

	@Override
	public void read_mac_table_file(String filename) {
		System.out.println("=== Reading Cisco Mac Address Table File ===");
	    FileReader fr;
		try {
			fr = new FileReader(filename);
			BufferedReader br=new BufferedReader(fr);
			String line=null;
			String[] arrs=null;
			boolean seen_star = false;
			ArrayList<String> ports = new ArrayList<String>();
			String mac = "";
			while (!(line=br.readLine()).startsWith("-"));
			while ((line=br.readLine())!=null) {
				arrs=line.split("\\s+");
				if(line.startsWith("*")) {
					if(seen_star) {
						macTable.put(mac, ports);
						ports = new ArrayList<String>();
					}
					mac = "vlan"+arrs[1]+","+arrs[2];
					seen_star = true;
					if(arrs.length >= 7) {
						String[] tempPorts = arrs[6].split(",");
						for(String port:tempPorts) {
							ports.add(port);
						}
					}
				}else if(seen_star) {
					String[] tempPorts = arrs[0].split(",");
					for(String port:tempPorts) {
						ports.add(port);
					}
				}
				macTable.put(mac, ports);
			}
			br.close();
			fr.close();
	    } catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("=== DONE Reading Cisco Mac Address Table File ===");
	}

	@Override
	public void read_config_file(String filename) {
		System.out.println("=== Reading Cisco Router Config File ===");
	    FileReader fr;
		try {
			fr = new FileReader(filename);
			BufferedReader br=new BufferedReader(fr);
			String line=null;
			String[] arrs=null;
			boolean reading_iface = false;
			ArrayList<String> iface_info = new ArrayList<String>();
			int line_counter = 0;
			while ((line=br.readLine())!=null) {
				line = line.trim();
				// read an access-list line
				if(line.startsWith("access-list")) {
					this.parse_access_list_entry(line, line_counter);
				}else if(line.startsWith("vlan")) {// define a VLAN
					arrs = line.split("\\s+");
					try {
						configedVlans.put(Integer.parseInt(arrs[1]), new Vlan());
					}catch(Exception e){
						String[] st = arrs[1].split("-");
						if(st.length > 1) {
							try {
								int s = Integer.parseInt(st[0]);
								int t = Integer.parseInt(st[1]);
								for(int i = s; i < t+1; i++) {
									configedVlans.put(i, new Vlan());
								}
							}catch(Exception pass) {
							}
						}
					}
				}else if(line.startsWith("interface")) {// read interface config
					reading_iface = true;
					iface_info.add(line);
				}else if(reading_iface) {
					iface_info.add(line);
					if(line.startsWith("!")) {
						reading_iface = false;
						this.parse_interface_config(iface_info, line_counter, filename);
						iface_info.clear();
					}
				}
				line_counter++;
			}
			br.close();
			fr.close();
	    } catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("=== DONE Reading Cisco Router Config File ===");
	}

	@Override
	public void read_spanning_tree_file(String filename) {
		System.out.println("=== Reading Cisco Router Spanning Tree File ===");
		int current_vlan = 0;
		FileReader fr;
		try {
			fr = new FileReader(filename);
			BufferedReader br=new BufferedReader(fr);
			String line=null;
			String[] arrs=null;
			while ((line=br.readLine())!=null) {
				arrs=line.split("\\s+");
				if(arrs.length == 0)continue;
				if(line.toLowerCase().startsWith("vlan")) {
					if(arrs.length == 1) {
						current_vlan = Integer.parseInt(arrs[0].substring(4));
						if(!vlanSpanPorts.containsKey(current_vlan)) {
							vlanSpanPorts.put(current_vlan, new ArrayList<String>());
						}
					}
				}else if(Helper.arrayContains(arrs, "FWD")||Helper.arrayContains(arrs, "fwd")) {
					String port = arrs[0].toLowerCase();
					if(!vlanSpanPorts.get(current_vlan).contains(port)) {
						vlanSpanPorts.get(current_vlan).add(port);
					}
				}
			}
			br.close();
			fr.close();
	    } catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("=== DONE Reading Cisco Router Spanning Tree File ===");
	}

	@Override
	public void read_route_file(String filename) {
		System.out.println("=== Reading Cisco Router IP CEF File ===");
	    FileReader fr;
		try {
			fr = new FileReader(filename);
			BufferedReader br=new BufferedReader(fr);
			String line=br.readLine();
			String[] arrs=null;
			String port = null;
			int lineCounter = 0;
			while ((line=br.readLine())!=null) {
				arrs=line.split("\\s+");
				if(arrs.length==0)continue;
				if(Helper.is_ip_subset(arrs[0])) {
					Ip ip_subnet = new Ip(arrs[0]);
					if(arrs.length > 2) {
						port = get_ethernet_port_name(arrs[2]);
						if(port.toLowerCase().startsWith("vlan")&&Helper.is_ip_address(arrs[1])) {
							if(arpTable.containsKey(arrs[1])) {
								Arp tempArp = arpTable.get(arrs[1]);
								if(tempArp.getVlan().startsWith("vlan")) {
									String vmKey = tempArp.getVlan()+","+tempArp.getMac();
									if(macTable.containsKey(vmKey)) {
										String resolvedPort = macTable.get(vmKey).get(0);
										Integer vlanNum = Integer.parseInt(tempArp.getVlan().substring(4));
										port = get_ethernet_port_name(resolvedPort)+"."+vlanNum;
									}
								}else {
									port = get_ethernet_port_name(tempArp.getVlan());
								}
							}
						}else if(port.toLowerCase().startsWith("vlan")) {
							int vlan = Integer.parseInt(port.substring(4));
						}else {
							String[] parts = port.split("\\.");
							if(parts.length > 1&&replacedVlan.size()>1&&Integer.parseInt(parts[1])==replacedVlan.get(0)) {
								port = parts[0] + "." + replacedVlan.get(1);
								int vlan = replacedVlan.get(1);
							}else if(parts.length > 1) {
								int vlan = Integer.parseInt(parts[1]);
							}
						}
					}else {
						port = "self";
					}
					if(port.toLowerCase().startsWith("loopback")||port.toLowerCase().startsWith("null")||arrs[1].toLowerCase().startsWith("drop")) {
						port = "self";
					}
					fwdTable.add(new FIB(ip_subnet, port.toLowerCase(), filename, lineCounter ));
				}
				lineCounter++;
			}
			br.close();
			fr.close();
	    } catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("=== DONE Reading Cisco Router IP CEF File ===");
	}
	
	public void generate_port_ids(HashSet<String> additionalPorts) {
		System.out.println("=== Generating port IDs ===");
		for(String port: configedPorts) {
			additionalPorts.add(port);
		}
		for(int vlan: vlanSpanPorts.keySet()) {
			for(String port: vlanSpanPorts.get(vlan)) {
				additionalPorts.add(port);
			}
		}
		int suffix = 1;
		for(String port: additionalPorts) {
			int id = this.switchID*this.SWITCH_ID_MULTIPLIER + suffix*this.PORT_ID_MULTIPLIER;
			this.portToID.put(port, id);
			suffix++;
		}
		System.out.println("=== DONE generating port IDs ===");
	}
	
	public int get_port_id(String portName) {
		if(this.portToID.keySet().contains(portName)) {
			return portToID.get(portName);
		}
		return -1;
	}
	
	public void generate_transfer_function() {
		System.out.println("=== Generating Transfer Function ===");
		System.out.println(" * Generating ACL transfer function * ");
		for(int acl: this.ACLIface.keySet()) {
			if(!this.ACLList.containsKey(acl)) {
				continue;
			}
			for(Access aclInstance: this.ACLIface.get(acl)) {
				String filename = aclInstance.getFile();
				ArrayList<Integer> trunkPorts = new ArrayList<Integer>();
				ArrayList<Integer> accessPorts = new ArrayList<Integer>();
				int vlan = aclInstance.getVlan();
				if(!this.configedVlans.containsKey(aclInstance.getVlan())) {
					continue;
				}
				if(aclInstance.getIface().startsWith("vlan")) {
					int vlanInt = Integer.parseInt(aclInstance.getIface().substring(4));
					for(String p: this.vlanSpanPorts.get(vlanInt)) {
						trunkPorts.add(portToID.get(p));
					}
					for(String p: this.configedVlans.get(aclInstance.getVlan()).getAccess()) {
						Integer pid = portToID.get(p);
						accessPorts.add(pid);
						if(trunkPorts.contains(pid)) {
							trunkPorts.remove(pid);
						}
					}
				}else {
					accessPorts.add(portToID.get(aclInstance.getIface()));
				}
				for(ACL aclDicEntry:this.ACLList.get(acl)) {
					ArrayList<Wildcard> matches = this.acl_dict_entry_to_wc(aclDicEntry);
					ArrayList<Integer> lines = new ArrayList<Integer>();
					lines.add(aclInstance.getLine());
					lines.add(aclDicEntry.getLine());
					//In ACL entries
					if(aclInstance.isInout()) {
						ArrayList<Integer> inPorts = trunkPorts;
						ArrayList<Integer> outPorts = new ArrayList<Integer>();
						if(aclDicEntry.isAction()) {
							outPorts.add(this.switchID*this.SWITCH_ID_MULTIPLIER);
						}
						for(Wildcard match: matches) {
							//IN ACL for VLAN tagged packets going to trunk or access ports
							match.setField(cHeader.getFormat(), "vlan", vlan, 0);
							//Rule nextRule = new Rule(inPorts,match,outPorts,null,null,filename,lines);
							Rule nextRule = new Rule(inPorts,match,outPorts,null,null);
							
							tf.addFwdRule(nextRule);
							//IN ACL for un-vlan tagged packets received on
							//access ports. If there is any access port, we 
							//should accept untagged packets, and tag them 
							//with the corresponding VLAN tag.
							if(accessPorts.size()>0) {
								match.setField(cHeader.getFormat(), "vlan", 0, 0);
								Wildcard mask = null;
								Wildcard rewrite = null;
								if(vlan!=-1) {
									mask = new Wildcard(cHeader.getFormat().get("length"),'1');
									rewrite = new Wildcard(cHeader.getFormat().get("length"),'0');
									mask.setField(cHeader.getFormat(), "vlan", 0, 0);
									rewrite.setField(cHeader.getFormat(), "vlan", vlan, 0);
								}
								//nextRule = new Rule(accessPorts, match, outPorts, mask, rewrite, filename, lines);
								nextRule = new Rule(accessPorts, match, outPorts, mask, rewrite);
								tf.addFwdRule(nextRule);
							}
						}// *** OUT ACL ENTRIES
					}else if((!aclInstance.isInout())&&vlan!=-1) {
						for(Wildcard match: matches) {
							match.setField(cHeader.getFormat(), "vlan", vlan, 0);
							if(!aclDicEntry.isAction()) {
								ArrayList<Integer> outPorts = new ArrayList<Integer>();
								ArrayList<Integer> inPorts = new ArrayList<Integer>();
								for(int port: trunkPorts) {
									inPorts.add(port+this.PORT_TYPE_MULTIPLIER*this.INTERMEDIATE_PORT_TYPE_CONST);
								}
								for(int port: accessPorts) {
									inPorts.add(port+this.PORT_TYPE_MULTIPLIER*this.INTERMEDIATE_PORT_TYPE_CONST);
								}
								//Rule nextRule = new Rule(inPorts, match,outPorts,null,null,filename,lines);
								Rule nextRule = new Rule(inPorts, match,outPorts,null,null);
								tf.addFwdRule(nextRule);
							}else {
								for(int port: trunkPorts) {
									ArrayList<Integer> outPorts = new ArrayList<Integer>();
									ArrayList<Integer> inPorts = new ArrayList<Integer>();
									inPorts.add(port+this.PORT_TYPE_MULTIPLIER*this.INTERMEDIATE_PORT_TYPE_CONST);
									outPorts.add(port + this.PORT_TYPE_MULTIPLIER*this.OUTPUT_PORT_TYPE_CONST);
									//Rule nextRule = new Rule(inPorts, match, outPorts, null, null, filename, lines);
									Rule nextRule = new Rule(inPorts, match, outPorts, null, null);
									tf.addFwdRule(nextRule);
								}
								for(int port: accessPorts) {
									ArrayList<Integer> outPorts = new ArrayList<Integer>();
									ArrayList<Integer> inPorts = new ArrayList<Integer>();
									inPorts.add(port+this.PORT_TYPE_MULTIPLIER*this.INTERMEDIATE_PORT_TYPE_CONST);
									outPorts.add(port + this.PORT_TYPE_MULTIPLIER*this.OUTPUT_PORT_TYPE_CONST);
									Wildcard mask = new Wildcard(cHeader.getFormat().get("length"),'1');
									Wildcard rewrite = new Wildcard(cHeader.getFormat().get("length"),'0');
									//TODO why here 0 and vlan before
									mask.setField(cHeader.getFormat(), "vlan", 0, 0);
									rewrite.setField(cHeader.getFormat(), "vlan", 0, 0);
									//Rule nextRule = new Rule(inPorts, match, outPorts, mask, rewrite, filename, lines);
									Rule nextRule = new Rule(inPorts, match, outPorts, mask, rewrite);
									tf.addFwdRule(nextRule);
								}
							}
						}// ** OUT ACL for non-vlan port
					}else if((!aclInstance.isInout())&&vlan==-1) {
						for(Wildcard match: matches) {
							for(int port: accessPorts) {
								// If sending out from an access port, strip the VLAN tag
								ArrayList<Integer> outPorts = new ArrayList<Integer>();
								ArrayList<Integer> inPorts = new ArrayList<Integer>();
								inPorts.add(port+this.PORT_TYPE_MULTIPLIER*this.INTERMEDIATE_PORT_TYPE_CONST);
								outPorts.add(port + this.PORT_TYPE_MULTIPLIER*this.OUTPUT_PORT_TYPE_CONST);
								if(!aclDicEntry.isAction()) {
									outPorts.clear();
								}
								//Rule nextRule = new Rule(inPorts, match, outPorts, null, null, filename, lines);
								Rule nextRule = new Rule(inPorts, match, outPorts, null, null);
								tf.addFwdRule(nextRule);
							}
						}
					}
				}
			}
		}
		//*** default rule for all vlans configured on this switch
		HashSet<String> allAccessPorts = new HashSet<String>();
		ArrayList<Integer> intermediatePort = new ArrayList<Integer>();
		intermediatePort.add(this.switchID*this.SWITCH_ID_MULTIPLIER);
		for(int cnfVlan:this.configedVlans.keySet()) {
			if(!this.vlanSpanPorts.containsKey(cnfVlan)) {
				continue;
			}
			ArrayList<Integer> trunkPorts = new ArrayList<Integer>();
			ArrayList<Integer> accessPorts = new ArrayList<Integer>();
			for(String p: this.vlanSpanPorts.get(cnfVlan)) {
				trunkPorts.add(portToID.get(p));
			}
			for(String p: this.configedVlans.get(cnfVlan).getAccess()) {
				Integer pid = portToID.get(p);
				accessPorts.add(pid);
				allAccessPorts.add(p);
				if(trunkPorts.contains(pid)) {
					trunkPorts.remove(pid);
				}
			}
			//default rule for vlan tagged packets received on trunk port
			Wildcard match = new Wildcard(cHeader.getFormat().get("length"),'x');
			match.setField(cHeader.getFormat(), "vlan", cnfVlan, 0);
			//Rule defRule = new Rule(trunkPorts, match, intermediatePort, null, null, "", new ArrayList<Integer>());
			Rule defRule = new Rule(trunkPorts, match, intermediatePort, null, null);
			tf.addFwdRule(defRule);
			
			//default rule for un-vlan tagged packets received on access port
			if(accessPorts.size()>0) {
				match = new Wildcard(cHeader.getFormat().get("length"),'x');
				Wildcard mask = new Wildcard(cHeader.getFormat().get("length"),'1');
				Wildcard rewrite = new Wildcard(cHeader.getFormat().get("length"),'0');
				match.setField(cHeader.getFormat(), "vlan", 0, 0);
				mask.setField(cHeader.getFormat(), "vlan", 0, 0);
				rewrite.setField(cHeader.getFormat(), "vlan", cnfVlan, 0);
				//defRule = new Rule(accessPorts, match, intermediatePort, mask, rewrite,"", new ArrayList<Integer>());
				defRule = new Rule(accessPorts, match, intermediatePort, mask, rewrite);
				tf.addFwdRule(defRule);
			}
			
			//default rules for vlan-tagged outgoing packets on an access port
			for(int portID: accessPorts) {
				match = new Wildcard(cHeader.getFormat().get("length"),'x');
				Wildcard mask = new Wildcard(cHeader.getFormat().get("length"),'1');
				Wildcard rewrite = new Wildcard(cHeader.getFormat().get("length"),'0');
				match.setField(cHeader.getFormat(), "vlan", cnfVlan, 0);
				mask.setField(cHeader.getFormat(), "vlan", 0, 0);
				rewrite.setField(cHeader.getFormat(), "vlan", 0, 0);
				ArrayList<Integer> beforeOutPort = new ArrayList<Integer>();
				ArrayList<Integer> afterOutPort = new ArrayList<Integer>();
				beforeOutPort.add(portID+ this.PORT_TYPE_MULTIPLIER*this.INTERMEDIATE_PORT_TYPE_CONST);
				afterOutPort.add(portID + this.PORT_TYPE_MULTIPLIER*this.OUTPUT_PORT_TYPE_CONST);
				//defRule = new Rule(beforeOutPort, match, afterOutPort, mask, rewrite, "", new ArrayList<Integer>());
				defRule = new Rule(beforeOutPort, match, afterOutPort, mask, rewrite);
				tf.addRewriteRule(defRule);
			}
		}
		//default rules for any outgoing packets on a non-access port
		for(String port: portToID.keySet()) {
			if((!port.equals("self"))&&(!allAccessPorts.contains(port))) {
				int portID = portToID.get(port);
				Wildcard match = new Wildcard(cHeader.getFormat().get("length"),'x');
				ArrayList<Integer> beforeOutPort = new ArrayList<Integer>();
				ArrayList<Integer> afterOutPort = new ArrayList<Integer>();
				beforeOutPort.add(portID+this.PORT_TYPE_MULTIPLIER*this.INTERMEDIATE_PORT_TYPE_CONST);
				afterOutPort.add(portID+this.PORT_TYPE_MULTIPLIER*this.OUTPUT_PORT_TYPE_CONST);
				//Rule defRule = new Rule(beforeOutPort, match, afterOutPort, null, null, "", new ArrayList<Integer>());
				Rule defRule = new Rule(beforeOutPort, match, afterOutPort, null, null);
				tf.addFwdRule(defRule);
			}
		}
		//default rule for unvlan-tagged packets received on an trunk port 
		if(this.configedVlans.containsKey(defVlan)) {
			for(String port: this.portToID.keySet()) {
				if((!port.equals("self"))&&(!allAccessPorts.contains(port))) {
					int portID = this.portToID.get(port);
					Wildcard match = new Wildcard(this.cHeader.getFormat().get("length"),'x');
					Wildcard mask = new Wildcard(this.cHeader.getFormat().get("length"),'1');
					Wildcard rewrite = new Wildcard(this.cHeader.getFormat().get("length"),'0');
					match.setField(cHeader.getFormat(), "vlan", 0, 0);
					mask.setField(cHeader.getFormat(), "vlan", 0, 0);
					rewrite.setField(cHeader.getFormat(), "vlan", defVlan, 0);
					ArrayList<Integer> portIDArray = new ArrayList<Integer>();
					portIDArray.add(portID);
					//Rule defRule = new Rule(portIDArray, match, intermediatePort,mask,rewrite,"",new ArrayList<Integer>());
					Rule defRule = new Rule(portIDArray, match, intermediatePort,mask,rewrite);
					tf.addFwdRule(defRule);
				}
			}
		}
		System.out.println(" * Generating IP forwarding transfer function... *");
		int testIndex = 0;
		for(int subnet = 0; subnet<=32; subnet++) {
			for(FIB fwdRule:this.fwdTable) {
				if(fwdRule.getIP().getSubnet()==subnet) {
					testIndex++;
					if(testIndex%100==0) {
						System.out.println(testIndex);
					}
					//in -ports and match wildcard
					Wildcard match = new Wildcard(cHeader.getFormat().get("length"),'x');
					Wildcard mask = new Wildcard(cHeader.getFormat().get("length"),'1');
					Wildcard rewrite = new Wildcard(cHeader.getFormat().get("length"),'0');
					match.setField(cHeader.getFormat(), "ip_dst", fwdRule.getIP().getIP(), subnet);
					ArrayList<Integer> inPorts = new ArrayList<Integer>();
					inPorts.add(this.switchID*this.SWITCH_ID_MULTIPLIER);
					//ArrayList<Integer> lines = new ArrayList<Integer>();
					//lines.add(fwdRule.getLineCounter());
					//String filename = fwdRule.getFileName();
					// set up out ports
					ArrayList<Integer> outPorts = new ArrayList<Integer>();
					int vlan = 0;
					String[] m = fwdRule.getPort().split("\\.");
					// drop rules
					if(fwdRule.getPort().equals("self")) {
						//Rule selfRule = new Rule(inPorts, match, outPorts, null,null,filename,lines);
						Rule selfRule = new Rule(inPorts, match, outPorts, null,null);
						tf.addFwdRule(selfRule);
					}else {//non drop rules
						// sub-ports: port.vlan
						if(m.length>1) {
							if(this.portToID.containsKey(m[0])){
								outPorts.add(portToID.get(m[0])+this.PORT_TYPE_MULTIPLIER*this.OUTPUT_PORT_TYPE_CONST);
								vlan = Integer.parseInt(m[1]);
							}else {
								System.out.print("error: unrecognized port "+m[0]);
								return;
							}
						}else if(fwdRule.getPort().startsWith("vlan")){//vlan outputs
							vlan = Integer.parseInt(fwdRule.getPort().substring(4));
							if(this.vlanSpanPorts.containsKey(vlan)) {
								ArrayList<String> portList = this.vlanSpanPorts.get(vlan);
								for(String p: portList) {
										outPorts.add(this.portToID.get(p)+this.PORT_TYPE_MULTIPLIER*this.OUTPUT_PORT_TYPE_CONST);
								}
							}else {
								System.out.print("error: unrecognized vlan "+vlan);
								return;
							}
						}else {//physical ports - no vlan taging
							if(this.portToID.containsKey(fwdRule.getPort())) {
								outPorts.add(this.portToID.get(fwdRule.getPort())+this.PORT_TYPE_MULTIPLIER*this.OUTPUT_PORT_TYPE_CONST);
								vlan = 0;
							}else {
								System.out.print("error: unrecognized port "+fwdRule.getPort());
								return;
							}
						}
						mask.setField(cHeader.getFormat(), "vlan", 0, 0);
						rewrite.setField(cHeader.getFormat(), "vlan", vlan, 0);
						//Rule tfRule = new Rule(inPorts,match,outPorts,null,null,filename,lines);
						Rule tfRule = new Rule(inPorts,match,outPorts,null,null);
						tf.addFwdRule(tfRule);
					}
				}
			}
		}
		System.out.println("=== Successfully Generated Transfer function ===");
	}
	
	public boolean parse_access_list_entry(String line, int line_counter) {
		String[] arrs = line.split("\\s+");
		LinkedList<String> arrs_linked = new LinkedList<String>();
		for(String arr:arrs) {
			arrs_linked.add(arr);
		}
		arrs_linked.remove(0);
		int acl_number = Integer.parseInt(arrs_linked.get(0));
		arrs_linked.remove(0);
		String action = arrs_linked.get(0);
		arrs_linked.remove(0);
		if(action.toLowerCase().equals("permit")||action.toLowerCase().equals("deny")) {
			if(!ACLList.keySet().contains(acl_number)) {
				ACLList.put(acl_number, new ArrayList<ACL>());
			}
			ACL new_entry = new ACL();
			new_entry.setAction(action.toLowerCase().equals("permit"));
			if(acl_number<100) {// standard access-list entry
				new_entry.setIp_protocol(0);
				Ip new_ip = parse_ip(arrs_linked);
				if(new_ip.getIP()!=-1||new_ip.getSubnet()!=-1) {
					new_entry.setSrc_ip(new_ip.getIP());
					new_entry.setSrc_ip_mask(new_ip.getSubnet());
					ACLList.get(acl_number).add(new_entry);
					return true;
				}else {
					return false;
				}
			}else {// extended access-list entry
				if(get_protocol_number(arrs_linked.get(0))!=-1) {
					new_entry.setIp_protocol(get_protocol_number(arrs_linked.get(0)));
					arrs_linked.remove(0);
				}else if(Helper.is_ip_address(arrs_linked.get(0))) {
					new_entry.setIp_protocol(0);
				}else {
					return false;
				}
				
				//src ip address and ip mask
				Ip new_ip = parse_ip(arrs_linked);
				if(new_ip.getIP()!=-1||new_ip.getSubnet()!=-1) {
					new_entry.setSrc_ip(new_ip.getIP());
					new_entry.setSrc_ip_mask(new_ip.getSubnet());
				}
				
				//src transport port number
				if(arrs_linked.size()>0) {
					Port new_ports = parse_port(arrs_linked, new_entry.getIp_protocol());
					if(new_ports.getPort_begin()!=-1||new_ports.getPort_end()!=-1) {
						new_entry.setTransport_src_begin(new_ports.getPort_begin());
						new_entry.setTransport_src_end(new_ports.getPort_end());
					}
				}
				
				//dst ip address and ip mask
				if(arrs_linked.size()>0) {
					new_ip = parse_ip(arrs_linked);
					if(new_ip.getIP()!=-1||new_ip.getSubnet()!=-1) {
						new_entry.setDst_ip(new_ip.getIP());
						new_entry.setDst_ip_mask(new_ip.getSubnet());
					}
				}
				
				//dst transport port number
				if(arrs_linked.size()>0) {
					Port new_ports = parse_port(arrs_linked, new_entry.getIp_protocol());
					if(new_ports.getPort_begin()!=-1||new_ports.getPort_end()!=-1) {
						new_entry.setTransport_dst_begin(new_ports.getPort_begin());
						new_entry.setTransport_dst_end(new_ports.getPort_end());
					}
				}
				
				//tranport control bits
				if(arrs_linked.size()>0) {
					String t = arrs_linked.get(0);
					arrs_linked.remove(0);
					if(t.equals("established")) {
						new_entry.setTransport_ctrl_begin(0x80);
						new_entry.setTransport_ctrl_end(0xff);
					}
				}
				
				new_entry.setLine(line_counter);
				ACLList.get(acl_number).add(new_entry);
				return true;
			}
		}
		return false;
	}
	
	public void parse_interface_config(ArrayList<String> iface_info, int line_counter, String file_path) {
		String[] arrs = iface_info.get(0).split("\\s+");
		String iface = get_ethernet_port_name(arrs[1].toLowerCase());
		int vlan = -1;
		if(iface.startsWith("vlan")) {//vlan port
			vlan = Integer.parseInt(iface.substring(4));
		}else {
			String[] parts = iface.split("\\.");
			if(parts.length > 1) {//virtual port
				vlan = Integer.parseInt(parts[1]);
				iface = parts[0];
				if(!configedVlans.containsKey(vlan)) {
					configedVlans.put(vlan, new Vlan());
					configedVlans.get(vlan).getTrunk().add(iface);
				}else {
					configedVlans.get(vlan).getTrunk().add(iface);
				}
				if(!vlanSpanPorts.containsKey(vlan)) {
					vlanSpanPorts.put(vlan, new ArrayList<String>());
					vlanSpanPorts.get(vlan).add(iface);
				}else {
					vlanSpanPorts.get(vlan).add(iface);
				}
			}
			configedPorts.add(iface);
		}
		boolean shutdown = false;
		ArrayList<String> vlanRanges = new ArrayList<String>();
		int accessVlan = -1;
		String portMode = "";
		for(int i = 0; i < iface_info.size(); i++) {
			String tempLine = iface_info.get(i);
			int tempCounter = line_counter-iface_info.size() + i;
			if(tempLine.startsWith("shutdown")) {
				shutdown = true;
			}else if(tempLine.startsWith("switchport mode")) {
				String[] tempArrs = tempLine.split("\\s+");
				portMode = tempArrs[2];
			}else if(tempLine.startsWith("ip access-group")) {
				String[] tempArrs = tempLine.split("\\s+");
				int groupNum = Integer.parseInt(tempArrs[2]);
				if(!ACLIface.containsKey(groupNum)) {
					ACLIface.put(groupNum, new ArrayList<Access>());
				}	
				ACLIface.get(groupNum).add(new Access(iface, tempArrs[3].equals("in"), vlan, file_path, tempCounter));
			}else if(tempLine.startsWith("switchport trunk allowed vlan")) {
				String[] tempArrs = tempLine.split("\\s+");
				String allowed = tempArrs[tempArrs.length-1];
				if(!allowed.toLowerCase().equals("none")) {
					String[] ranges = allowed.split(",");
					for(String range: ranges) {
						vlanRanges.add(range);
					}
				}
			}else if(tempLine.startsWith("switchport access vlan")) {
				String[] tempArrs = tempLine.split("\\s+");
				accessVlan = Integer.parseInt(tempArrs[tempArrs.length-1]);
			}
		}
		if(shutdown) {
			if(vlan != -1) {
				if(configedVlans.containsKey(vlan)) {
					configedVlans.remove(vlan);
				}
			}else {
				configedPorts.remove(iface);
			}
		}else if(portMode.equals("access")&&accessVlan!=-1) {
			configedVlans.get(accessVlan).getAccess().add(iface);
		}else if(portMode.equals("trunk")) {
			for(int v : configedVlans.keySet()) {
				for(String range:vlanRanges) {
					if(is_in_range(range,v)) {
						configedVlans.get(v).getTrunk().add(iface);
						break;
					}
				}
			}
		}
	}
	
	public void optimize_forwarding_table() {
		System.out.println("=== Compressing forwarding table ===");
		System.out.println(" * Originally has " + this.fwdTable.size() + " ip fwd entries * ");
		ArrayList<FIB> n = Helper.compress_ip_list(fwdTable);
		System.out.println(" * After compression has " + n.size() + " ip fwd entries * ");
		this.fwdTable = n;
		System.out.println("=== DONE forwarding table compression ===");
	}
	
	public Ip parse_ip(LinkedList<String> arrs) {
		Ip result = new Ip();
		if(arrs.get(0).toLowerCase().equals("any")) {
			result.setIP(0);
			//result.setSubnet(0xffffffff);
			result.setSubnet(32);
			arrs.remove(0);
		}else if(arrs.get(0).toLowerCase().equals("host")) {
			result.setIP(arrs.get(1));
			//result.setIPMask(0);
			result.setSubnet(0);
			arrs.remove(0);
			arrs.remove(0);
		}else if(Helper.is_ip_address(arrs.get(0))) {
			result.setIP(arrs.get(0));
			if(arrs.size()>1&&Helper.is_ip_address(arrs.get(1))) {
				result.setACLSubnet(arrs.get(1));
				arrs.remove(0);
				arrs.remove(0);
			}else {
				result.setSubnet(0);
				arrs.remove(0);
			}
		}
		return result;
	}
	
	public Port parse_port(LinkedList<String> arrs,int proto) {
		Port result = new Port();
		if(arrs.get(0).equals("eq")) {
			arrs.remove(0);
			if(proto == 17) {
				result.setPort_begin(get_udp_port_number(arrs.get(0)));
			}else{
				result.setPort_begin(get_transport_port_number(arrs.get(0)));
			}
			result.setPort_end(result.getPort_begin());
			arrs.remove(0);
		}else if(arrs.get(0).equals("gt")) {
			arrs.remove(0);
			if(proto == 17) {
				result.setPort_begin(get_udp_port_number(arrs.get(0))+1);
			}else{
				result.setPort_begin(get_transport_port_number(arrs.get(0))+1);
			}
			result.setPort_end(0xffff);
			arrs.remove(0);
		}else if(arrs.get(0).equals("range")) {
			arrs.remove(0);
			if(proto == 17) {
				result.setPort_begin(get_udp_port_number(arrs.get(0)));
				result.setPort_end(get_udp_port_number(arrs.get(1)));
			}else{
				result.setPort_begin(get_transport_port_number(arrs.get(0)));
				result.setPort_end(get_transport_port_number(arrs.get(1)));
			}
			arrs.remove(0);
			arrs.remove(0);
		}
		return result;
	}
	
	public boolean is_in_range(String range,int val) {
		String[] arrs = range.split("-");
		if(arrs.length > 1 && val >= Integer.parseInt(arrs[0]) && val <= Integer.parseInt(arrs[1])) {
			return true;
		}else if(arrs.length == 1 && val == Integer.parseInt(arrs[0])) {
			return true;
		}else {
			return false;
		}
	}
	
	public String get_ethernet_port_name(String port) {
		if(port.toLowerCase().startsWith("tengigabitethernet")) {
			return "te" + port.substring(18);
		}else if(port.toLowerCase().startsWith("gigabitethernet")) {
			return "gi" + port.substring(15);
		}else if(port.toLowerCase().startsWith("fastethernet")) {
			return "fa" + port.substring(12);
		}else {
			return port;
		}
	}
	
	public int get_protocol_number(String proto_name) {
		switch(proto_name) {
		case "ah": return 51;
		case "eigrp": return 88;
		case "esp": return 50;
		case "gre": return 47;
		case "icmp": return 1;
		case "igmp": return 2;
		case "igrp": return 9;
		case "ip": return 0;
		case "ipinip": return 94;
		case "nos": return 4;
		case "ospf": return 89;
		case "tcp": return 6;
		case "udp": return 17;
		}
		try {
			return Integer.parseInt(proto_name);
		}catch(Exception e) {
			
		}
		return -1;
	}
	
	public int get_udp_port_number(String port_name) {
		switch(port_name) {
		case "biff": return 512;
		case "bootpc": return 68;
		case "bootps": return 69;
		case "discard": return 9;
		case "domain": return 53;
		case "dnsix": return 90;
		case "echo": return 7;
		case "mobile-ip": return 434;
		case "nameserver": return 42;
		case "netbios-dgm": return 137;
		case "netbios-ns": return 138;
		case "ntp": return 123;
		case "rip": return 520;
		case "snmp": return 161;
		case "snmptrap": return 162;
		case "sunrpc": return 111;
		case "syslog": return 514;
		case "tacacs-ds": return 49;
		case "talk": return 517;
		case "tftp": return 69;
		case "time": return 37;
		case "who": return 513;
		case "xdmcp": return 177;
		}
		try {
			return Integer.parseInt(port_name);
		}catch(Exception e) {
			
		}
		return -1;
	}
	
	public int get_transport_port_number(String port_name) {
		switch(port_name) {
		case "bgp": return 179;
		case "chargen": return 19;
		case "daytime": return 13;
		case "discard": return 9;
		case "domain": return 53;
		case "echo": return 7;
		case "finger": return 79;
		case "ftp": return 21;
		case "ftp-data": return 20;
		case "gopher": return 70;
		case "hostname": return 101;
		case "irc": return 194;
		case "klogin": return 543;
		case "kshell": return 544;
		case "lpd": return 515;
		case "nntp": return 119;
		case "pop2": return 109;
		case "pop3": return 110;
		case "smtp": return 25;
		case "sunrpc": return 111;
		case "syslog": return 514;
		case "tacacs-ds": return 65;
		case "talk": return 517;
		case "telnet": return 23;
		case "time": return 37;
		case "uucp": return 540;
		case "whois": return 43;
		case "www": return 80;
		}
		try {
			return Integer.parseInt(port_name);
		}catch(Exception e) {
			
		}
		return -1;
	}
	
	public ArrayList<Wildcard> acl_dict_entry_to_wc(ACL dicEntry) {
		ArrayList<Wildcard> result = new ArrayList<Wildcard>();
		result.add(new Wildcard(cHeader.getFormat().get("length"),'x'));
		if(dicEntry.getIp_protocol()!=0) {
			result.get(0).setField(cHeader.getFormat(), "ip_proto", dicEntry.getIp_protocol(), 0);
		}
		result.get(0).setField(cHeader.getFormat(), "ip_src", dicEntry.getSrc_ip(), dicEntry.getSrc_ip_mask());
		result.get(0).setField(cHeader.getFormat(), "ip_dst", dicEntry.getDst_ip(), dicEntry.getDst_ip_mask());

		//tp_src
		HashMap<Integer,Integer> tpSrcMatches = Helper.range_to_wildcard(dicEntry.getTransport_src_begin(), dicEntry.getTransport_src_end(), 16);
		ArrayList<Wildcard> tmp = new ArrayList<Wildcard>();
		for(int tpSrcKey: tpSrcMatches.keySet()) {
			Wildcard w = new Wildcard(result.get(0));
			w.setField(cHeader.getFormat(), "transport_src", tpSrcKey, tpSrcMatches.get(tpSrcKey));
			tmp.add(w);
		}
		result = tmp;

		//tp_dst
		HashMap<Integer,Integer> tpDstMatches = Helper.range_to_wildcard(dicEntry.getTransport_dst_begin(), dicEntry.getTransport_dst_end(), 16);
		tmp = new ArrayList<Wildcard>();
		for(int tpDstKey: tpDstMatches.keySet()) {
			for(Wildcard r: result) {
				Wildcard w = new Wildcard(r);
				w.setField(cHeader.getFormat(), "transport_dst", tpDstKey, tpDstMatches.get(tpDstKey));
				tmp.add(w);
			}
		}
		result = tmp;
		
		//tp_ctrl
		HashMap<Integer,Integer> tpCtrlMatches = Helper.range_to_wildcard(dicEntry.getTransport_ctrl_begin(), dicEntry.getTransport_ctrl_end(), 8);
		tmp = new ArrayList<Wildcard>();
		for(int tpCtrlKey: tpCtrlMatches.keySet()) {
			for(Wildcard r: result) {
				Wildcard w = new Wildcard(r);
				w.setField(cHeader.getFormat(), "transport_ctrl", tpCtrlKey, tpCtrlMatches.get(tpCtrlKey));
				tmp.add(w);
			}
		}
		result = tmp;
		return result;
	}
	
	public Device generateDevice() {
		return new RawDevice(this.portToID, this.tf);
	}
	
	public static void main(String args[]) {
		CiscoParser cp = new CiscoParser(1);
		cp.read_arp_table_file("examples\\yoza_rtr_arp_table.txt");
		cp.read_mac_table_file("examples\\yoza_rtr_mac_table.txt");
		cp.read_config_file("examples\\yoza_rtr_config.txt");
		cp.read_spanning_tree_file("examples\\yoza_rtr_spanning_tree.txt");
		cp.read_route_file("examples\\yoza_rtr_route.txt");
		cp.optimize_forwarding_table();
		cp.generate_port_ids(new HashSet<String>());
		cp.generate_transfer_function();
		System.out.println(cp.get_transport_port_number("syslog"));
	}
}