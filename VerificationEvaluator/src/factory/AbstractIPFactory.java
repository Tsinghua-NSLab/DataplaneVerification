package factory;

import hassel.bean.Wildcard;
import interfaces.AbstractIP;

public class AbstractIPFactory{
	public static AbstractIP generateAbstractIP(String type, AbstractIP other) {
		if(type == "Wildcard") {
			if(other.getClass().toString() == "hassel.bean.Wildcard") {
				return new Wildcard((Wildcard)other);
			}
		}
		return null;
	}
	public static AbstractIP generateAbstractIP(AbstractIP other) {
		return generateAbstractIP(config.TypeConfig.HEADER_TYPE,other);
	}
}