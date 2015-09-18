package formulas;
//import java.lang.*;
import java.util.*;

public class JHue extends java.util.BitSet {
	//java.util.BitSet knowntrue; // formulas known to be true

	protected Subformulas sf;
	protected   Boolean statehue=false;

	private int cached_hashcode=-1; // This isn't really used except for debugging.

	public Object clone() {
		JHue new_hue = (JHue)super.clone();
		new_hue.cached_hashcode=-1;
		new_hue.statehue=statehue;
		return new_hue;
	}

	public JHue(Subformulas s) {
		super();
		this.sf=s;
		//sf.count();
	}
	public JHue(boolean stateh, Subformulas s) {
		super();
		this.sf=s;
		statehue=stateh;
		//sf.count();
	}



/*	public JHue(JHue h) {
		super(h);
		this.sf=h.sf;
	}*/

	private int essential_index() {
		if (!JNode.use_optional_hues) throw new RuntimeException("Optional hues disabled, but used");
		return sf.count();
	}

	public boolean isEssential() {
		return true;
		// TODO: return super.get(essential_index());
	}

	@Override
	public boolean equals(Object o) {
		return (super.equals(o) && (statehue == ((JHue)o).statehue));
	}

	public void setEssential(boolean e) {
		super.set(essential_index(), e);
	}

/*	public JHue copy() {
		c=JHue(sf);




	}

        private void set_or(int i) {

        }
*/

/*	public boolean get(int i) {
		boolean b=super.get(i);
		if (b) {
			JNode.out.println("GOT: "+formulaToString(i));
		} else {
			JNode.out.println("get: NOT "+formulaToString(i));
		}
		return b;
	}
	*/
//	private void f(int i, int j){
	public void set(int i) {
		//.out.format("Before: %d %s\n",i,toString());
		if (cached_hashcode!=-1) throw new RuntimeException ("Modifying used Hue.");
		if (i >= sf.count()) {
			throw new RuntimeException("JHue.set(i): i is too large");
		}
		if (i < 0) {
			throw new RuntimeException("JHue.set(i): i is negative");
		}

		if (!get(i)) {
			if (JNode.use_no_star) {
				if (statehue    && !sf.state_formula (i)) return;
				if ((!statehue) && !sf.path_sensitive(i)) return;
			}

			super.set(i);
			if (JNode.log) JNode.out.println("set: "+formulaToString(i));
			//if (JNode.use_no_star && Subformulas.state_formula(i)) return;
			switch (sf.topChar(i)) {
			case '&': set(sf.right(i)); set(sf.left(i)); break;
			case 'A':
				//if ((!JNode.use_no_star) || (!sf.AE_no_star) {
					set(sf.left(i));
				//}
				break;
			case '-':
				{
					int f = sf.left(i);
					switch (sf.topChar(f)) {
					case '-': set(sf.left(f)); break;
					case 'U': case 'Y': case 'I': set(sf.negn(sf.right(f))); // -(aUb) => -b
					}
				}
			}
                    for (int j=this.nextSetBit(0);j>=0;j=nextSetBit(j+1)) {
                        switch (sf.topChar(j)) {
                            case 'U': case 'Y': case 'I': {
                                int l=sf.left(j);
                                int r=sf.right(j);
                                if (get(sf.negn(l))) {
                                    set(r);
                                } else if (get(sf.negn(r))) {
                                    set(l);
                                }
                            }
                            break;

                            case '-': if (sf.topChar(sf.left(j)) == '&') {
                                int k = sf.left(j);
                                int l=sf.left(k);
                                int r=sf.right(k);
                                if (get(l)) {
                                    set(sf.negn(r));
                                } else if (get(r)) {
                                    set(sf.negn(l));
                                }
                            }
                        }
                    }
		}

		//System.out.println("After: "+toString());
	}

	// maybe use below?
/*	public get_E_eventualities() {
		assert(state_hue);
		int[] ev = new int[cardinality()];
		int ptr=0;

 		for (int j=this.nextSetBit(0);j>=0;j=nextSetBit(j+1)) {
			if (sf.topChar(j)='E' && sf.AE_no_star(j) && sf.topChar(sf.left(j)=='U') ) {
				ev[ptr]=j;
				ptr++;
			}
		}

		return Arrays.copyOfRange(ev,0,ptr);
	}*/

	// With the BCTL+BCTL* combined tableau we will need temporal successors to 'E' type BCTL formulas
	// Unlike BCTL* we cannot assume they have a hue to satisfy them.
        public ArrayList<Integer> get_E_nostar() {
            ArrayList<Integer> e = new ArrayList<Integer>();

            for (int j=this.nextSetBit(0);j>=0;j=nextSetBit(j+1)) {
		//if (sf.topChar(j) == 'E' && sf.AE_no_star(j)) {
		int L=sf.left(j); char c=sf.topChar(j);
		if ( c=='I' && (!get(sf.right(j))) )                  e.add(j);
		if ( c=='-' && sf.topChar(L)=='Y' && !get(sf.negn(sf.left(L)))) e.add(j);
		if ( c=='-' && sf.topChar(L)=='B' ) e.add(sf.negn(sf.left(L)));
            }

	    JNode.out.print (toString() + " ENS-> ");
	    for (int i: e) { JNode.out.print(formulaToString(i)+", "); }
	    JNode.out.println ("");
            return e;

        }

	public ArrayList<Integer> hasTop(String set) {
            ArrayList<Integer> e = new ArrayList<Integer>();
            for (int j=this.nextSetBit(0);j>=0;j=nextSetBit(j+1)) {
	    	if (set.indexOf(sf.topChar(j))>-1) e.add (j);
	    }
	    return e;
	}



        public ArrayList<Integer> addEventualities(ArrayList<Integer> e) {

            for (int j=this.nextSetBit(0);j>=0;j=nextSetBit(j+1)) {
                if (sf.topChar(j)=='U'||sf.topChar(j)=='Y'||sf.topChar(j)=='I') {
                    if (!get(sf.right(j))) {
                        e.add(sf.right(j));
                    }
                }
            }

            return e;

        }

	public ArrayList<Integer> getEventualities() {
            ArrayList<Integer> e = new ArrayList<Integer>();
	    return addEventualities(e);
	}

        public ArrayList<Integer> getEventualities_AU() {
            ArrayList<Integer> e = new ArrayList<Integer>();

            for (int j=this.nextSetBit(0);j>=0;j=nextSetBit(j+1)) {
		int fS = sf.followString(j,"Y-");
		if (fS >= 0 && !get(sf.negn(sf.left(fS))))
		    e.add(j);
            }

            return e;

        }

        public ArrayList<Integer> addEventualities_AU2(char top, ArrayList<Integer> e) {
            for (int j=this.nextSetBit(0);j>=0;j=nextSetBit(j+1))
		if (sf.topChar(j)==top) 
		    if (!get(sf.right(j)))
			e.add(j);

            return e;

        }


	public JHue temporalSuccessor(int i) {
		//if(statehue) {
		//	assert(JNode.use_no_star);
		//} //else {
			JHue succ=new JHue(statehue,this.sf);
			for (int j=this.nextSetBit(0);j>=0;j=nextSetBit(j+1)) {
				int t=j;
				//if(statehue) {
				//	if (sf.AE_no_star(j) && sf.topChar(j)=='A') t=sf.left(j);
				//	else continue;
				//}
				switch (sf.topChar(t)) {
				    case 'U': case 'Y':
					if (!get(sf.right(t))) {
					    succ.set(j);
					}
					break;
				    case 'X': case 'B': succ.set(sf.left(t)); break;
				    case '-': int k=sf.left(t);
					  switch (sf.topChar(k)) {
					      case 'U': case 'I': if (!get(sf.negn(sf.left(k))))
						  succ.set(j);
					      break;
					      case 'X': succ.set(sf.negn(sf.left(k)));
					  }
				}
			}
		//}
		JNode.out.println(this.toString() + " X-> " + succ.toString() );
		return succ;
	}

	public boolean hasContradiction() {
		for (int i=nextSetBit(0); i!=-1 ;i=nextSetBit(i+1)) {
                        if (sf.topChar(i)=='0') return true;
			if (sf.topChar(i)=='-') {
				if (get(sf.left(i))) return true;
                                if (sf.topChar(sf.left(i))=='1') return true;
			}
		}
		return false;
	}

/*
	public int[] linearRules(int f) {
		int[] ret = {};
		if (sf.topChar(f)=='^') {
			ret = {sf.left(f), sf.right(f)};
		};
		return ret;
	}

	public void complete() {
		for (i=0;i<length; i++) {

		}
	}
*/
       /**
         * Prints formula number f as a string. This doesn't really belong
         * in JHue, but I don't want to add to many methods to Marks code
         *
         * @param f
         * @return
         */

    public static String formulaToStringV(int f) {
        return JHueEnum.e.ft.getSubformulas()[f].toString();
        //return JHueEnum.e.ft.getSubformulas()[f].toString();
    }
        public static String formulaToString(int f) {
            // BUGGY: return JHueEnum.e.ft.getSubformulas()[f].abbrev().toString();
        return JHueEnum.e.ft.getSubformulas()[f].toString();
            //return JHueEnum.e.ft.getSubformulas()[f].toString();
        }

public String toString() {
	String s="{";
        String comma="";
	for (int i=nextSetBit(0); i!=-1 ;i=nextSetBit(i+1)) {
		s+=comma+formulaToString(i);
                comma=", ";
	}
	s=s+"}";

	return s;
}

private int hashCode_() {
	return super.hashCode();
}

public int hashCode() {
	if (cached_hashcode == -1) {
		cached_hashcode = hashCode_();
	} else {
		if (cached_hashcode != hashCode_()) {
			throw new RuntimeException("Hashcode is Variant!");
			// Java Collections usually assume that Hashcode is invariant, so if we return different hashCodes, something bad is probably happening.
		}
	}
    return (cached_hashcode);
}


}
