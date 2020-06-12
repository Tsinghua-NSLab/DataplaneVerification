package interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import bean.basis.Ip;

public interface Header{
	//Init 
	void setHeader(Ip ip);
	String toString();
	//get
	//ArrayList<AbstractIP> getHsList();
	//ArrayList<ArrayList<AbstractIP>> getHsDiff();
	//Set Operation
	void add(Header header);//OR
	//void add(AbstractIP header);
	void and(Header header);//AND
	//void and(AbstractIP header);
	Header copyAnd(Header header);
	//Header copyAnd(AbstractIP header);
	void complement();
	Header copyComplement();
	void minus(Header header);
	Header copyMinus(Header header);
	//void diffHS(AbstractIP header);
	
	void rewrite(Header mask, Header rewrite);
	void setField(HashMap<String,Integer> hsFormat, String field, long value, int rightMask);
	//int count();
	
	Header copy();
	
	boolean isSubsetOf(Header other);
	boolean isEmpty();
	//Others
	void cleanUp();
	void pushAppliedTfRule(String ruleID, int inPort);
}