package formulas;

import java.util.*;

public abstract class JHBranch {

    /*public JHBranch (JColour2 c) {
    
    }*/
    JHNode parent;
    int hue;
//	int num_children_created=0;
    int max_children = 0;
    private HashMap<Integer, JHNode> satisfied_by = new HashMap<Integer, JHNode>();
    
    public String hint_text() {
    	return "";
    }

    int hue2int(JHue h) {
	    return JHueEnum.e.hue2Int(h);
    } 

    JHue int2hue(int h) {
	    return JHueEnum.e.int2Hue(h);
    } 

    public void clear_eventualities () {
	     satisfied_by = new HashMap<Integer, JHNode>();
    }


    public int num_children_created() {
        return children.size();
    }

    /** 
     * Set of children than can be used to satisfy eventualities.
     * @return 
     */
    public ArrayList<JHNode> eChildren() {
        return children;
    }

    public boolean eventualitiesSatsified() {
	if (hue == 0) {
		parent.log_line(0,"","");
		return false;
	}
        ArrayList<Integer> e = int2hue(hue).getEventualities();
        for (int f : e) {
            if (satsifiedBy(f) == null) {
                return false;
            }
        }
        return true;
    }

    public String eventualityString() {
	if (hue==0) return "N/A";    
        String comma = "", s = "{";
        ArrayList<Integer> e = int2hue(hue).getEventualities();
        for (int f : e) {
            if (satsifiedBy(f) == null) {
                s+=comma+JHue.formulaToString(f)+":0";
            } else {
                s+=comma+JHue.formulaToString(f)+":"+satsifiedBy(f).toString();
            }
            comma=", ";
        }
        return s+"}";
    }

    /**
     * Returns true if we cannot attempt to satisfy eventualties on this branch
     * by adding more children.
     * @return 
     */
    public boolean eIsFull() {
        return isFull();
    }

    public JHNode satsifiedBy(int f) {
        if (JHueEnum.e.int2Hue(hue).get(f)) {
            return parent;
        } else {
            //update_eventuality0(f);
            JHNode sat_by = satisfied_by.get(f);
            if (sat_by != null) {
                if (sat_by.pruned) {
                    return null;
                }
            }
            return satisfied_by.get(f);
        }
    }

    protected boolean update_eventuality0(int f) {
        JHNode sat_by = satisfied_by.get(f);
        if (sat_by != null) {
            if (sat_by.pruned) {
                satisfied_by.put(f, null);
                return true;
            }
        }
        return false;
    }

    public boolean update_eventuality(int f) {
        boolean updated = false;
        JHNode sat_by = satisfied_by.get(f);
        if (sat_by != null) {
            if (sat_by.pruned) {
                sat_by = null;
                satisfied_by.put(f, null);
                updated = true;
            }
        }
        if (sat_by == null) {
            for (JHNode c : eChildren()) {
                if (c.b != null && !c.pruned) {
                    sat_by = c.b.satsifiedBy(f);
                    if (sat_by != null) {
                        satisfied_by.put(f, sat_by);
                        return true;
                    }
                }
            }
        }
        return updated;
    }

    public boolean update_eventualities() {
        boolean updated = update_eventualities_();
        if (updated) parent.update_eventualities();
        return updated;
    }

    
    public boolean update_eventualities_() {
	if ( hue == 0 ) {
		return false;
	}
        ArrayList<Integer> e = JHueEnum.e.getEventualities(hue);
        boolean updated = false;
        for (int i : e) {
            if (update_eventuality(i)) {
                updated = true;
            }
        }
        //if (updated) parent.update_eventualities();
        return updated;
    }
    ArrayList<JHNode> children = new ArrayList<JHNode>();
    //static JBinaryRule or_rule = new JOrRule(), until_rule = new JUntilRule(), not_until_rule = new JNotUntilRule();

    public abstract JHNode addchild();

    public abstract String type();

    public abstract boolean isCovered();

    public JHNode getChild(int i) {
        return children.get(i);
    }

    public boolean isFull() {
        return max_children == num_children_created();
    }

    /**
     * @return true, if all children must be satisfied (else any child can be satisfied).
     */
    public boolean conjunct() {
        return false;
    }

    public static JHBranch create(JHNode node) {
        JHBranch ret = create(node._hue);
        if (ret != null) {
            ret.parent = node;
        }
        return ret;
    }

    private static JHBranch create(int h) {
        JHBranch ret;
/*        if (or_rule == null) {
            or_rule = new JOrRule();
            until_rule = new JUntilRule();
            not_until_rule = new JNotUntilRule();
        }*/
        //System.out.println(c.toString());
        //ret = Jor.create(c); if (ret != null) return ret;
	    if ( h == 0 ) return null;
        ret = JHBinaryBranch.create(h, JBranch.or_rule);
        if (ret != null) {
            return ret;
        }
        ret = JHBinaryBranch.create(h, JBranch.until_rule);
        if (ret != null) {
            return ret;
        }
        ret = JHBinaryBranch.create(h, JBranch.not_until_rule);
        if (ret != null) {
            return ret;
        }
        ret = new JHTemporalSuccessor(h);
        //ret = JHTemporalSuccessor.create(h);
        return ret;
    }
}

abstract class JHDisjunctBranch extends JHBranch {

	public int formula;
	
    @Override
    public boolean isCovered() {
        for (int i = 0; i < num_children_created(); i++) {
            JHNode child;
            child = getChild(i);
            if (child != null) {
                if (!child.pruned) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean conjunct() {
        return false;
    }
}

final class JHTemporalSuccessor extends JHDisjunctBranch {

    public String type() {
        return "X";
    }

    public JHTemporalSuccessor() {
        throw new RuntimeException("Empty Constructor Called");
    }

    public JHTemporalSuccessor(int h) {
    	    hue=h;
            max_children = 1;
    }

    public JHNode addchild() {
        if (isFull()) {
            return null;	
        }
        int n = num_children_created();
        int h;
	if (hue == 0) {
		h=0;
	} else {
        h = JHueEnum.e.temporalSuccessor(hue);

		//System.out.format("%s -> %s\n", JHueEnum.e.toString(hue), JHueEnum.e.toString(h) ) ;
	}
	//if (hue == 0) return  JHNode.getNode(0, this);
        //c.normalise();
        JHNode node = JHNode.getNode(h, this);
        children.add(node);
		//num_children_created++;
        update_eventualities(); 
        return node;
    }
}

/*abstract class JBinaryRule {

    Subformulas sf = JHueEnum.e.sf;

    public abstract String topString();

    public abstract int left_choice(int f);

    public abstract int right_choice(int f);
}

final class JOrRule extends JBinaryRule {

    public String topString() {
        return "&-";
    }

    ;
        public int left_choice(int f) {
        return sf.negn(sf.left(f));
    }

    ;
        public int right_choice(int f) {
        return sf.negn(sf.right(f));
    }
;

}

final class JUntilRule extends JBinaryRule {

    public String topString() {
        return "U";
    }
        public int left_choice(int f) {
            int ret = sf.right(f);
            if (ret < 0) throw new RuntimeException("Until Left Choice == -1, from: "+f+" " + JHueEnum.e.toString(f));
            return ret;
        
        }

    ;
        public int right_choice(int f) {
            int ret = sf.negn(sf.right(f));
            if (ret < 0) throw new RuntimeException("Until Left Choice == -1");
            return ret;
    }
; // May not be efficient
}

final class JNotUntilRule extends JBinaryRule {

    public String topString() {
        return "U-";
    }

    ;
        public int left_choice(int f) {
        return sf.negn(sf.left(f));
    }

    ;
        public int right_choice(int f) {
        return sf.left(f);
    }
; // May not be efficient
}
*/

final class JHBinaryBranch extends JHDisjunctBranch {

    JBinaryRule rule;
    int formula;

    @Override
    public String type() {
        return rule.topString();

    }

    @Override
    public String hint_text() {
        return " " + formulaToString(formula) + " -> " + formulaToString(rule.left_choice(formula)) + " or " + formulaToString(rule.right_choice(formula));
    }

    public JHBinaryBranch(int h, JBinaryRule r, int f) {
        max_children = 2;
        rule = r;
        formula = f;
        hue = h;
    }
    
    private static String formulaToString(int f) {
        return JHueEnum.e.ft.getSubformulas()[f].abbrev().toString();
        //return JHueEnum.e.ft.getSubformulas()[f].toString();
    }   

    public JHNode addchild(int i) {
        JHue h=(JHue)(int2hue(hue).clone());
        Subformulas sf = JHueEnum.e.sf;
        switch (i) {
            //Left hand side of or is true
            case 0:
                h.set(rule.left_choice(formula));
                break;
            // Right side or or is true;
            case 1:
                h.set(rule.right_choice(formula));
                break;
            default:
                throw new RuntimeException();
        }
        JHNode node = JHNode.getNode(hue2int(h), this);

        children.add(node);
        
        update_eventualities();
        //System.out.format("Jor %d : %s\n", i, c.toString());
        return node;
    }

    @Override
    public JHNode addchild() {
        if (isFull()) {
            return null;
        }
        System.out.println(num_children_created());
        return addchild(num_children_created());

    }

    public static JHBinaryBranch create(int hu, JBinaryRule r) {
        if (hu==0) {
            return null;
        }
        //System.out.println(c.toString());
        JHueEnum he = JHueEnum.e;
        Subformulas sf = he.sf;
        //for (int i = 0; i < num_hues; i++) {
            JHue h = he.int2Hue(hu);
            //int num_subformulas=c.he.sf.count();
            for (int f = h.nextSetBit(0); f != -1; f = h.nextSetBit(f + 1)) {
                int subf = sf.followString(f, r.topString());
                //System.out.println(sf.topChar(f));
                if (subf >= 0) {
                    if (h.get(r.left_choice(subf)) || h.get(r.right_choice(subf))) {
                        //System.out.println("Jor already sat.");
                        // already satisfied
                    } else {
                        //System.out.format("Jor %s,%d\n",c.toString(),left);
                        return new JHBinaryBranch(hu, r, subf);
                    }
                }
            }

        // }
        return null;
    }
}
