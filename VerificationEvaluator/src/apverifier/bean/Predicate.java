package apverifier.bean;

import java.util.ArrayList;
import java.util.HashSet;

import factory.HeaderFactory;
import interfaces.Header;

public class Predicate{
	Integer Id = -1;
	Header header = null;
	HashSet<String> containedRuleIDs = new HashSet<String>();

	public Predicate() {
		
	}
	public Predicate(Header header) {
		this.header = header.copy();
	}
	public Predicate(Predicate predicate) {
		this.Id = predicate.Id;
		this.header =predicate.getHeader().copy();
		this.containedRuleIDs.addAll(predicate.getContainedRuleIDs());
	}
	public boolean isEmpty() {
		return header.isEmpty();
	}
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public HashSet<String> getContainedRuleIDs() {
		return containedRuleIDs;
	}
	public void setContainedRuleIDs(HashSet<String> containedRuleIDs) {
		this.containedRuleIDs = containedRuleIDs;
	}
	public Predicate complement() {
		return new Predicate(header.copyComplement());
	}
	public void and(Predicate other) {
		this.header.and(other.getHeader());
		this.containedRuleIDs.addAll(other.getContainedRuleIDs());
	}
	public Predicate copyAnd(Predicate other){
		Predicate result = new Predicate(this);
		result.getHeader().and(other.getHeader());
		result.getContainedRuleIDs().addAll(other.getContainedRuleIDs());
		return result;
	}
}