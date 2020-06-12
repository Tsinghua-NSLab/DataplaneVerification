package apverifier.bean;

import java.util.HashMap;

import bean.basis.Ip;
import interfaces.Header;
import net.sf.javabdd.*;

public class APHeader implements Header {
	
	private static BDDFactory factory = BDDFactory.init("j", 1000, 1000);
	public BDD bdd;
	private int length = -1;
	// TODO: it is ok to make the header's length variable but I leave it to future
	// work. Because when we want to use and/or/not operation with javabdd, it is
	// necessary to make sure that the bdds are in the same factory!
	static {
		factory.setVarNum(1000);
	}

	public APHeader() {
		this.bdd = factory.zero(); // Not matter what's the input, the bdd is zero.
		// All input to make bdd equal '1' --> the header set
	}

	public APHeader(int length, char bit) {
		this.length = 1000;
		this.bdd = factory.zero();
		if(bit == 'x') {
			for (int i = 0; i < length; i++) {
				this.bdd.orWith(factory.ithVar(i));
				// First, let us make all bits of the field is non-sense, which means the field
				// is xxxxx.
			}
		}else if(bit == '1') {
			for (int i = 0; i < length; i++) {
				this.bdd.orWith(factory.ithVar(i));
				// First, let us make all bits of the field is non-sense, which means the field
				// is xxxxx.
			}
			for (int i = 0; i < length; i++) {
				int setPos = length - (i + 1); // The position of the required location
				this.bdd = this.bdd.and(factory.ithVar(setPos));
			}
		}else if(bit == '0') {
			for (int i = 0; i < length; i++) {
				this.bdd.orWith(factory.ithVar(i));
				// First, let us make all bits of the field is non-sense, which means the field
				// is xxxxx.
			}
			for (int i = 0; i < length; i++) {
				int setPos = length - (i + 1); // The position of the required location
				this.bdd = this.bdd.and(factory.ithVar(setPos).not());
			}
		}else if(bit == 'z') {
			for (int i = 0; i < length; i++) {
				this.bdd.orWith(factory.ithVar(i));
				// First, let us make all bits of the field is non-sense, which means the field
				// is xxxxx.
			}
			for (int i = 0; i < length; i++) {
				int setPos = length - (i + 1); // The position of the required location
				this.bdd = this.bdd.and(factory.ithVar(setPos));
				this.bdd = this.bdd.and(factory.ithVar(setPos).not());
			}
		}
	}
	
	public APHeader(String wc) {
		this.length = wc.length();
		this.bdd = factory.one();
		for(int i = 0; i<length; i++) {
			//this.bdd.orWith(factory.ithVar(i));
			if(wc.charAt(i)=='x') {
			}else if(wc.charAt(i)=='1') {
				int setPos = length - (i + 1); // The position of the required location
				this.bdd = this.bdd.and(factory.ithVar(setPos));
			}else if(wc.charAt(i)=='0') {
				int setPos = length - (i + 1); // The position of the required location
				this.bdd = this.bdd.and(factory.ithVar(setPos).not());
			}else if(wc.charAt(i)=='z') {
				int setPos = length - (i + 1); // The position of the required location
				this.bdd = this.bdd.and(factory.ithVar(setPos));
				this.bdd = this.bdd.and(factory.ithVar(setPos).not());
			}else {
				System.out.println("Error, invalied string");
			}
		}
	}
	
	/* Get Operation */
	public int getLength() {
		return this.length;
	}

	public BDD getBDD() {
		return this.bdd;
	}

	@Override
	public void setHeader(Ip ip) {
		// TODO: This Function is useless now.
	}

	private static boolean checkCompatibility(Header h1, Header h2) {
		if (h1.getClass().getName() == "apverifier.bean.APHeader"
				&& h2.getClass().getName() == "apverifier.bean.APHeader") {
			APHeader aph1 = (APHeader) h1;
			APHeader aph2 = (APHeader) h2;
			if (aph1.getLength() == aph2.getLength()) {
				return true;
			} else {
				System.out.println("AP Header length mismatch");
			}
		} else {
			System.out.println("AP Header add operation type unmatched.");
		}
		return false;
	}

	@Override
	public void add(Header header) {
		// TODO: check the operation is right?
		if (APHeader.checkCompatibility(this, header)) {
			APHeader apheader = (APHeader) header;
			BDD aimBDD = apheader.getBDD();
			this.bdd = this.bdd.or(aimBDD);
		}
	}

	@Override
	public void and(Header header) {
		// TODO: check the operation
		if (APHeader.checkCompatibility(this, header)) {
			APHeader apheader = (APHeader) header;
			BDD aimBDD = apheader.getBDD();
			this.bdd = this.bdd.and(aimBDD);
		}
	}

	@Override
	public Header copyAnd(Header header) {
		APHeader newH = (APHeader) this.copy();
		newH.and(header);
		return newH;
	}

	@Override
	public void complement() {
		// TODO Auto-generated method stub
		this.bdd = this.bdd.not();
	}

	@Override
	public Header copyComplement() {
		APHeader newH = (APHeader) this.copy();
		newH.complement();
		return newH;
	}

	@Override
	public void minus(Header header) {
		// TODO: check the operation
		if (APHeader.checkCompatibility(this, header)) {
			APHeader apheader = (APHeader) header;
			BDD aimBDD = apheader.getBDD();
			this.bdd = this.bdd.and(aimBDD.not());
		}
	}

	@Override
	public Header copyMinus(Header header) {
		APHeader newH = (APHeader) this.copy();
		newH.minus(header);
		return newH;
	}

	@Override
	public void rewrite(Header mask, Header rewrite) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setField(HashMap<String, Integer> hsFormat, String field, long value, int rightMask) {
		// TODO Auto-generated method stub
		// hsFormat: name(field) --> val(field length or position)
		// field: string(hsFormat, field)
		// value: the value of field
		// rightMask: /24 means 8
		// For example: (xxx, 'src_ip', 108245972, 8),
		int fieldLength = hsFormat.get(field + "_len");
		int startPos = hsFormat.get(field + "_pos");
		for (int i = 0; i < fieldLength; i++) {
			this.bdd.orWith(factory.ithVar(i + startPos));
			// First, let us make all bits of the field is non-sense, which means the field
			// is xxxxx.
		}
		for (int i = rightMask; i < fieldLength; i++) {
			int setPos = startPos + fieldLength - (i + 1); // The position of the required location
			int setVal = (int)((value >> i) & 0x01); // Get the value require of this location
			if (setVal == 1) {
				this.bdd = this.bdd.and(factory.ithVar(setPos));
			} else {
				this.bdd = this.bdd.and(factory.ithVar(setPos).not());
			}
		}
	}

	@Override
	public Header copy() {
		// TODO Auto-generated method stub
		APHeader newHeader = new APHeader();
		newHeader.bdd = this.bdd.or(factory.zero());
		newHeader.length = this.length;
		return newHeader;
	}

	@Override
	public boolean isSubsetOf(Header other) {
		// TODO Auto-generated method stub
		if (APHeader.checkCompatibility(this, other)) {
			APHeader aim = (APHeader) other;
			BDD aimbdd = aim.getBDD();
			if (aimbdd.equals(aimbdd.and(this.bdd))) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		// Not input will generate 0, means that it is empty
		return (this.bdd.isZero());
	}

	@Override
	public void cleanUp() {
		//this.bdd.free();
	}

	@Override
	public void pushAppliedTfRule(String ruleID, int inPort) {
		// TODO Auto-generated method stub

	}

}
