package interfaces;

import java.util.ArrayList;

import bean.Network;
import bean.basis.Node;
import bean.basis.Rule;

public interface TransferFunc{
	void addFwdRule(Rule rule);
	void addFwdRule(Rule rule, int position);
	void addRewriteRule(Rule rule);
	void addRewriteRule(Rule rule, int position);
	void addLinkRule(Rule rule);
	void addLinkRule(Rule rule, int position);
	void writeTopology(Network network);
	
	ArrayList<Node> T(Node node);
}