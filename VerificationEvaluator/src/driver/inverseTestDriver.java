package driver;

import java.util.ArrayList;

import bean.basis.BasicTF;
import bean.basis.Node;
import bean.basis.Rule;
import factory.HeaderFactory;
import hassel.bean.HS;
import inverse.bean.InverseTransFunc;
import inverse.bean.InverseVerifier;
import interfaces.Header;

public class inverseTestDriver{
	public static void main(String[] args) {
		//transferFunctions
		BasicTF testTTF = new BasicTF();
		BasicTF testNTF = new BasicTF();
		//add TTF rules
		Rule rule;
		ArrayList<Integer> inPorts;
		ArrayList<Integer> outPorts;
		Header ip;
		//rule1: inport:1-1 dst:10x src:src outport:1-3
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		inPorts.add(11);
		outPorts = new ArrayList<Integer>();
		outPorts.add(13);
		rule.setInPorts(inPorts);
		rule.setOutPorts(outPorts);
		ip = HeaderFactory.generateHeader("10x01x");
		rule.setMatch(ip);
		testNTF.addFwdRule(rule);
		//rule2: inport:1-1 dst:1xx src:xxx outport:1-2
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		inPorts.add(11);
		outPorts = new ArrayList<Integer>();
		outPorts.add(12);
		rule.setInPorts(inPorts);
		rule.setOutPorts(outPorts);
		ip = HeaderFactory.generateHeader("1xxxxx");
		rule.setMatch(ip);
		testNTF.addFwdRule(rule);
		//rule3: inport:2-1 2-2 dst:10x src:xxx outport:2-3
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		inPorts.add(21);
		inPorts.add(22);
		outPorts = new ArrayList<Integer>();
		outPorts.add(23);
		rule.setInPorts(inPorts);
		rule.setOutPorts(outPorts);
		ip = HeaderFactory.generateHeader("10xxxx");
		rule.setMatch(ip);
		testNTF.addFwdRule(rule);
		//rule4: inport:3-1 dst:1xx src:xxx outport:3-3// rewrite:dst[1]=0
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		inPorts.add(31);
		outPorts = new ArrayList<Integer>();
		outPorts.add(33);
		rule.setInPorts(inPorts);
		rule.setOutPorts(outPorts);
		ip = HeaderFactory.generateHeader("xxx1xx");
		rule.setMatch(ip);
		testNTF.addFwdRule(rule);
		//rule5: inport:3-1 dst:xxx src:1xx outport:3-3
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		inPorts.add(31);
		outPorts = new ArrayList<Integer>();
		outPorts.add(32);
		rule.setInPorts(inPorts);
		rule.setOutPorts(outPorts);
		ip = HeaderFactory.generateHeader("1xxxxx");
		rule.setMatch(ip);
		testNTF.addFwdRule(rule);
		
		//add NTF rules (links)
		//13->21
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		inPorts.add(13);
		outPorts = new ArrayList<Integer>();
		outPorts.add(21);
		rule.setInPorts(inPorts);
		rule.setOutPorts(outPorts);
		testTTF.addLinkRule(rule);
		//13->21
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		inPorts.add(12);
		outPorts = new ArrayList<Integer>();
		outPorts.add(31);
		rule.setInPorts(inPorts);
		rule.setOutPorts(outPorts);
		testTTF.addLinkRule(rule);
		//32->22
		rule = new Rule();
		inPorts = new ArrayList<Integer>();
		inPorts.add(32);
		outPorts = new ArrayList<Integer>();
		outPorts.add(22);
		rule.setInPorts(inPorts);
		rule.setOutPorts(outPorts);
		testTTF.addLinkRule(rule);
				
		//initial input node
		HS inputHeader = new HS(6);
		inputHeader.add(HeaderFactory.generateHeader("xxxxxx"));
		Node output = new Node(inputHeader, 23);
		//initial outPorts
		inPorts = new ArrayList<Integer>();
		inPorts.add(11);
		//testNTF.ruleDecouple();
		//use HSA verifier to verify
		InverseTransFunc NTF = new InverseTransFunc(testNTF);
		InverseTransFunc TTF = new InverseTransFunc(testTTF);
		ArrayList<Node> paths = InverseVerifier.findReachabilityByPropagation(NTF, TTF, output, inPorts);
		System.out.println(paths);
	}
}