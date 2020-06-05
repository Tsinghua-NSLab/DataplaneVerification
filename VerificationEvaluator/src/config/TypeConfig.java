package config;

public class TypeConfig{
	@Deprecated
	public static String ABSTRACT_IP_TYPE = "Wildcard";
	public static String HEADER_TYPE = "APHeader";//"HS";"APHeader";;;"naive";
	public static String RULE_TYPE = "Naive";//"Uncovered";"Atom";
	public static String TF_TYPE = "HSA";//"Inverse";;;"Z3";"graph"
	public static String PARSER_TYPE = "CiscoParser";
}