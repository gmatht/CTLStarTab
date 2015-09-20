// TODO: Force Colours to be final before 

/**
 * 
 */
package formulas;

import java.util.*;
//import java.lang.*;

/**
 * @author John
 *
 */
public class JColour2 {

    boolean isNormalised=false; // for test purposes only.
    
    int hues[];
    int state_hue=-1;
    int state_E=-1;
    int num_hues;
    static int max_num_hues_in_colour=0;
    public static boolean state_variables = true; // Set this to false to use path variables
    boolean contradiction = false;
//	JHueEnum he; //We don't really need a copy of this pointer in every colour.
    boolean pruned = false;
    
    int cached_hashcode=-1;
    
//   HashSet<Integer> essential_hues; // should probably use negative integers, as it works out simpler

    public Object clone() {
    	throw new RuntimeException("Clone not supported at the moment");
    }
    
    public JColour2() {
        throw new RuntimeException();
    }

    /**
     * For debug only;
     */
    public void check() {
    	//if (!contradiction)
    	for (int h : hues) {
    		if (h ==0) {
    			throw new RuntimeException();
    		//} else {
    		//	System.out.println(""+h);
    		}
        }
	assert (state_hue > 0 || !JNode.use_no_star); 
     }

    public JColour2(JHueEnum e, Subformulas sf) {
            JHue h = new JHue(sf);
            num_hues = 2;
            hues = new int[2];
	    hues[1]=JHueEnum.e.hue2Int(new JHue(sf));
	    if (JNode.use_no_star) {
            	//JHue sh = new JStateHue(sf);
            	JHue sh = new JHue(true,sf);
		//phi is the largest subformula of phi, hence (0) is phi
		if (sf.state_formula(0)) {
		    	//system.out.println("phi IS state-formula);
			sh.set(0);
		} else {
		        h.set(0);
		}
		state_hue=JHueEnum.e.hue2Int(sh);
		assert(state_hue>=0);
	    } else {
		h.set(0);
	    }
	    hues[0]=JHueEnum.e.hue2Int(h);
            normalise();

	    for (int i = 0; i < sf.count();i++) {
		String s=JHue.formulaToString(i);
		if (sf.path_sensitive(i)) s+=" PathS";
		if (sf.state_formula(i)) s+=" state_formula";
		JNode.out.println(s);
	    }
    }

    public JColour2(JHueEnum e, int h) {
        num_hues = 1;
        hues = new int[1];
        hues[0] = h;
	assert(!JNode.use_no_star);
    }

    public JColour2(JHueEnum e, int h, int h2) {
        num_hues = 2;
        hues = new int[2];
        hues[0] = h;
        hues[1] = h2;
	assert(!JNode.use_no_star);
    }

    public String toString(int hue_index, int formula) {
    	
    	String e = "";
        JHueEnum he = JHueEnum.e;
        //String s="C:{" + Integer.toString(hues[0]);
        String s="C:";
	//s += hue_index +" ";
	//if (formula >=0) s+=JHue.formulaToString(formula);
	if (state_E != -1) {
		s = s + " e: "+JHue.formulaToString(state_E)+" ";
	}
	if (JNode.use_no_star) {
	    	assert(state_hue>=0);
		if (state_hue>=0) s = s + " [" + he.toString (state_hue,(hue_index==-1), formula) + "] ";
		else s = s + " [?] ";
	}
	s = s + "{" + he.toString(hues[0], (hue_index==0), formula);
        for (int i = 1; i < num_hues; i++) {
            if (JNode.use_optional_hues) {
            	if (JHueEnum.e.isEssential(hues[i])) {
            		e="e:";
            	} else {
            		e="";
            	}
            }
	    String prefix="";
	    if (i==hue_index) prefix = "** ";

            s = s + ", " + e + prefix + he.toString(hues[i], (hue_index==i), formula);
        }
        s = s + '}';
        return s;
    }

    public String toString() {
	return toString(-3,-3);
    }

    public JColour2(JColour2 c) {
        num_hues = c.num_hues;
	state_hue = c.state_hue;
	state_E = c.state_E;
        hues = (int[]) c.hues.clone();
//		he=c.he;
    }
    
    private void assertNotLocked() {
    	if (this.cached_hashcode != -1) {
    		throw new RuntimeException();
    	}
    }

    /**
     * Contructs the bi-th temporal successor, using bi as a bit field
     * to choose which hue works.
     *
     * @param c
     * @param bi
     */
    public JColour2(JColour2 c, java.math.BigInteger bi) {
        if (c == null) {
            throw new RuntimeException();
        }
        if (JNode.use_optional_hues) {
        	throw new RuntimeException("BUG: Why using BigInteger and Optional Hues?");
        }
        /*                if (c.he == null) {
        throw new RuntimeException();
        }*/
	state_E=c.state_E;
	int i0=1; if (state_E >= 0) i0=0;
        num_hues = bi.bitCount() + i0;
        hues = new int[num_hues];
	JHueEnum e=JHueEnum.e;
	if (JNode.use_no_star) {
		JNode.out.println("AA: "+ e.toString(c.state_hue));
		state_hue=e.temporalSuccessor(c.state_hue);
		if (state_E >=0) {
			JNode.out.println("AB: "+ e.toString(state_hue)+" + "+JHue.formulaToString(state_E));
			state_hue = e.addFormula2Hue(state_E, state_hue);
		}
		JNode.out.println("AC: "+ e.toString(state_hue));
	}
	if (i0>0) hues[0]   = e.temporalSuccessor(c.hues[0]);
        //hues[0]=he.temporalSuccessor(c.hues[0]);
        int j = i0;
        for (int i = i0; i < c.num_hues; i++) {
            if (bi.testBit(i - i0)) {
                //System.out.println(">>>"+JHueEnum.e.toString(c.hues[i]));
                hues[j] = JHueEnum.e.temporalSuccessor(c.hues[i]);
                j++;
            }
        }
        //System.out.println(c.toString()+ " -> "+toString());
    }

//    public JColour2(JColour2 c, int h, boolean essential) {

	// Creates a Colour with a new hue h
        public JColour2(JColour2 c, int h) {
        num_hues = c.num_hues + 1;
	state_hue = c.state_hue;
	state_E = c.state_E;
        hues = (new int[num_hues]);
        System.arraycopy(c.hues, 0, hues, 0, c.num_hues);
   /*     if (JNode.use_optional_hues) {
        	essential_hues = new HashSet<Integer>(c.essential_hues);
        	if (essential) {
        		essential_hues.add(num_hues - 1);
        	}
         }*/
//		he=c.he;
        hues[num_hues - 1] = h;
    }

    /**
     * Assert that formula number f is true in hue number h (Thus modifying colour)
     * @param h
     * @param f
     */
    public void assert_formula(int index_of_hue, int f) {
        JHueEnum he = JHueEnum.e;
	if (index_of_hue==-1) {
		JNode.out.println(he.toString(state_hue) + " a-> " +he.toString(he.addFormula2Hue(f, state_hue)));
		state_hue=he.addFormula2Hue(f, state_hue);
		JNode.out.println(he.toString(state_hue) + " -<a");
	} else {
	        int h = hues[index_of_hue];
        	int h_plus_f = he.addFormula2Hue(f, h);
	        hues[index_of_hue] = h_plus_f;
	}
    }

    /**
     * The first hue (possibly) indicates the hue that we are trying to find a temporal successor for
     * other than that the order of the hues does not matter, so we can 
     * sort the hues when normalizing.
     */
    private void sortAndPack() {
        int first_hue = hues[0];
        TreeSet<Integer> ts = new TreeSet<Integer>();
        for (int j = 1; j < num_hues; j++) {
        	int hue=hues[j];
            if (hue != first_hue) {
            	if (JHueEnum.e.isEssential(hue) || !JHNode.is_hue_bad(hue)) {
            		ts.add(hue);
            	}
            }
        }
        num_hues = ts.size()+1;
        hues = new int[num_hues];
        hues[0] = first_hue;
        int i = 1;
        for (int j : ts) {
            hues[i] = j;
            i++;
        }
    }

    
    public void normalise() {
        normalise_();
        normalise_(); 
        normalise_(); // make sure A formulas can go from path -> state ->path
        isNormalised=true;
    }

    
    private void normalise_() {
    	assertNotLocked();
        //System.out.format("Norm A %s \n",toString());
        JHueEnum he = JHueEnum.e;
	//JNode.out.println("NormC");

        if (state_hue == 0) {
                contradiction = true; return;
	}
	
        for (int j = 0; j < num_hues; j++) {
            if (hues[j] == 0) {
                contradiction = true; return;
            }
        }

        if (num_hues > 1 || JNode.use_no_star) {
            Subformulas sf = he.sf;
            int num_subformulas = sf.count();
            boolean[] Aformulas = new boolean[num_subformulas];
            for (int i = 0; i < num_subformulas; i++) {
                Aformulas[i] = false;
            }
            for (int i = 0; i < num_hues; i++) {
            	//TODO:opt no only essential
            	// JHBranch choose to eliminate optional hue?
                JHue h = he.int2Hue(hues[i]);
                for (int f = h.nextSetBit(0); f != -1; f = h.nextSetBit(f + 1)) {
                    //System.out.format("%s",sf.topChar(f));
                    /*if (sf.topChar(f) == 'A' || //All paths
                            (state_variables && sf.topChar(f) >= 'a' && sf.topChar(f) <= 'z')) //variable
                    {
                        Aformulas[f] = true;
                    }
                    if (sf.topChar(f) == '-') {
                        int leftc = (sf.topChar(sf.left(f)));
                        if (state_variables && leftc >= 'a' && leftc <= 'z') {
                            Aformulas[f] = true;
                        }
                    }*/
		    if (sf.state_formula(f)) Aformulas[f] =true;
                }
            }
			   JNode.out.println("Aformula: +JHue.formulaToString(f)"+toString());
	    if (JNode.use_no_star) {
		JHue h = he.int2Hue(state_hue);
		for (int f = h.nextSetBit(0); f != -1; f = h.nextSetBit(f + 1)) {
			JNode.out.println("Aformula1: "+JHue.formulaToString(f));
			if (sf.state_formula(f)) { 
			    Aformulas[f] =true; 
			    JNode.out.println("Aformula: "+JHue.formulaToString(f));
			}
		}
	    }
            for (int f = 0; f < num_subformulas; f++) {
                if (Aformulas[f]) {
		    //JNode.out.println("b: "+ he.toString(state_hue));
                    for (int j = 0; j < num_hues; j++) {
                        hues[j] = he.addFormula2Hue(f, hues[j]);
		    	if (sf.topChar(f) == 'A') {
				hues[j] = he.addFormula2Hue(sf.left(f), hues[j]);
				hues[j] = he.addFormula2Hue(f, hues[j]);
			}
                    }
		    if (JNode.use_no_star) {
			//JNode.out.println("Adding StateFormula: " + he.sf.toString(f)); 
			//JNode.out.println("bA: "+ he.toString(state_hue));
			state_hue=he.addFormula2Hue(f, state_hue);
			//JNode.out.println("bB: "+ he.toString(state_hue));
		    	if (sf.topChar(f)=='-' && sf.topChar(sf.left(f)) == 'A') {
				state_hue=he.addFormula2Hue(sf.negn(sf.left(sf.left(f))), state_hue);
			}
		    }
                }
            }
            for (int j = 0; j < num_hues; j++) {
                if (hues[j] == 0) {
                    contradiction = true;
                }

                if (JNode.use_hue_tableau) {
              	  //if (JHueEnum.e.isEssential(j)) { TODO: Add back in!
                	if (JHNode.is_hue_bad(hues[j])) {
                    		  contradiction = true;
                    	  //} else {
                    		  //TODO: Delete hue
                    		  //hues[j]=1;
                    	  //}
                          if (JNode.log)
                        	  JNode.out.format("Note: %s is a bad hue\n", JHueEnum.e.toString(hues[j]));
                          
                          return;                	  
                      }
                  //}
                }
            }
        }
//                		System.out.format("Norm B %s \n",toString());
	if (state_hue == 0) contradiction = true;
        sortAndPack();
        if (num_hues > max_num_hues_in_colour) {
            max_num_hues_in_colour = num_hues;
        }
    }

    public void setFirstHue(int index_of_hue) {
    	assertNotLocked();
        int temp = hues[0];
        hues[0] = hues[index_of_hue];
        hues[index_of_hue] = temp;
    }

    public java.util.ArrayList<Integer> getEventualities() {
	Subformulas sf = JHueEnum.e.sf;
	ArrayList<Integer>     ev ;
	if (state_E==-1) ev=JHueEnum.e.int2Hue(hues[0]).getEventualities();
	else ev = new ArrayList<Integer>();
	if (JNode.use_no_star) ev=JHueEnum.e.int2Hue(state_hue).addEventualities(ev);
	return ev;
    }

    public java.util.ArrayList<Integer> getEventualities_AU() {
	Subformulas sf = JHueEnum.e.sf;
	ArrayList ev = JHueEnum.e.int2Hue(state_hue).getEventualities_AU();
	if (state_E != -1 && sf.topChar(state_E) == 'I') 
	    ev.add(state_E);
	return ev;
    }
    public java.util.ArrayList<Integer> getEventualities_AU2() {
	Subformulas sf = JHueEnum.e.sf;
	ArrayList<Integer>     ev=new ArrayList<Integer>(2);
	JHueEnum.e.int2Hue(hues[0]).addEventualities_AU2('U',ev);
        ev=JHueEnum.e.int2Hue(state_hue).addEventualities_AU2('Y',ev);
	return ev;
    }
	/*if (state_E < 0) {
	//        return JHueEnum.e.int2Hue(hues[0]).getEventualities();
	//} else {
		java.util.ArrayList<Integer> se = new java.util.ArrayList<Integer>(1);
		if ("YI".indexOf(sf.topChar(state_E))>1) {
			se.add(state_E);
		}
		return se;
	}*/

    public boolean hasContradiction() {
        return contradiction;
    }

    public boolean equals(Object o) {
        if (o.getClass() != getClass()) {
            throw new RuntimeException();
        }
        JColour2 c = (JColour2) o;
        return java.util.Arrays.equals(hues, (c.hues)) && state_hue == c.state_hue && state_E == c.state_E; 
    }
    
    private int hashCode_() {
    	return java.util.Arrays.hashCode(hues);
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
