package interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import bean.Node;

public interface Device{
	HashMap<String, Integer> portToID = new HashMap<String, Integer>(); 
	public ArrayList<Node> applyFwdRule(Node input);
}