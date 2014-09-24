package formulas;

public class Choice {

	RestrictedHueGenerator ihg;//hue zero
	RestrictedOrderedColourGenerator icg;//colour
	
	Stableau st;
	int leaf;
	
	public Choice(RestrictedHueGenerator ihg,
			RestrictedOrderedColourGenerator icg, int leaf) {

		this.ihg = ihg;
		this.icg = icg;
		
		this.leaf = leaf;
	}
	
	
	
}
