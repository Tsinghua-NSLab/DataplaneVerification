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
	
	public static void singleTest() {
		Network network = new Network();
		//network.initFattree2_4_4();
		//network.initFattree4_8_16();
		network.initStanfordSimple();
		network.outputToFile("examples\\simple_stanford.network");
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
	
	public static void massTest() {
		Network network = new Network();
		network.initFattree2_4_4();
		//network.initFattree4_8_16();
		//Save.saveNetwork(network, "stanfordNetwork.dat");
		//Network network = Save.readNetwork("stanfordNetwork.dat");
		long starttime = System.nanoTime();
		for(int i = 0; i < network.hostIDs.size(); i++) {
			for(int j = 0; j < network.hostIDs.size(); j++) {
				if(j == i) {
					continue;
				}
				Node Pkt = new Node();
				Pkt.setHdr(HeaderFactory.generateInputHeader(34,'x'));
				//Pkt.setHdr(HeaderFactory.generateHeader(128));
				Pkt.setPort(network.hostIDs.get(i));
				ArrayList<Integer> Ports = new ArrayList<Integer>();
				Ports.add(network.hostIDs.get(j));
				ArrayList<Node> result = TransferFuncFactory.findReachabilityByPropagation(network.getNTF(), network.getTTF(), Pkt, Ports);
			}
		}
		//System.out.println(result);
		long stoptime = System.nanoTime();
		System.out.print("Total cost time: ");
		System.out.println(stoptime-starttime);
	}
	
	public static void main(String args[]) {
		singleTest();
		//massTest();
	}
}