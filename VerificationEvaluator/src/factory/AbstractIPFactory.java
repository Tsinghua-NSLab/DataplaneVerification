package factory;

import hassel.bean.Wildcard;
import interfaces.AbstractIP;

public class AbstractIPFactory{
	private static AbstractIP generateAbstractIP(String type, AbstractIP other) {
		if(type == "Wildcard") {
			if(other.getClass().toString() == "hassel.bean.Wildcard") {
				return new Wildcard((Wildcard)other);
			}
		}
		return null;
	}
	private static AbstractIP generateAbstractIP(String type, int length, char bit) {
		if(type == "Wildcard") {
			return new Wildcard(length, bit);
		}
		return null;
	}
	public static AbstractIP generateAbstractIP(AbstractIP other) {
		return generateAbstractIP(config.TypeConfig.HEADER_TYPE,other);
	}
	public static AbstractIP generateAbstractIP(int length, char bit) {
		return generateAbstractIP(config.TypeConfig.HEADER_TYPE, length, bit);
	}
}