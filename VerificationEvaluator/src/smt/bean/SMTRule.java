package smt.bean;

import bean.basis.Rule;
import config.TypeConfig;

import java.util.HashMap;

import com.microsoft.z3.*;

public class SMTRule{
	public Rule initRule;
	public Context ctx;
	public SMTRule() {
		HashMap<String,String> cfg = new HashMap<String,String>();
		cfg.put("model","true");
		this.ctx = new Context(cfg);
	}
	public BoolExpr makeForwardRule(){
		BoolExpr result = null;
		if(TypeConfig.ABSTRACT_IP_TYPE=="Wildcard") {
			
		}else if(TypeConfig.ABSTRACT_IP_TYPE=="BitMask") {
			
		}
		return result;
	}
}