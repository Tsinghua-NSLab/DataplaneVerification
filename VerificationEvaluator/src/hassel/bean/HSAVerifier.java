package hassel.bean;

import java.util.ArrayList;

import bean.Network;
import bean.basis.Node;
import interfaces.Header;
import interfaces.TransferFunc;

public class HSAVerifier{
	//public static ArrayList<Node> findReachabilityByPropagation(Network network, Node inputPkt, ArrayList<Integer> outPorts){
	//	return findReachabilityByPropagation(network.getNTF(),network.getTTF(),inputPkt,outPorts);
	//}
	public static ArrayList<Node> findReachabilityByPropagation(HSATransFunc NTF, HSATransFunc TTF, Node inputPkt, ArrayList<Integer> outPorts) {
		ArrayList<Node> paths = new ArrayList<Node>();
		ArrayList<Node> propagation = new ArrayList<Node>();
		Node pNode = new Node(inputPkt);
		propagation.add(pNode);
		int loopCount = 0;
		while(propagation.size()>0) {
			//get the next node in propagation graph and apply it to NTF and TTF
	        System.out.println("Propagation has length: " + propagation.size());
	        ArrayList<Node> tmpPropagate = new ArrayList<Node>();
	        System.out.println("loops: " + loopCount);
	        for(Node tmpNode: propagation) {
	        	ArrayList<Node> nextHPs = NTF.T(tmpNode);
	        	for(Node nextHP: nextHPs) {
	        		if(outPorts.contains(nextHP.getPort())) {
	        			Node reached = new Node(nextHP);
	        			reached.getVisits().addAll(tmpNode.getVisits());
	        			reached.getVisits().add(tmpNode.getPort());
	        			for(Header n: tmpNode.getHsHistory()) {
	        				reached.getHsHistory().add(n.copy());
	        			}
	        			reached.getHsHistory().add(tmpNode.getHdr().copy());
	        			paths.add(reached);
	        		}else {
	        			ArrayList<Node> linkeds =  TTF.T(nextHP);
	        			for(Node linked: linkeds) {
	        				Node newPNode = new Node(linked);
	        				newPNode.getVisits().addAll(tmpNode.getVisits());
	        				newPNode.getVisits().add(tmpNode.getPort());
	        				newPNode.getVisits().add(nextHP.getPort());
	        				for(Header n: tmpNode.getHsHistory()) {
	        					newPNode.getHsHistory().add(n.copy());
	        				}
	        				newPNode.getHsHistory().add(tmpNode.getHdr().copy());
	        				newPNode.getHsHistory().add(nextHP.getHdr().copy());
	        				if(outPorts.contains(linked.getPort())) {
	        					paths.add(newPNode);
	        				}else if(newPNode.getVisits().contains(linked.getPort())) {
	        					loopCount++;
	        					//System.out.println("Warning: detected a loop");
	        				}else {
	        					tmpPropagate.add(newPNode);
	        				}
	        			}
	        		}
	        	}
	        }
	        propagation = tmpPropagate;
		}
		return paths;
	}
}