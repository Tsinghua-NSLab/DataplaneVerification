package factory;

import java.util.ArrayList;
import java.util.HashMap;

import apverifier.bean.APVTransFunc;
import bean.basis.BasicTF;
import bean.basis.Influence;
import bean.basis.Rule;
import config.TypeConfig;
import interfaces.Header;

public class RuleFactory{
	private static void Preprocess(String type, BasicTF TF) {
		if(type == "Naive") {
			//Do nothing
		}else if(type == "Uncovered") {
			TF.ruleDecouple();
		}else if(type == "Atom") {
			new APVTransFunc(TF);
		}
	}
	public static void Preprocess(BasicTF TF) {
		Preprocess(TypeConfig.RULE_TYPE, TF);
	}
}
