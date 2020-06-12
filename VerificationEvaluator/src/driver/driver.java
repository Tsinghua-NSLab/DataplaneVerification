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
	public static void main(String args[]) {
		Network network = new Network();
		network.initStanford();
		Save.saveNetwork(network, "stanfordNetwork.dat");
		//Network network = Save.readNetwork("stanfordNetwork.dat");
		Node Pkt = new Node();
		Pkt.setHdr(HeaderFactory.generateHeader(128, 'x'));
		Pkt.setPort(1000007);
		ArrayList<Integer> Ports = new ArrayList<Integer>();
		Ports.add(1000000);
		ArrayList<Node> result = TransferFuncFactory.findReachabilityByPropagation(network.getNTF(), network.getTTF(), Pkt, Ports);
		System.out.println(result);
	}
}