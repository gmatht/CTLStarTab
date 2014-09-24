package formulas;
import java.util.*;

public class JHueEnum {
	
	static JHueEnum e=null;
	// Not efficient, should use primitive types!
	HashMap<JHue,Integer> hue2int = new HashMap<JHue,Integer>();
    HashMap<Integer,Integer> temp_succ = new HashMap<Integer,Integer>();
	ArrayList<JHue> int2hue  = new ArrayList<JHue>() ;
//	ArrayList<Node>[] myarr = (new ArrayList<Node>[]);
	HashMap<Integer,Integer>[] add_formula;
	Subformulas sf;
	FormulaTree ft;
	
	public JHueEnum (Subformulas s, FormulaTree t) {
		sf=s;
		ft=t;
		
		//@SuppressWarnings(value = "unchecked");
		add_formula = (new HashMap[sf.count()]); //The Java warning is harmless and unavoidable
		int i=0;
		for (i=0; i<sf.count(); i++) {
			add_formula[i]= new HashMap<Integer,Integer>();
		}
		hue2Int(null); //Add False Hue
		hue2Int(new JHue(sf)); //Add True (empty) Hue
	}

        public int temporalSuccessor(int i) {
            int succ;
            Integer Isucc = temp_succ.get(i);
            if (Isucc == null) {
                succ=hue2Int(int2Hue(i).temporalSuccessor(i));
                temp_succ.put(i,succ);
                return succ;
            } else {
                return Isucc;
            }
        }
	
	public int hue2Int(JHue h) {
		int i;
                if (h!=null) {
                    if (h.hasContradiction()) {
                        return 0;
                    }
                }
		if (!hue2int.containsKey(h)) {
			//need to add it.
			i = int2hue.size();
			hue2int.put(h, i);
			int2hue.add(h);
		} else {
			i=hue2int.get(h);
		}
		return i;
	}

	public JHue int2Hue(int h) {
		JHue hue = int2hue.get(h);
                if (hue == null) {
                    throw new RuntimeException("Integer "+h+" does not map to a hue");
                }
                return hue;
	}
	
	/**
	 * Returns true if hue number h contains formulae f
	 */
	public boolean h_has_f(int h, int f) {
		return int2hue.get(h).get(f);
	}
	
	/**
	 * Convert a hue number to a string describing the hue.
	 * 
	 * @param h
	 * @return
	 */
	
	public String toString(int h){
		if (h==0) {
			return "FALSE";
		}
		JHue jh=int2hue.get(h);
		if (jh==null) {
			System.out.format("NULL %s\n",h);
		}

		return int2hue.get(h).toString();
	}
	
	public int addFormula2Hue(int f, int h) {
		if (f >= sf.count()) {
			throw new RuntimeException("addFormula2Hue "+ f +" " + h + " " + sf.count());
		}
		HashMap<Integer,Integer> add_formula_f=add_formula[f];
		int ret;
		if (h==0) { //false hue
			return 0;
		}
		if (add_formula_f.containsKey(h)) {
			ret=add_formula_f.get(h);
		} else {
			JHue h_plus_f = int2hue.get(h);
			h_plus_f = (JHue)h_plus_f.clone();
			h_plus_f.set(f);
			if (h_plus_f.hasContradiction()) {
				ret=0; // false hue
			} else {
				ret=hue2Int(h_plus_f);
			}
		} 
		return ret;
	}

	public boolean isEssential(int h) { /* not cached */
		if (h == 0) {
			return true;
		}
		return int2Hue(h).isEssential();
	}

/*	public boolean Essential(int h) { // not cached 
		return int2Hue(h). ();
	}*/

	
	public int setEssential(int h, boolean essential) { /* not cached */
		JHue hue = (JHue) int2Hue(h).clone();
		hue.setEssential(essential);
		return hue2Int(hue);
	}

        public ArrayList<Integer> getEventualities(int hue) {
            JHue h=int2Hue(hue);
            return h.getEventualities();
        }
}
