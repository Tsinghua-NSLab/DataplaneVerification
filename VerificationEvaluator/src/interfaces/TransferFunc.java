package interfaces;

import rules.Rule;

public interface TransferFunc{
	void addFwdRule(Rule rule);
	void addFwdRule(Rule rule, int position);
	void addRewriteRule(Rule rule);
	void addRewriteRule(Rule rule, int position);
	void addLinkRule(Rule rule);
	void addLinkRule(Rule rule, int position);
	
}