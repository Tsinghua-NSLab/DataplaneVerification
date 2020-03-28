package verifiers;

import java.util.ArrayList;

import bean.Network;
import bean.Node;
import hassel.bean.HS;

public class applications{
	public void findReachability(Network network, String device, String inPort, ArrayList<String> outPort, HS inputPkt) {
		ArrayList<Node> paths = new ArrayList<Node>();
		ArrayList<Node> propagation = new ArrayList<Node>();
		Node pNode = new Node(inputPkt, network.getport(device, inPort));
		propagation.add(pNode);
		int loopCount = 0;
		while(propagation.size()>0) {
			//get the next node in propagation graph and apply it to NTF and TTF
	        System.out.println("Propagation has length: " + propagation.size());
	        ArrayList<Node> tmpPropagation = new ArrayList<Node>();
	        System.out.println("loops: " + loopCount);
	        for(Node tmpNode: propagation) {
	        	
	        }
		}
	}
}