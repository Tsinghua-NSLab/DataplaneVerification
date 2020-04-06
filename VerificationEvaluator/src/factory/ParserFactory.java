package factory;

import interfaces.Parser;
import parser.CiscoParser;

public class ParserFactory{
	private static Parser generateParser(String type, int switchID) {
		if(type == "CiscoParser") {
			return new CiscoParser(switchID);
		}
		return null;
	}
	public static Parser generateParser(int switchID) {
		return generateParser(config.TypeConfig.PARSER_TYPE, switchID);
	}
}