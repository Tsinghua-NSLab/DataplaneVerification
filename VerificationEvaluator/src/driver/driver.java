package driver;

import java.util.ArrayList;

import utils.Save;
import bean.Network;
import bean.basis.Node;
import factory.HeaderFactory;
import factory.TransferFuncFactory;

public class driver{
	Network network = new Network();	
	
	public void init() {
		network.initStanford();
	}
	/**
	 * This function describes abstract verification procedure  
	 *
	public void testmain() {
		network.initRouterList();
		network.readFiles();
		network.
	}
	 */
	
	public static void singleTest() {
		Network network = new Network();
		network.initFattree4_8_16();
		//Save.saveNetwork(network, "stanfordNetwork.dat");
		//Network network = Save.readNetwork("stanfordNetwork.dat");
		Node Pkt = new Node();
		Pkt.setHdr(HeaderFactory.generateHeader(256));
		Pkt.setPort(10000);
		ArrayList<Integer> Ports = new ArrayList<Integer>();
		Ports.add(11503);
		ArrayList<Node> result = TransferFuncFactory.findReachabilityByPropagation(network.getNTF(), network.getTTF(), Pkt, Ports);
		System.out.println(result);
	}
	
	public static void massTest() {
		Network network = new Network();
		network.initFattree4_8_16();
		//Save.saveNetwork(network, "stanfordNetwork.dat");
		//Network network = Save.readNetwork("stanfordNetwork.dat");
		long starttime = System.nanoTime();
		for(int i = 0; i < network.hostIDs.size(); i++) {
			for(int j = 0; j < network.hostIDs.size(); j++) {
				if(j == i) {
					continue;
				}
				Node Pkt = new Node();
				Pkt.setHdr(HeaderFactory.generateHeader(256));
				Pkt.setPort(network.hostIDs.get(i));
				ArrayList<Integer> Ports = new ArrayList<Integer>();
				Ports.add(network.hostIDs.get(j));
				ArrayList<Node> result = TransferFuncFactory.findReachabilityByPropagation(network.getNTF(), network.getTTF(), Pkt, Ports);
			}
		}
		//System.out.println(result);
		long stoptime = System.nanoTime();
		System.out.print("Total cost time: ");
		System.out.println(stoptime-starttime);
	}
	
	public static void main(String args[]) {
		singleTest();
		//massTest();
	}
}