package interfaces;

import bean.basis.Ip;

public interface Header{
	//Init 
	void setHeader(Ip ip);
	String toString();
	//Set Operation
	void add(Header header);//OR
	void and(Header header);//AND
	Header copyAnd(Header header);
	void complement();
	Header copyComplement();
	void minus(Header header);
	Header copyMinus(Header header);
	
	Header copy();
	
	boolean isSubsetOf(Header other);
	boolean isEmpty();
}