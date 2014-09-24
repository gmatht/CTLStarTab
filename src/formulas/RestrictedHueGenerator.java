package formulas;


/**
 * @author mark
 * is an object which iterates through all the hues which contain the
 * specified contents
 * At the moment this is an inefficient implementation.
 */

public class RestrictedHueGenerator {
	
	private TemporalGraph t;
	private int[] contents; //compulsory contents subformula numbers
	private int hue=-1; //current value from t's mtcs array
	private int numHues;
	
	//say which fmlas from t's subformulas must be in all the hues generated
	public RestrictedHueGenerator(TemporalGraph t, int[] contents) {

		this.t = t;
		this.contents = contents;
		numHues=t.numHues();
	}
	
	//moves to first/next satisfactory hue if there is one
	public boolean hasNext(){
		boolean found=false;
		
		while ((!found) && (++hue<numHues)){
			if (t.isPerfect(hue)){			//use of hue preprocessor
				boolean allin=true;
				for(int i=0;i<contents.length;i++){
					if (!t.contains(hue,contents[i])) allin=false;
					}
				if (allin) found=true;
			}
		}//end while
		return found;
	}//end hasNext
	
	//report on the hue, assumes that hasNext has been called before
	public int hue(){
		return hue;
	}
	
	public void reset(){
		hue=-1;
	}
	
	//sets just before users choice: expects hasNext called
	public boolean userSetHue(){
		System.out.println("You can choose from the following hues:");
		reset();
		boolean exists=false;
		while(hasNext()){
			exists=true;
			System.out.println("#"+hue+": "+t.getHue(hue));
		}
		if (exists){
			System.out.println("Enter number:");
			hue=Integer.parseInt(Keyboard.getInput());
			hue=hue-1; //NB
		} else {
			System.out.println("Oops. There are no initial hues.");
			hue=-1;
			return false;
		}
		return true;
	}

}
