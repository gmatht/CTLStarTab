package formulas;

import java.util.*;

public abstract class JBranch {

    /*public JBranch (JColour2 c) {
    
    }*/
    JNode parent;
    JColour2 col;
//	int num_children_created=0;
    int max_children = 0;
    private HashMap<Integer, JNode> satisfied_by = new HashMap<Integer, JNode>();

    /* The direct subchild which satisfies the eventuality (not the final node) */
    private HashMap<Integer, JNode> satisfied_by_D = new HashMap<Integer, JNode>();
    public void clear_eventualities () {
	     satisfied_by = new HashMap<Integer, JNode>();
	     satisfied_by_D = new HashMap<Integer, JNode>();
    }


    public int num_children_created() {
        return children.size();
    }

    /** 
     * Set of children than can be used to satisfy eventualities.
     * @return 
     */
    public ArrayList<JNode> eChildren() {
        return children;
    }

    public boolean eventualitiesSatsified() {
        ArrayList<Integer> e = col.getEventualities();
        for (int f : e) {
            if (satsifiedBy(f) == null) {
                return false;
            }
        }
        return true;
    }

    public String eventualityString() {
        String comma = "", s = "{";
        ArrayList<Integer> e = col.getEventualities();
        for (int f : e) {
            if (satsifiedBy(f) == null) {
                s+=comma+JHue.formulaToString(f)+":0";
            } else {
		if (satsifiedBy_D(f)==null) {
            		s+=comma+JHue.formulaToString(f)+":"+satsifiedBy(f).toString()+"/NULL";
		} else {
                	s+=comma+JHue.formulaToString(f)+":"+satsifiedBy(f).toString()+"/"+satsifiedBy_D(f).toString();
		}
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

    public JNode satsifiedBy(int f) {
        if (JHueEnum.e.int2Hue(col.hues[0]).get(f)) {
            return parent;
        } else {
            //update_eventuality0(f);
            JNode sat_by = satisfied_by.get(f);
            if (sat_by != null) {
		//TODO: I added the satsified_by_D(irect descendant) test long after
		// I originally wrote this code. This fixed a bug with 
		// -((((EXa)U(Xa)))>(((Xa)U(((EXEa)U(Xa)))))) appearing to be 
		// satisfiable. However, is this enough?
		// I think it should be since we can follow the pruning back.
		// In any case I MUST port to JHBranch too, or the hue optimization will probably be incorrect.
                if (sat_by.pruned || satisfied_by_D.get(f).pruned) {
                    return null;
                }
            }
            return satisfied_by.get(f);
        }
    }

    public JNode satsifiedBy_D(int f) {
        if (JHueEnum.e.int2Hue(col.hues[0]).get(f)) {
            return parent;
        } else {
            //update_eventuality0(f);
            JNode sat_by = satisfied_by_D.get(f);
            if (sat_by != null) {
                if (sat_by.pruned) {
                   // return null;
                }
            }
            return satisfied_by.get(f);
        }
    }

    protected boolean update_eventuality0(int f) {
        JNode sat_by = satisfied_by.get(f);
        if (sat_by != null) {
            if (sat_by.pruned || satisfied_by_D.get(f).pruned) {
                satisfied_by.put(f, null);
                satisfied_by_D.put(f, null);
                return true;
            }
        }
        return false;
    }

    public boolean update_eventuality(int f) {
        boolean updated = false;
        JNode sat_by = satisfied_by.get(f);
        if (sat_by != null) {
            if (sat_by.pruned || satisfied_by_D.get(f).pruned) {
                sat_by = null;
                satisfied_by.put(f, null);
                satisfied_by_D.put(f, null);
                updated = true;
            }
        }
        if (sat_by == null) {
            for (JNode c : eChildren()) {
                if (c.b != null && !c.pruned) {
                    sat_by = c.b.satsifiedBy(f);
                    if (sat_by != null) {
                        satisfied_by.put(f, sat_by);
                	satisfied_by_D.put(f, c);
		    	//JNode.out.println("SAT col.toString() + " Sat by D:" + c.toString() +" F:"+JHue.formulaToString(f)+":"+satsifiedBy(f).toString());
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
        ArrayList<Integer> e = JHueEnum.e.getEventualities(col.hues[0]);
        boolean updated = false;
        for (int i : e) {
            if (update_eventuality(i)) {
                updated = true;
            }
        }
        //if (updated) parent.update_eventualities();
        return updated;
    }
    ArrayList<JNode> children = new ArrayList<JNode>();
    static JBinaryRule or_rule = new JOrRule(), until_rule = new JUntilRule(), not_until_rule = new JNotUntilRule();

    public abstract JNode addchild();

    public abstract String type();

    public abstract boolean isCovered();

    public JNode getChild(int i) {
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

    public static JBranch create(JNode node) {
        JBranch ret = create(node.col);
        if (ret != null) {
            ret.parent = node;
            if (ret.col != node.col) {
            	throw new RuntimeException("col mismatch " + node);
            }
        }
        return ret;
    }

    private static JBranch create(JColour2 c) {
        JBranch ret;
/*        if (or_rule == null) {
            or_rule = new JOrRule();
            until_rule = new JUntilRule();
            not_until_rule = new JNotUntilRule();
        }*/
        if (c == null) {
            throw new NullPointerException();
        }
        //System.out.println(c.toString());
        //ret = Jor.create(c); if (ret != null) return ret;
        ret = JBinaryBranch.create(c, or_rule);
        if (ret != null) {
            return ret;
        }
        c.check();
        ret = JBinaryBranch.create(c, until_rule);
        if (ret != null) {
            return ret;
        }
        ret = JBinaryBranch.create(c, not_until_rule);
        if (ret != null) {
            return ret;
        }
        ret = JE.create(c);
        if (ret != null) {
            return ret;
        }
        ret = JChooseHue.create(c);
        if (ret != null) {
            return ret;
        }
        return ret;
    }
}

abstract class JDisjunctBranch extends JBranch {

    @Override
    public boolean isCovered() {
        for (int i = 0; i < num_children_created(); i++) {
            JNode child;
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

final class JE extends JDisjunctBranch {

    int E_formula; // The formula that must exist in some hue;

    public String type() {
        return "E";
    }

    public JE(JColour2 c, int f) {
        col = c;
        max_children = c.num_hues;
        //System.out.format("dE\n");
        E_formula = f;
    }

    @Override
    public JNode addchild() {
        if (isFull()) {
            return null;
        }
        int n = num_children_created();
        int h = col.hues[n];
        //System.out.format("JE using hue %d\n",h);
        h = JHueEnum.e.addFormula2Hue(E_formula, h);

        //System.out.format("JE using hue2 %d\n",h);
        JColour2 c = new JColour2(col, h);
        c.normalise();
        JNode node = JNode.getNode(c, this);
        children.add(node);
//		num_children_created++;
        update_eventualities();
        return node;
    }

    public static JE create(JColour2 c) {
        if (c == null) {
            throw new NullPointerException();
        }
        //System.out.println(c.toString());
        int num_hues = c.num_hues;
        JHueEnum he = JHueEnum.e;
        Subformulas sf = he.sf;
        for (int i = 0; i < num_hues; i++) {
            JHue h = he.int2Hue(c.hues[i]);
            {
                //int num_subformulas=c.he.sf.count();
                for (int f = h.nextSetBit(0); f != -1; f = h.nextSetBit(f + 1)) {
                    //System.out.println(sf.topChar(f));
                    if (sf.topChar(f) == '-') {

                        int left = sf.left(f);
                        //System.out.println(Character.toString(sf.topChar(f))+sf.topChar(left));

                        if (sf.topChar(left) == 'A') {
                            int neg = sf.negn(sf.left(left));
                            //System.out.println("Found E");
                            //System.out.format("Must have %d\n",neg);
                            boolean E_satisfied = false;
                            innerloop:
                            for (int j = 0; j < num_hues; j++) {
                                int h2 = c.hues[j];
                                if (he.h_has_f(h2, neg)) {
                                    E_satisfied = true;
                                    break innerloop;
                                }

                            }
                            //System.out.println("Found E");

                            if (!E_satisfied) {
                                //System.out.format("JE %s,%d\n",c.toString(),neg);
                                return new JE(c, neg);
                            }
                        }
                    }
                }
            }

        }
        return null;
    }
}

/*
 * This class is used to choose a hue that we will try to find a temporal successor for
 */
final class JChooseHue extends JBranch {

    int E_formula; // The formula that must exist in some hue;
    HashMap<Integer, JNode> satisfied_by = new HashMap<Integer, JNode>();

    @Override
    public String type() {
        return "H";
    }

    @Override
    public boolean conjunct() {
        return true;
    }

    public JChooseHue(JColour2 c) {
        col = c;
        max_children = c.num_hues;
    }

    public static JChooseHue create(JColour2 c) {
        return new JChooseHue(c);
    }

    public boolean eIsFull() {
        return (num_children_created() > 0);
    }

    /*	
    public JChooseHue(JNode node) {
    JColour2 c=node.col;
    parent=node;
    col=c;
    max_children=c.num_hues;
    }
     */
    @Override
    public JNode addchild() {
        if (isFull()) {
            return null;
        }
        JColour2 c = new JColour2(col);
        int n = num_children_created();
        c.setFirstHue(n);
        c.normalise();
        JNode node = JNode.getNodeX(c, this); //Can't re-use node here as we have to know whether we are JChooseHue or a TemporalSuccessor
        //JNode node=JNode.getNode(c);
        if (node.b == null) {
            node.b = new JTemporalSuccessor(c);
            node.b.parent = node;
        }
        children.add(node);
        
        update_eventualities();
        //num_children_created++;
        return node;
    }

    @Override
    public boolean isCovered() {
        if (num_children_created() != max_children) {
            return false;
        }
        for (int i = 0; i < num_children_created(); i++) {
            JNode child;
            child = getChild(i);
            if (child == null) {
                return false;
            }
            if (child.pruned) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<JNode> eChildren() {
        ArrayList<JNode> first_child = new ArrayList<JNode>();
        if (!children.isEmpty())
            first_child.add(children.get(0));
        return first_child;
    }
}

final class JTemporalSuccessor extends JDisjunctBranch {

    int E_formula; // The formula that must exist in some hue;

    public String type() {
        return "X";
    }

    public JTemporalSuccessor() {
        throw new RuntimeException("Empty Constructor Called");
    }

    public JTemporalSuccessor(JColour2 c) {
        if (c.hues.length < 1) {
            throw new RuntimeException("Temporal Successor taken of Colour with no Hues.");
        }
        col = c;
        if (c.num_hues > 30) {
            //Integer overflow may ensue
            //throw new RuntimeException();
            //OK instead we will set max children to MAXINT.
            //  Then we will make sure we throw an exception before we reach MAXINT.
            max_children=Integer.MAX_VALUE;
        } else {
            max_children = 1 << (c.num_hues - 1);
        }
    }

    @Override
    public JNode addchild() {
        if (this.num_children_created()>=Integer.MAX_VALUE) {
                //If max children == MAXINT, then this is implemenation defined
                //maximum, not the true mathematical maximum.
                throw new RuntimeException("Number of children has reached MAXINT");
        }
        if (isFull()) {
            return null;
        }
        int n = num_children_created();
        JColour2 c = new JColour2(col, new java.math.BigInteger(Integer.toString(n)));
        c.normalise();
        JNode node = JNode.getNode(c, this);
        children.add(node);
//		num_children_created++;
        update_eventualities(); 
        return node;
    }
}

abstract class JBinaryRule {

//    Subformulas sf = JHueEnum.e.sf;
      //static Subformulas sf;
    //Subformulas sf = JHueEnum.e.sf;
    final Subformulas sf() { return (JHueEnum.e.sf); }  
	
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
        return sf().negn(sf().left(f));
    }

    ;
        public int right_choice(int f) {
        return sf().negn(sf().right(f));
    }
;

}

final class JUntilRule extends JBinaryRule {

    public String topString() {
        return "U";
    }
        public int left_choice(int f) {
            int ret = sf().right(f);
            // Why are these needed?
            if (ret < 0) throw new RuntimeException("Until Left Choice == "+ret+":"+JHueEnum.e.sf.right(f)+ ", from: "+f+" " + JHue.formulaToStringV(f));
            return ret;
        
        }

    ;
        public int right_choice(int f) {
            int ret = sf().negn(sf().right(f));
            //if (ret < 0) throw new RuntimeException("Until Left Choice == -1");
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
        return sf().negn(sf().left(f));
    }

    ;
        public int right_choice(int f) {
        return sf().left(f);
    }
; // May not be efficient
}


final class JBinaryBranch extends JDisjunctBranch {

    JBinaryRule rule;
    int formula;
    int hue_index;

    @Override
    public String type() {
        return rule.topString();
    }

    public JBinaryBranch(JColour2 c, JBinaryRule r, int f, int hi) {
        max_children = 4;
        rule = r;
        formula = f;
        hue_index = hi;
        col = c;
	//JNode.out.println("Branch: "+r.topString()+" "+JHue 
    }

    public JNode addchild(int i) {
        JColour2 c;
        if (col == null) {
            throw new NullPointerException();
        }
        Subformulas sf = JHueEnum.e.sf;
        switch (i) {
            //Left hand side of or is true
            case 0:
                c = new JColour2(col);
                c.assert_formula(hue_index, rule.left_choice(formula));
                break;
            // Right side or or is true;
            case 1:
                c = new JColour2(col);
                c.assert_formula(hue_index, rule.right_choice(formula));
                break;
            // Both left and right are true, but in different hues.
            case 2:
                int new_hue = JHueEnum.e.addFormula2Hue(rule.left_choice(formula), col.hues[hue_index]);
                c = new JColour2(col, new_hue);
                c.assert_formula(hue_index, rule.right_choice(formula));
                break;
             case 3:
                new_hue = JHueEnum.e.addFormula2Hue(rule.right_choice(formula), col.hues[hue_index]);
                c = new JColour2(col, new_hue);
                c.assert_formula(hue_index, rule.left_choice(formula));
                break;               
            default:
                throw new RuntimeException();
        }
//		num_children_created++;
                c.normalise();
        JNode node = JNode.getNode(c, this);

        children.add(node);
        
        update_eventualities();
        //System.out.format("Jor %d : %s\n", i, c.toString());
        return node;
    }

    @Override
    public JNode addchild() {
        if (isFull()) {
            return null;
        }
        return addchild(num_children_created());

    }

    public static JBinaryBranch create(JColour2 c, JBinaryRule r) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c.hasContradiction()) {
            return null;
        }
        c.check();
        //System.out.println(c.toString());
        int num_hues = c.num_hues;
        JHueEnum he = JHueEnum.e;
        Subformulas sf = he.sf;
        for (int i = 0; i < num_hues; i++) {
            JHue h = he.int2Hue(c.hues[i]);
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
                        return new JBinaryBranch(c, r, subf, i);
                    }
                }
            }

        }
        return null;
    }
}
