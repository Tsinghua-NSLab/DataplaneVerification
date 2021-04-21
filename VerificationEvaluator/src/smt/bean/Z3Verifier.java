package smt.bean;

import java.util.ArrayList;
import java.util.HashSet;

import bean.Network;
import bean.GraphNode;
import bean.basis.BasicTF;
import bean.basis.Node;
import hassel.bean.HSATransFunc;
import interfaces.Header;
import interfaces.TransferFunc;
import smt.bean.SMTTransFunc;

public class Z3Verifier{
	public static ArrayList<Node> findReachabilityByfindReachabilityByPropagation(Network network, Node inputPkt, ArrayList<Integer> outPorts){
		return findReachabilityByPropagation(network.getNTF(),network.getTTF(),inputPkt,outPorts);
	}
	
	public static ArrayList<Node> findReachabilityByPropagation(BasicTF BasicNTF, BasicTF BasicTTF, Node inputPkt, ArrayList<Integer> outPorts){
		SMTTransFunc SMTTF = new SMTTransFunc(BasicNTF, BasicTTF);
		HashSet<Integer> allPorts = new HashSet<Integer>();
		HashSet<Integer> edgePorts = new HashSet<Integer>();
		for(int port : BasicNTF.outportToRule.keySet()) {
			allPorts.add(port);
		}
		for(int port : BasicNTF.inportToRule.keySet()) {
			allPorts.add(port);
		}
		for(int port : BasicTTF.outportToRule.keySet()) {
			allPorts.add(port);
		}
		for(int port : allPorts) {
			if(!BasicTTF.outportToRule.containsKey(port)) {
				edgePorts.add(port);
			}
		}
		return SMTTF.solve(inputPkt, outPorts, edgePorts);
	}
	
	public static ArrayList<Node> findReachabilityByReverse(Network network, Node outputPkt, ArrayList<Integer> outPorts){
		return findReachabilityByReverse(network.getNTF(), network.getTTF(), outputPkt, outPorts);
	}
	public static ArrayList<Node> findReachabilityByReverse(BasicTF BasicNTF, BasicTF BasicTTF, Node outputPkt, ArrayList<Integer> outPorts){
		HSATransFunc NTF = new HSATransFunc(BasicNTF);
		HSATransFunc TTF = new HSATransFunc(BasicTTF);
		//TODO finish it
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
	        		if(outPorts.contains(nextHP.getPort())) {
	        			Node reached = new Node(nextHP);
	        			reached.getVisits().addAll(tmpNode.getVisits());
	        			reached.getVisits().add(tmpNode.getPort());
	        			for(Header n: tmpNode.getHsHistory()) {
	        				reached.getHsHistory().add(n.copy());
	        			}
	        			paths.add(reached);
	        		}else {
	        			ArrayList<Node> linkeds =  TTF.T(nextHP);
	        			for(Node linked: linkeds) {
	        				Node newPNode = new Node(linked);
	        				newPNode.getVisits().addAll(tmpNode.getVisits());
	        				newPNode.getVisits().add(tmpNode.getPort());
	        				newPNode.getVisits().add(nextHP.getPort());
	        				for(Header n: tmpNode.getHsHistory()) {
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
	public static ArrayList<Node> findReachabilityByZ3(){
		//TODO finish it
		ArrayList<Node> paths = new ArrayList<Node>();
		return paths;
	}
}