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
    int num_hues;
    static int max_num_hues_in_colour=0;
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
     }
    
    public JColour2(JHueEnum e, int h) {
        num_hues = 1;
        hues = new int[1];
        hues[0] = h;
    }

    public JColour2(JHueEnum e, int h, int h2) {
        num_hues = 2;
        hues = new int[2];
        hues[0] = h;
        hues[1] = h2;
    }

    public String toString() {
    	
    	String e = "";

        JHueEnum he = JHueEnum.e;
        //String s="C:{" + Integer.toString(hues[0]);
        String s = "C:{" + he.toString(hues[0]);
        for (int i = 1; i < num_hues; i++) {
            if (JNode.use_optional_hues) {
            	if (JHueEnum.e.isEssential(hues[i])) {
            		e="e:";
            	} else {
            		e="";
            	}
            }
            //s=s+", "+Integer.toString(hues[i]);
            s = s + ", " + e +  he.toString(hues[i]);
        }
        s = s + '}';
        return s;
    }

    public JColour2(JColour2 c) {
        num_hues = c.num_hues;
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
        num_hues = bi.bitCount() + 1;
        hues = new int[num_hues];
//		he=c.he;
        int ch0 = c.hues[0];
        int Xch0 = JHueEnum.e.temporalSuccessor(ch0);
        hues[0] = Xch0;
        //hues[0]=he.temporalSuccessor(c.hues[0]);
        int j = 1;
        for (int i = 1; i < c.num_hues; i++) {
            if (bi.testBit(i - 1)) {
                //System.out.println(">>>"+JHueEnum.e.toString(c.hues[i]));
                hues[j] = JHueEnum.e.temporalSuccessor(c.hues[i]);
                j++;
            }
        }
        //System.out.println(c.toString()+ " -> "+toString());
    }

//    public JColour2(JColour2 c, int h, boolean essential) {
        public JColour2(JColour2 c, int h) {
        num_hues = c.num_hues + 1;
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
        int h = hues[index_of_hue];
        int h_plus_f = he.addFormula2Hue(f, h);
        hues[index_of_hue] = h_plus_f;
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
        isNormalised=true;
    }

    
    private void normalise_() {
    	assertNotLocked();
        //System.out.format("Norm A %s \n",toString());
        JHueEnum he = JHueEnum.e;
        for (int j = 0; j < num_hues; j++) {
            if (hues[j] == 0) {
                contradiction = true;
                return;
            }
        }

        if (num_hues > 1) {
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
                    if (sf.topChar(f) == 'A' || //All paths
                            (sf.topChar(f) >= 'a' && sf.topChar(f) <= 'z')) //variable
                    {
                        Aformulas[f] = true;
                    }
                    if (sf.topChar(f) == '-') {
                        int leftc = (sf.topChar(sf.left(f)));
                        if (leftc >= 'a' && leftc <= 'z') {
                            Aformulas[f] = true;
                        }
                    }
                }
            }
            for (int f = 0; f < num_subformulas; f++) {
                if (Aformulas[f]) {
                    for (int j = 0; j < num_hues; j++) {
                        hues[j] = he.addFormula2Hue(f, hues[j]);
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
        return JHueEnum.e.int2Hue(hues[0]).getEventualities();
    }

    public boolean hasContradiction() {
        return contradiction;
    }

    public boolean equals(Object o) {
        if (o.getClass() != getClass()) {
            throw new RuntimeException();
        }
        JColour2 c = (JColour2) o;
        return java.util.Arrays.equals(hues, (c.hues));
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
