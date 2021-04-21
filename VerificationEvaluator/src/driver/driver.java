package driver;

import java.util.ArrayList;

import utils.Save;
import bean.Network;
import bean.basis.Node;
import factory.HeaderFactory;
import factory.TransferFuncFactory;
import apverifier.bean.APVTransFunc;

public class driver{
	Network network = new Network();	
	
	public static void main(String args[]) {
		Network network = new Network();
		network.importFromFile("examples\\2_4_4.network");
		//network.importFromFile("examples\\simple_stanford.network");
		//network.importFromFile("examples\\4_8_16.network");
		Node Pkt = new Node();
		//32 bit for StanfordSimple, 34 bit for fattree
		Pkt.setHdr(HeaderFactory.generateInputHeader(32,'x'));
		//Pkt.setHdr(HeaderFactory.generateFullAtomHeader(APVTransFunc.predicates.size()));
		
		//Pkt.setPort(100);//244
		//Pkt.setPort(10101);//4816
		Pkt.setPort(300001);//stanford simple
		ArrayList<Integer> Ports = new ArrayList<Integer>();
		//Ports.add(133);//244
		//Ports.add(11503);//4816
		Ports.add(1600004);//1600001);
		ArrayList<Node> result = TransferFuncFactory.findReachabilityByPropagation(network.getNTF(), network.getTTF(), Pkt, Ports);
		System.out.println("Path num: " + result.size());
		System.out.println(result);
	}
}