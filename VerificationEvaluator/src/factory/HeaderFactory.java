package factory;

import apverifier.bean.APHeader;
import hassel.bean.HS;
import interfaces.Header;

public class HeaderFactory{
	public static Header generateHeader(String type, Header other) {
		if(other == null) {
			return null;
		}else if(type == "HS") {
			return other.copy();
		}else if(type == "APHeader") {
			return other.copy();
		}
		return null;
	}
	public static Header generateHeader(String type, int length, char bit) {
		if(type == "HS") {
			HS result = new HS(length);
			result.add(AbstractIPFactory.generateAbstractIP(length, bit));
			return result;
		}else if(type == "APHeader") {
			APHeader result = new APHeader(length,bit);	
			return result;
		}
		return null;
	}
	public static Header generateHeader(String type, String wc) {
		if(type == "HS") {
			HS result = new HS(wc.length());
			result.add(AbstractIPFactory.generateAbstractIP(wc));
			return result;
		}else if(type == "APHeader") {
			APHeader result = new APHeader(wc);
			return result;
		}
		return null;
	}
	
	public static Header generateHeader(Header other) {
		return generateHeader(config.TypeConfig.HEADER_TYPE, other);
	}
	public static Header generateHeader(int length, char bit) {
		return generateHeader(config.TypeConfig.HEADER_TYPE, length, bit);
	}
	public static Header generateHeader(String wc) {
		return generateHeader(config.TypeConfig.HEADER_TYPE, wc);
	}
	
}