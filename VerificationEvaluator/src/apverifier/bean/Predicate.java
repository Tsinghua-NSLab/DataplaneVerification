package apverifier.bean;

import java.util.ArrayList;

import bean.basis.Rule;
import factory.AbstractIPFactory;
import interfaces.AbstractIP;

public class Predicate{
	Integer Index = -1;
	ArrayList<AbstractIP> headers = new ArrayList<AbstractIP>();
	ArrayList<String> containedRuleIDs = new ArrayList<String>();

	public Predicate() {
		
	}
	public Predicate(Predicate predicate) {
		this.Index = predicate.Index;
		for(AbstractIP header:predicate.headers) {
			this.headers.add(AbstractIPFactory.generateAbstractIP(header));
		}
		this.containedRuleIDs.addAll(predicate.getContainedRuleIDs());
	}
	public boolean isEmpty() {
		return headers.isEmpty();
	}
	public Integer getIndex() {
		return Index;
	}
	public void setIndex(Integer index) {
		Index = index;
	}
	public ArrayList<AbstractIP> getHeaders() {
		return headers;
	}
	public void setHeaders(ArrayList<AbstractIP> headers) {
		this.headers = headers;
	}
	public ArrayList<String> getContainedRuleIDs() {
		return containedRuleIDs;
	}
	public void setContainedRuleIDs(ArrayList<String> containedRuleIDs) {
		this.containedRuleIDs = containedRuleIDs;
	}
	public Predicate complement() {
		Predicate result = new Predicate();
		ArrayList<AbstractIP> temp = new ArrayList<AbstractIP>();
		for(AbstractIP header:this.getHeaders()) {
			temp.addAll(header.complement());
			ArrayList<AbstractIP> intersect = new ArrayList<AbstractIP>();
			for(AbstractIP )
		}
	}
}