package driver;

import java.util.ArrayList;

import bean.Network;
import bean.Node;
import verifiers.applications;

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
		driver test = new driver();
		test.init();
		Node node = new Node();
		applications.findReachability(test.network.getNTF(), test.network.getTTF(), node, new ArrayList<Integer>());
		System.out.println(test);
	}
}