/**
 * 
 */
package formulas;
import java.util.*;
/**
 * A representation of a colour as a set of hues.
 * 
 * @author John
 *
 */
public class JColour {
	HashSet<JHue> hues = new HashSet<JHue>();
	Subformulas sf;
	
	boolean pruned=false; 
	
	public JColour(Subformulas f) {
		sf=f;
	}

	public void addHue (JHue h) {
		JHue state_hue=new JHue(sf);
		for (int i=h.nextSetBit(0); i!=-1 ;i=h.nextSetBit(i+1)) {
			if (sf.topChar(i)=='A') {
				state_hue.set(i);
			}
		}
		Iterator<JHue> it = hues.iterator();
		while (it.hasNext()) {
			it.next().or(state_hue);
		}
		hues.add(h);
	}
	
	public boolean hasContradiction() {
		Iterator<JHue> it = hues.iterator();
		while (it.hasNext()) {
			if(it.next().hasContradiction()) {
				return true;
			}
		}
		return false;
	}
	
}
