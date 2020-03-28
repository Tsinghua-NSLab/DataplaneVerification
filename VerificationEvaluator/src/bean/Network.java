package bean;

import java.util.ArrayList;
import java.util.HashMap;

import interfaces.Device;

public class Network{
	ArrayList<Link> links = new ArrayList<Link>();
	HashMap<String, Device> routers = new HashMap<String, Device>();
	public Network() {
		
	}
	public Network(HashMap<String,Device> routers, ArrayList<Link> links) {
		this.routers.putAll(routers);
		this.links.addAll(links);
	}
	public HashMap<String, Device> getRouters() {
		return routers;
	}
	public void setRouters(HashMap<String, Device> routers) {
		this.routers = routers;
	}
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	public int getport(String router, String Iface) {
		return routers.get(router).portToID.get(Iface);
	}
}