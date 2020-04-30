package factory;

import hassel.bean.Wildcard;
import interfaces.AbstractIP;

public class AbstractIPFactory{
	private static AbstractIP generateAbstractIP(String type, AbstractIP other) {
		if(other == null) {
			return null;
		}else if(type == "Wildcard") {
			return new Wildcard((Wildcard)other);
		}
		return null;
	}
	private static AbstractIP generateAbstractIP(String type, int length, char bit) {
		if(type == "Wildcard") {
			return new Wildcard(length, bit);
		}
		return null;
	}
	private static AbstractIP generateAbstractIP(String type, String wc) {
		if(type == "Wildcard") {
			return new Wildcard(wc);
		}
		return null;
	}
	public static AbstractIP generateAbstractIP(AbstractIP other) {
		return generateAbstractIP(config.TypeConfig.ABSTRACT_IP_TYPE,other);
	}
	public static AbstractIP generateAbstractIP(int length, char bit) {
		return generateAbstractIP(config.TypeConfig.ABSTRACT_IP_TYPE, length, bit);
	}
	public static AbstractIP generateAbstractIP(String wc) {
		return generateAbstractIP(config.TypeConfig.ABSTRACT_IP_TYPE, wc);
	}
}