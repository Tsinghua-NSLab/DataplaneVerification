package interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.z3.*;

public interface AbstractIP{
	//Init
	void setMask(int rightMask);
	void setField(HashMap<String,Integer> hsFormat, String field, long value, int rightMask);
	//Bit operation
	void and(AbstractIP other);
	BoolExpr z3Match(Context ctx, Expr packet);
	//void or(AbstractIP other);
	//void xor(AbstractIP other);
	//void not();
	ArrayList<AbstractIP> complement();
	ArrayList<AbstractIP> minus(AbstractIP other);
	static ArrayList<AbstractIP> compressList(ArrayList<AbstractIP> l){
		ArrayList<Integer> popIndex = new ArrayList<Integer>();
		for(int i = 0; i< l.size(); i++) {
			for(int j = i+1; j < l.size(); j++) {
				if(l.get(i).contains(l.get(j))) {
					popIndex.add(j);
				}
				if(l.get(j).contains(l.get(i))) {
					popIndex.add(i);
				}
			}
		}
		ArrayList<AbstractIP> result = new ArrayList<AbstractIP>();
		for(int i = 0; i < l.size(); i++) {
			if(!popIndex.contains(i)) {
				result.add(l.get(i));
			}
		}
		return result;
	}
	int rewrite(AbstractIP mask, AbstractIP rewrite);//
	//Judgements
	boolean equals(AbstractIP other);//
	boolean contains(AbstractIP other);//
	boolean isEmpty();//
	//get&set
	int getLength();//
	void setLength(int length);//
	String getString();//
}