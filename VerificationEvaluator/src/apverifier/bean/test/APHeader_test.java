package apverifier.bean.test;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class APHeader_test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		APHeader_test.testBDD();
	}

	public static void testBDD() {
		System.out.println("Start");
		int varNum = 200;
		BDDFactory f = BDDFactory.init("j", varNum, varNum);
		f.setVarNum(varNum * 2);

		BDD res = (f.ithVar(0)).or(f.ithVar(190)).and(f.ithVar(1));
		res.printSet();
		BDD res_n = res.or(f.zero());
		res = res.not();
		res_n.printSet();
		res.printSet();

		BDD res2 = (f.ithVar(0)).and(f.ithVar(190));
		res2.printSet();

		BDD res3 = res.or(res2);
		res3.printSet();

//		System.out.println(res2.equals(res.and(f.ithVar(0))));
		System.out.println(res2.level());

		res = f.zero();
		res.andWith(f.ithVar(0));
		res.printSet();
		res = f.one();
		res.orWith(f.ithVar(0));
		res.not().printSet();
		System.out.println(f.ithVar(0).and(f.ithVar(0).not()).equals(f.zero()));

	}

}
