package factory;

import java.util.HashSet;

import apverifier.bean.APHeader;
import apverifier.bean.APVTransFunc;
import apverifier.bean.Atom;
import hassel.bean.HS;
import interfaces.Header;
import utils.General;

import com.microsoft.z3.*;

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
			APHeader result = new APHeader(length, bit);	
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
	public static Header generateHeader(int length) {
		Atom header = new Atom(length);
		HashSet<Integer> atomSet = new HashSet<Integer>();
		for(int i = 0; i < length; i++) {
			atomSet.add(i);
		}
		header.setAtomIndexs(atomSet);
		return header;
	}
	
	public static Header generateInputHeader(int length, char bit) {
		Header header = generateHeader(length, bit);
		if(config.TypeConfig.RULE_TYPE == "Atom") {
			header = APVTransFunc.headerToAtom(header);
		}
		return header;
	}
	
	public static Header generateInputHeader(String wc) {
		Header header = generateHeader(wc);
		if(config.TypeConfig.RULE_TYPE == "Atom") {
			header = APVTransFunc.headerToAtom(header);
		}
		return header;
	}
	
	public static Header generateOutputHeader(int value, int length) {
		if(config.TypeConfig.RULE_TYPE == "Atom") {
			Atom atom = new Atom(length);
			HashSet<Integer> atomSet = new HashSet<Integer>();
			atomSet.add(value);
			atom.setAtomIndexs(atomSet);
			return APVTransFunc.AtomToHeader(atom);
		}else {
			String wc = General.int2WC(value, length);
			return generateHeader(wc);
		}
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
	
	public static BoolExpr generateNotEmptyRule(Context ctx, int length) {
		String rType = config.TypeConfig.RULE_TYPE;
		String hType = config.TypeConfig.HEADER_TYPE;
		if(rType == "Atom") {
			return ctx.mkBool(true);
		}else if(hType == "HS") {
			return AbstractIPFactory.generateNotEmptyRule(ctx, length);
		}else if(hType == "APHeader") {
			return ctx.mkBool(true);
		}
		return null;
	}
	
	public static Expr generateZ3Header(Context ctx, int length) {
		String rType = config.TypeConfig.RULE_TYPE;
		String hType = config.TypeConfig.HEADER_TYPE;
		String aType = config.TypeConfig.ABSTRACT_IP_TYPE;
		if(rType == "Atom") {
			return ctx.mkIntConst("pkt");
		}else if(hType == "HS" && aType == "Wildcard") {
			return ctx.mkBVConst("pkt", 2*length);
		}else if(hType == "HS" && aType == "BitMask") {
			return ctx.mkBVConst("pkt", length);
		}else if(hType == "APHeader") {
			return ctx.mkBVConst("pkt", length);
		}
		return null;
	}
	
}