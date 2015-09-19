package formulas;

import java.io.PrintWriter;
import java.util.*;

public class JNode {

    JColour2 col;
    JBranch b;
    String type = "R";
    
    //static HashSet<JNode> eDecendants = null;
    //static HashSet<JNode> eDecendants = null;
    static HashSet<JNode> uncovered = null;
    static HashSet<JNode> unfulfilled = null;

    public static PrintWriter out = new PrintWriter(System.out);
    
    LinkedHashSet<JBranch> parents = new LinkedHashSet<JBranch>();
    boolean isleaf = false;
    boolean pruned = false;
    
    //static boolean use_hue_tableau = true;
    static public boolean use_hue_tableau = false;
    static boolean use_optional_hues = false; // not finished.
    static boolean use_no_star = true; // not finished. /* use combined BCTL and BCTL* tableau */
    public static boolean use_no_star_auto() {return true;}

    int node_num;

    public static boolean log = false;
    static HashMap<JColour2, JNode> col2node;
    static HashMap<JColour2, JNode> col2nodeX;
    static int num_nodes = 0;
    static int last_node_log=0;
    
    public static void static_reset() {
            col2node = new LinkedHashMap<JColour2, JNode>();
            col2nodeX = new LinkedHashMap<JColour2, JNode>();
            uncovered = new HashSet<JNode>();
            unfulfilled = new HashSet<JNode>();
            JColour2.max_num_hues_in_colour=0;
            num_nodes=0;
            last_node_log=0;
            JHueEnum.e = null;
            if (use_hue_tableau) {
            	JHNode.static_reset();
            	JHNode.out=out;
            }
    }
    
    public void prune() {
        pruned = true;
        update_eventualities();
	JNode.out.println ("Pruned "+toString());
        uncovered.remove(this);
        unfulfilled.remove(this);
        for (JBranch pb: parents) {
            //if (log) out.println("Updating: "+pb.parent);
            pb.parent.updateStatus();
        }
	// Now free no-longer needed structures.
	b = null;
	//parents = null; //actually, this is used by the --log
    }

    public String parentNodesAsString() {
        String s = "{", comma = "";
        for (JBranch p : parents) {
            s += comma;
            comma = ", ";
            if (p.parent == null) {
                s += "null";
            } else {
                s += p.parent.toString();
            }
        }
        return s + "}";

    }

    public String directDecendantsAsString() {
        String s = "{", comma = "";
        if (b == null) {
            return "0";
        }
        for (JNode n : b.children) {
            s += comma;
            comma = ", ";
            if (n == null) {
                s += "0";
            } else {
                s += n.toString();
            }
        }
        return s + "}";

    }

    public void update_eventualities() {
        if (b!= null) {
            b.update_eventualities_();
        }
        for (JBranch p : parents) {
            p.update_eventualities();
        }
        
        }
    
    public void updateStatus() {
        /*if (col.hasContradiction()) {
            prune();
        }*/
                    
        if (pruned) {
            uncovered.remove(this);
            unfulfilled.remove(this);
        } else {
                    if (b==null) {
                        uncovered.add(this);
                        return;
                    }
                    if (b.isCovered()) {
                        uncovered.remove(this);
                    } else {
                        uncovered.add(this);
                    }
                    if (b.eventualitiesSatsified()) {
                        unfulfilled.remove(this);
                    } else {
                        unfulfilled.add(this);
                    }
        }
    }


    public String toString() {
        String s;
        if (b == null) {
            s = "L";
        } else {
            s = b.type();
        }
        s+=node_num;
        //s += hashCode();
        return s;
    }

    public JNode() {
        throw new RuntimeException();
    }

    private JNode(JColour2 c) {
        node_num=num_nodes;
        num_nodes++;
        col=c;
        if (!c.isNormalised) {
        	throw new RuntimeException("Attempting to use non-normalised node");
        }
        if (c.hasContradiction()) {
            pruned = true;
        }
    }
        /*//JColour2 c; //hack
        JColour2 c2=new JColour2(c);
        c2.normalise();
        //c.normalise();
        if (c2.hasContradiction()) {
            pruned = true;
        }
        col = c2;*/
    
    /*JNode(JBranch b) {
    col=b.col;
    parents=new ArrayList<JBranch>()
    c.normalise();
    col=c;
    }*/
    static JNode getNode(HashMap<JColour2, JNode> col2node_pool, JColour2 c) {
        if (c.hues.length < 1) {
            throw new RuntimeException();
        }
        JNode node = col2node_pool.get(c);
        if (!col2node_pool.containsKey(c) && node == null) {
        	Timeout.yield();
//            node == null) {
        //if (node == null) {
            int size = col2node_pool.size();
            node = new JNode(c);
            col2node_pool.put(c, node);
            if (col2node_pool.size()!=size+1) {
                out.format("%d+ colours, %d+ hues, Max# hues in colour: %d+\n", 
                    JNode.col2node.size(), JHueEnum.e.int2hue.size(), JColour2.max_num_hues_in_colour);

                out.format("%d=%d+%d\n h%d", num_nodes, col2node.size(), col2nodeX.size());
                out.format("Sizes: %d %d %s.%d.%d\n",size, col2node_pool.size(), c.toString(),c.hashCode(),col2node_pool.get(c).col.hashCode() );
                out.flush();
                throw new RuntimeException();
            }
        }
        return node;
    }
    
    static boolean isTableauComplete() {
        return (uncovered.isEmpty() && unfulfilled.isEmpty());
    }

    static JNode getNode(HashMap<JColour2, JNode> col2node_pool, JColour2 c, JBranch parent) {
        if (!c.isNormalised) {
            throw new RuntimeException("Should normalise colours before adding them");
        }
        JNode n = getNode(col2node_pool, c);
        n.parents.add(parent);
        return n;
    }

    static JNode getNode(JColour2 c) {
        return getNode(col2node, c);
    }

    static JNode getNode(JColour2 c, JBranch parent) {
        return getNode(col2node, c, parent);       
    }

/*    static JNode getNodeX(JColour2 c) {
        JNode n = getNode(col2nodeX, c);
        n.type="T";
        return n;
    }*/

    static JNode getNodeX(JColour2 c, JBranch parent) {
        JNode n = getNode(col2nodeX, c, parent);
        n.type="T";
        return n;
    }

    public int hashCode() {
        return java.util.Arrays.hashCode(col.hues);
    }

    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            throw new RuntimeException();
        }
        JNode n = ((JNode) o);
        //return (java.util.Arrays.equals(col.hues, n.col.hues) && type.equals(n.type)) ;
        return (n.col==col && type.equals(n.type)) ;
    }

    public JBranch getBranch() {

        if (isleaf || pruned) {
            return null;
        } else if (b == null) {
        	col.check();
            if (col.hasContradiction()) {
                return null;
            }
            b = JBranch.create(this);
            if (b == null) {
                isleaf = true;
            }
        }
        return b;
    }

    public boolean complete() {
        return log_complete(0, "o");
    }

    public void checkFinished() {
        if (pruned) return;
        if (!b.isCovered()) {
            out.println(toString() + " IS NOT COVERED!!!, Size="+uncovered.size());
            out.println(log_line(1,"@","@"));
            for (JBranch p: parents) {
                if (p.parent.pruned) out.println("pruned:"); 
                out.println(p.parent.log_line(1,"@","@"));
            }
            throw new RuntimeException();
        }
        if (!b.eventualitiesSatsified()) {
            out.println(toString() + " IS NOT SATISFIED!!!");
            out.println(log_line(1,"@","@"));
            
            throw new RuntimeException();
        }
        updateStatus();
        if (!b.isCovered()) {
            out.println(toString() + " IS NOT COVERED!!!2, Size="+uncovered.size());
            out.println(log_line(1,"@","@"));
            if (!uncovered.contains(this)) {
                out.println(toString() + " And is not in uncovered!!!");
                
            }
            
        }
        if (!b.eventualitiesSatsified()) {
            out.println(toString() + " IS NOT SATISFIED!!!2");
            out.println(log_line(1,"@","@"));
        }

    }
    
    public boolean isWorkRequired() {
        boolean ret = isWorkRequired_();
        if (ret) {
            out.format("WR: %s %s\n", toString(), col.toString());
        }
        return ret;
    }

    public boolean isWorkRequired_() {
        if (isleaf || col.hasContradiction()) {
            return false;
        }
        if (b == null) {
            return true;
        }
        if (b.isFull()) {
            return false;
        }
        if (!b.isCovered()) {
            return true;
        }
        if ((b.eventualitiesSatsified())) {
            return false;
        }
        if ((b.eIsFull())) {
            return false;
        }
        return true;
    }

    public static boolean isWorkRequired_ALL() {
        return (isWorkRequired(col2node.values())
                || isWorkRequired(col2node.values()));
    }

    private static boolean isWorkRequired(Collection<JNode> nodes) {
        for (JNode n : nodes) {
            if (n.isWorkRequired()) {
                return true;
            }
        }
        return false;
    }

    private String log_line(int depth, String open_or_close, String reason) {
        String p = "";
        for (int i = 0; i < depth; i++) {
            p = p + " "; //not efficient, but hey.
        }
        String type;
        if (b!=null) {
            type=b.type();
        } else {
            type="L";
        }
        //p = p + open_or_close + " " +  toString() +"."+col.hashCode() + " " + reason + " ";
        p = p + open_or_close + " " +  toString() + " " + reason + " ";
        p += col.toString();

        p += " P:" + parentNodesAsString();

        if (!">".equals(open_or_close)) {
            p += " D:" + directDecendantsAsString();
        }
        if (b!=null) p += " S:" + b.eventualityString();

        return p;
    }

    public boolean log_complete(int depth, String reason) {
        /*for (int i=0;i<depth;i++) {
        System.out.format("  ");
        }
        System.out.println(col.toString());*/
        //log = false;

        boolean complete = complete(depth, reason);
        if (!complete) {
            prune();
        } else {
            /*if (b.eventualitiesSatsified()) {
                unfulfilled.remove(this);
            } else {
                unfulfilled.add(this);
            }*/
            updateStatus();
        }
            
        if (log) {
            String close;
            if (complete) {
                close = "Y";
            } else {
                if (isleaf || b == null) {
                    close=".";
                } else if (col.hasContradiction() || !b.isCovered()) {
                    close = "N";
                } else {
                    close = "n";
                }
            }

            out.println(log_line(depth, close, reason));
            //System.out.format("%d %d %s", depth, JNode.col2node.size(), toString());
            //System.out.format("%d %s", depth, JNode.col2node.size(), toString());
            /*if (b != null) {
            System.out.format("%s:%d/%d ", b.type(), b.num_children_created(), b.max_children);
            } else {
            System.out.format("L ");
            }*/
//            System.out.format("%s", col.toString());
//            System.out.format(" P:%s", parentNodesAsString());
//            System.out.format(" D:%s", this.directDecendantsAsString());
//            System.out.format(" S:%s", b.eventualityString());
//            System.out.println();
        }
//        if (!complete && b != null && !isleaf && !col.hasContradiction() && b.isCovered()) {
//            for (JNode n : b.children) {
//                System.out.println(n.toString() + " " + n.col.toString());
//            }
//        }
        return complete;
    }

    // Print out the whole tableau at end.
    public void log_graph(){
	log_graph_(0,"",new HashSet<JNode>());
    }
    public void log_graph_(int depth, String reason,HashSet<JNode> logged)
{
 if (!pruned && !logged.contains(this)) {
	logged.add(this);
	out.println(log_line(depth, "", reason));
	for (JNode c : b.eChildren()) {
		c.log_graph_(depth+1,reason,logged);
	}
 }
}
            
    public static void outputStatus(int depth, String p) {
                out.format("Nodes: %d=%d+%d Current Depth: %d\n ", num_nodes, col2node.size(), col2nodeX.size(),depth);
                out.format("%d%s colours, %d%s hues, Max# hues in colour: %d%s\n", 
                    JNode.col2node.size(),p, JHueEnum.e.int2hue.size(),p, JColour2.max_num_hues_in_colour,p);
                out.flush();
    }
    
    private boolean complete(int depth, String reason) {
       
            if (num_nodes%1000==0 && last_node_log!=num_nodes) {
                outputStatus(depth,"+");
                last_node_log=num_nodes;
            }

            if (!col.isNormalised) {
            	throw new RuntimeException();
            }
        
        if (col.hasContradiction() || pruned) {
            return false;
        }
        getBranch();
//        for (int i=0;i<depth;i++) {
//             System.out.format(" ");
//        }

        if (log) out.println(log_line(depth, ">", reason));

        if (isleaf) {
            //System.out.println("is leaf!");
            return !pruned;
        }
        JNode child;
        if (b == null) {
        	throw new RuntimeException("b is null ," + col.hasContradiction() + pruned + isleaf);
        }
	JNode.out.println ("XYZ");
        while ((!b.isFull()) && (!b.isCovered())) {
		JNode.out.println ("XYZ1");
            //while ((
            //while ((child=b.addchild())!=null) {)!=null) {
            //System.out.println("adding child");
            if (col.hues.length < 0) {
                throw new RuntimeException();
            }
            child = b.addchild();
            child.log_complete(depth + 1, "c");
            if (pruned) {
            	// if we end up pruning a node, branch can be set to null, so we want to quit quickly.
            	return false;
            }
        }
        if (!b.isCovered()) {
		JNode.out.println ("XYZr21");
            return false;
        }
        update_eventualities();
/*        while ((!b.eIsFull()) && (!b.eventualitiesSatsified())) {
            child = b.addchild();
            child.log_complete(depth + 1, "e");
            
            update_eventualities();
        }*/
        /*
        if (!b.eventualitiesSatsified()) {
            HashSet<JNode> eDecendants = new HashSet<JNode>();
            for (JNode n: b.eChildren()) {
                if (!eDecendants.contains(n) && !n.pruned) {
                    n.log_complete(depth+2,"!");
                    eDecendants.add(n);
                }
            }
        }*/
        
        //eComplete(depth);

        return (b.isCovered());
        //return (b.eventualitiesSatsified() && b.isCovered());
        //return (b.eventualitiesSatsified() && b.isCovered());
    }
    
    public void eComplete(int depth) {
        HashSet<JNode> eDecendants;
        if (pruned) return;
        while (!b.eventualitiesSatsified()) {
            eDecendants=new HashSet<JNode>();
            eDecendants.add(this);
            eCompleteR(depth+2,this,eDecendants);
            update_eventualities();
            for (JNode c: eDecendants) {
                    c.updateStatus();
            }

            if (!b.eventualitiesSatsified()) {
                // We must have built all eDecendants and still not been able to satisfy our eventualities
               if (log) out.println("Could not fulfill "+toString() + ", pruning;");
                /*if (JNode.use_no_star) {
		    for (JNode c: eDecendants) {

		else*/ 
	        //if (!JNode.use_no_star) 
		{
		    for (JNode c: eDecendants) {
                    if (c.b!=null && c.b.eventualitiesSatsified()) {
                        if (!b.eventualitiesSatsified()) {
                            out.println("???");
                            b.update_eventualities();

//                            b.update_eventualities();                            
                            out.println(log_line(0,"@","#"));
                            out.println(b.children.get(1).log_line(0,"",""));
                            
                            //out.println(c.parents.e .parent.log_line(0,"",""));                            
                            out.println(c.log_line(0,"X","X"));
                            out.println("X???");
                            out.flush();
                            throw new RuntimeException("How can a child " + c.toString() + " be satisfied if its parent " + toString() +" is not?");
                        } else { 
                            throw new RuntimeException("Pruning nodes satisifies eventualities?");
                        }
                    }
                    c.prune();
		 }
                }
                return;
                 
            }
            /*for (JNode c: eDecendants) {
                //c.log_complete(depth+1,"!");
                //c.getBranch();
                c.updateStatus();
                /*if (c.b != null) {
                    if (c.b.isCovered()) {
                        uncovered.remove(c);
                    } else {
                        uncovered.add(c);
                    }
                }* /
            } */
            ///It is possible that completing these nodes causes eventualities to be pruned, so we may have to try again.
        }
        updateStatus();
        
    }
    
    
    private void eCompleteR(int depth, JNode eNode, HashSet<JNode> eDecendants) {
        if (log) out.println(log_line(depth, ">", "!"));
        getBranch();
        if (col != b.col) {
        	//Boolean.toString(col == null)
        	System.out.println("col == null:" + (col == null));
        	System.out.println("b.col == null:" + (b.col == null));
        	System.out.println(":" + (b.parent == this));
           	System.out.println(b.col + " !=  " +col);
           	System.out.println(col + "");
           	System.out.println("Node: " +this);
                   	
        	throw new RuntimeException ("Colours do not match");
        }
        if (col.hasContradiction()) {
            prune();
            return;
        }
                while ((!b.eIsFull()) && (!eNode.b.eventualitiesSatsified())) {
                    JNode child=b.addchild();
                    child.getBranch();
                    child.update_eventualities();
                }

        
            for (JNode n: b.eChildren()) {
                if (eNode.b.eventualitiesSatsified()) {
                    return;
                }
                if (!n.pruned) {
                    if (!eDecendants.contains(n)) {
                        //out.println("Decendants do not yet contain: "+n);
                        eDecendants.add(n);
                        Timeout.yield(); 
                        n.eCompleteR(depth+1,eNode,eDecendants); //TODO: do we have a possible infinite loop here?
                    }
                }
            }
        
        
        //return eNode.b.eventualitiesSatsified();
        update_eventualities();
        return;
    }
    
}
