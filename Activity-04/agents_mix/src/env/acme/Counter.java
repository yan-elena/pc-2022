package acme;

import cartago.*;

public class Counter extends Artifact {
	
	void init(int initialValue){ 
		this.defineObsProperty("count", initialValue);
	}
	
	@OPERATION void inc(){
		ObsProperty p = getObsProperty("count"); 
		p.updateValue(p.intValue() + 1); 
		signal("tick",4,5);
	} 
}
