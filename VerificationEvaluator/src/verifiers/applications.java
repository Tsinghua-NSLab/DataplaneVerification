package verifiers;

import java.util.ArrayList;

import bean.Network;
import bean.Node;
import hassel.bean.HS;
import tfFunction.tfFunction;

public class applications{
	public ArrayList<Node> findReachability(tfFunction NTF, tfFunction TTF, Node inputPkt, ArrayList<Integer> outPorts) {
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
	        			for(HS n: tmpNode.getHsHistory()) {
	        				reached.getHsHistory().add(n.copy());
	        			}
	        			paths.add(reached);
	        		}else {
	        			ArrayList<Node> linkeds =  TTF.T(nextHP);
	        			for(Node linked: linkeds) {
	        				Node newPNode = new Node(linked);
	        				newPNode.getVisits().addAll(tmpNode.getVisits());
	        				newPNode.getVisits().add(tmpNode.getPort());
	        				//newPNode.getVisits().add(nextHP.getPort());
	        				for(HS n: tmpNode.getHsHistory()) {
	        					newPNode.getHsHistory().add(n);
	        				}
	        				newPNode.getHsHistory().add(tmpNode.getHdr());
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