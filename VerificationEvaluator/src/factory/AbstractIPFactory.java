package factory;

import hassel.bean.Wildcard;
import interfaces.AbstractIP;
import hassel.bean.BitMask;
import com.microsoft.z3.*;

public class AbstractIPFactory{
	private static AbstractIP generateAbstractIP(String type, AbstractIP other) {
		if(other == null) {
			return null;
		}else if(type == "Wildcard") {
			return new Wildcard((Wildcard)other);
		}else if(type == "BitMask") {
			return new BitMask((BitMask)other);
		}
		return null;
	}
	private static AbstractIP generateAbstractIP(String type, int length, char bit) {
		if(type == "Wildcard") {
			return new Wildcard(length, bit);
		}else if(type == "BitMask") {
			return new BitMask(length, bit);
		}
		return null;
	}
	private static AbstractIP generateAbstractIP(String type, String wc) {
		if(type == "Wildcard") {
			return new Wildcard(wc);
		}else if(type == "BitMask") {
			return new BitMask(wc);
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
	
	public static BoolExpr generateNotEmptyRule(String type, Context ctx, int length) {
		if(type == "Wildcard") {
			int mask = 0;
			for(int i = 0; i < length; i++) {
				mask = mask << 2;
				mask = mask + 1;
			}
			BitVecExpr maskExpr = ctx.mkBV(mask, 2 * length);
			BitVecExpr pkt = ctx.mkBVConst("pkt", 2 * length);
			BitVecExpr pktAndShift = ctx.mkBVOR(ctx.mkBVRotateRight(1, pkt), pkt);
			return ctx.mkEq(ctx.mkBVAND(maskExpr, pktAndShift), maskExpr);
		}else if(type == "BitMask") {
			return ctx.mkBool(true); // always true
		}
		return null;
	}
	
	public static BoolExpr generateNotEmptyRule(Context ctx, int length) {
		return generateNotEmptyRule(config.TypeConfig.ABSTRACT_IP_TYPE, ctx, length);
	}
}