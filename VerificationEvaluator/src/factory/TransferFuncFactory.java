package factory;

import java.util.ArrayList;

import bean.basis.BasicTF;
import bean.basis.Node;
import hassel.bean.HSATransFunc;
import hassel.bean.HSAVerifier;
import interfaces.TransferFunc;
import inverse.bean.InverseTransFunc;
import inverse.bean.InverseVerifier;
import smt.bean.SMTTransFunc;
import smt.bean.Z3Verifier;

public class TransferFuncFactory{	
	public static ArrayList<Node> findReachabilityByPropagation(String type, BasicTF BasicNTF, BasicTF BasicTTF, Node Pkt, ArrayList<Integer> Ports){
		if(type == "HSA") {
			HSATransFunc NTF = new HSATransFunc(BasicNTF);
			HSATransFunc TTF = new HSATransFunc(BasicTTF);
			return HSAVerifier.findReachabilityByPropagation(NTF, TTF, Pkt, Ports);
		}else if(type == "Inverse") {
			InverseTransFunc NTF = new InverseTransFunc(BasicNTF);
			InverseTransFunc TTF = new InverseTransFunc(BasicTTF);
			return InverseVerifier.findReachabilityByPropagation(NTF, TTF, Pkt, Ports);
		}else if(type == "Z3") {
			//SMTTransFunc TF = new SMTTransFunc(BasicNTF, BasicTTF);
			return Z3Verifier.findReachabilityByPropagation(BasicNTF, BasicTTF, Pkt, Ports);
		}
		return null;
	}
	
	public static ArrayList<Node> findReachabilityByPropagation(BasicTF BasicNTF, BasicTF BasicTTF, Node Pkt, ArrayList<Integer> Ports){
		return findReachabilityByPropagation(config.TypeConfig.TF_TYPE, BasicNTF, BasicTTF, Pkt, Ports);
	}
	
}