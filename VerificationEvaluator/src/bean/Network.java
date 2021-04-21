package bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import bean.basis.BasicTF;
import bean.basis.Rule;
import factory.HeaderFactory;
import factory.ParserFactory;
import factory.RuleFactory;
import interfaces.Header;
import interfaces.Parser;
import utils.General;

public class Network implements Serializable{
	public ArrayList<Integer> hostIDs = new ArrayList<Integer>();//Test variable
	ArrayList<Link> links = new ArrayList<Link>();
	ArrayList<GraphNode> graph = new ArrayList<GraphNode>();
	HashMap<String, Parser> routers = new HashMap<String, Parser>();
	HashMap<String, Integer> portToID = new HashMap<String, Integer>(); 
	BasicTF NTF = new BasicTF();
	BasicTF TTF = new BasicTF();
	String configDict = "examples\\";
	
	public Network() {
		
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
	
	public void importFromFile(String FileName) {
		FileReader fr;
		try {
			fr = new FileReader(FileName);
			BufferedReader br=new BufferedReader(fr);
			String line=br.readLine();
			String[] arrs=null;
			while ((line=br.readLine())!=null) {
				if(line.startsWith("fwd")) {
					Rule rule = new Rule();
					int index = line.indexOf("]");
					index = line.indexOf("]", index+1);
					String meaningful = line.substring(0, index+1);
					arrs = meaningful.split("\\$");
					String inportsString = arrs[1];
					String matchString = arrs[2].substring(1, arrs[2].length()-1);
					String outportString = arrs[3];
					rule.setInPorts(General.string2Array(inportsString));
					rule.setOutPorts(General.string2Array(outportString));
					Header ip = HeaderFactory.generateHeader(matchString);
					rule.setMatch(ip);
					this.NTF.addFwdRule(rule);
				}else if(line.startsWith("link")) {
					Rule rule = new Rule();
					arrs = line.split("\\$");
					String inportsString = arrs[1];
					String outportString = arrs[2];
					rule.setInPorts(General.string2Array(inportsString));
					rule.setOutPorts(General.string2Array(outportString));
					this.TTF.addLinkRule(rule);
				}
			}
			br.close();
			fr.close();
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void outputToFile(String FileName) {
		try {
            BufferedWriter out = new BufferedWriter(new FileWriter(FileName));
            for(Rule rule : this.NTF.rules) {
            	out.write("fwd$" + rule.toString() + "\n");
    		}
    		for(Rule rule : this.TTF.rules) {
    			out.write("link$" + rule.toString() + '\n');
    		}
    		out.close();
            System.out.println("Network output success");
        } catch (IOException e) {
        }
		
	}
}