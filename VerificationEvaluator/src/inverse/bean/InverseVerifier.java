package inverse.bean;

import java.util.ArrayList;

import bean.basis.Node;
import interfaces.Header;

public class InverseVerifier{
	public static ArrayList<Node> findReachabilityByPropagation(InverseTransFunc NTF, InverseTransFunc TTF, Node outputPkt, ArrayList<Integer> inPorts) {
		ArrayList<Node> paths = new ArrayList<Node>();
		ArrayList<Node> propagation = new ArrayList<Node>();
		Node pNode = new Node(outputPkt);
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
	        		if(inPorts.contains(nextHP.getPort())) {
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
	        				if(inPorts.contains(linked.getPort())) {
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