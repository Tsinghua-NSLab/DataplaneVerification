package driver;

import java.util.ArrayList;

import bean.Network;
import bean.basis.Node;
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
		Node Pkt = new Node();
		ArrayList<Integer> Ports = new ArrayList<Integer>();
		ArrayList<Node> result = TransferFuncFactory.findReachabilityByPropagation(network.getNTF(), network.getTTF(), Pkt, Ports);
		System.out.println(result);
	}
}