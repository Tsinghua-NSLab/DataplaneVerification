package apverifier.bean;

import java.util.ArrayList;
import java.util.HashMap;

public class APVTransFunc{
	ArrayList<Predicate> predicates = new ArrayList<Predicate>();
	
	public void refreshPredicates(Predicate predicate) {
		//Use formula (3) to calculate predicates
		if(predicate.isEmpty()) {
			return;
		}else if(predicates.isEmpty()) {
			Predicate temp = new Predicate(predicate);
			
		}
	}
}