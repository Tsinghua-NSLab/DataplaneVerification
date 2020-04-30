package smt.bean;

import java.util.ArrayList;

import com.microsoft.z3.*;

public class SMTHeader{
	Context ctx = null;
	TupleSort IPSort = null;
	ArrayList<Expr> hsList = new ArrayList<Expr>();
	ArrayList<Expr> hsDiff = new ArrayList<Expr>();
	
	public SMTHeader(Context ctx, TupleSort IPSort) {
		this.ctx = ctx;
		this.IPSort = IPSort;
	}
}