package formulas;

import java.io.PrintWriter;
import java.util.*;

public class JHNode {

    int _hue;
    JHBranch b;
    String type = "R";
    
    //static HashSet<JHNode> eDecendants = null;
    //static HashSet<JHNode> eDecendants = null;
    static HashSet<JHNode> uncovered = null;
    static HashSet<JHNode> unfulfilled = null;

    public static PrintWriter out = new PrintWriter(System.out);
    
    LinkedHashSet<JHBranch> parents = new LinkedHashSet<JHBranch>();
    boolean isleaf = false;
    boolean pruned = false;


    int node_num;

    public static boolean log = false;
    static LinkedHashMap<Integer, JHNode> col2node;
    static int num_nodes = 0;
    static int last_node_log=0;
    
    /**
     * Try to satisfy currently uncovered and unfulfilled nodes
     * 
     * TODO: add full_tidy()
     */
    
    public static void quick_tidy() {
        LinkedList<JHNode> S = new LinkedList<JHNode>(unfulfilled);    	
        out.println("\nFulfilling Nodes (" + S.size() + ")");
        for (JHNode n : S) {
            out.format(n.toString() + " ");

        }
        out.println();

        for (JHNode n : S) {
            n.eComplete(1);
        }
        S = new LinkedList<JHNode>(uncovered);

        out.println("\nCovering Nodes(" + S.size() + ")");
        for (JHNode n : S) {
            n.log_complete(1, "!");
        }
    	
    }
    
    
    /**
     * Try to show that hue is not satisfiable under LTL-like semantics.
     * 
     * I think this function is not complete, because it uses quick tidy, but it should at least be sound.
     * 
     * @param h A hue
     * @return true if we can show the hue is not satisfiable, and false otherwise.
     */
    
    public static boolean is_hue_bad (int h) {
   // 	JNode.out.println("Testing a hue " + JHueEnum.e.toString(h));
    	JHNode n = col2node.get(h);
    	if (n != null) {
    		return (n.pruned);
    	}
    	n = getNode(h);
    	n.complete();
    	while (!isTableauComplete()) quick_tidy();
    	
/*    	if (n.pruned) {
    		JNode.out.println("The hue " + JHueEnum.e.toString(h) + " is BAD.");
    	} else {
    		JNode.out.println("The hue " + JHueEnum.e.toString(h) + " is GOOD.");
    	}*/
    	return (n.pruned);
    }
    
    public static void static_reset() {
            col2node = new LinkedHashMap<Integer, JHNode>();
            uncovered = new HashSet<JHNode>();
            unfulfilled = new HashSet<JHNode>();
            num_nodes=0;
            last_node_log=0;
            JHueEnum.e = null;
    }
    
    public void prune() {
        pruned = true;
        update_eventualities();
        uncovered.remove(this);
        unfulfilled.remove(this);
        for (JHBranch pb: parents) {
            //if (log) out.println("Updating: "+pb.parent);
            pb.parent.updateStatus();
        }
	// Now free no-longer needed structures.
	//b = null;
	//parents = null; //actually, this is used by the --log
    }

    public String parentNodesAsString() {
        String s = "{", comma = "";
        for (JHBranch p : parents) {
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
        for (JHNode n : b.children) {
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
        for (JHBranch p : parents) {
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

    public JHNode() {
        throw new RuntimeException();
    }

    private JHNode(int h) {
        node_num=num_nodes;
        num_nodes++;
        //JColour2 c; //hack
        //c2=new JColour2(c);
        //c2.normalise();
        if (h==0) { //.hasContradiction()) {
            pruned = true;
        }
        _hue = h;
    }

    /*JHNode(JHBranch b) {
    col=b.col;
    parents=new ArrayList<JHBranch>()
    c.normalise();
    col=c;
    }*/
    static JHNode getNode(HashMap<Integer, JHNode> col2node_pool, int h) {
        JHNode node = col2node_pool.get(h);
        if (!col2node_pool.containsKey(h) && node == null) {
//            node == null) {
        //if (node == null) {
            int size = col2node_pool.size();
            node = new JHNode(h);
            col2node_pool.put(h, node);
            if (col2node_pool.size()!=size+1) {
                out.format("%d+ JHNodes, %d+ hues", 
                    JHNode.col2node.size(), JHueEnum.e.int2hue.size());

                out.format("%d=%d", num_nodes, col2node.size());
                out.format("Sizes: %d %d .%d\n",size, col2node_pool.size(),h);
                out.flush();
                throw new RuntimeException();
            }
        }
        return node;
    }
    
    static boolean isTableauComplete() {
        return (uncovered.isEmpty() && unfulfilled.isEmpty());
    }

    static JHNode getNode(HashMap<Integer, JHNode> col2node_pool, int h, JHBranch parent) {
        JHNode n = getNode(col2node_pool, h);
        n.parents.add(parent);
        return n;
    }

    static JHNode getNode(int c) {
        return getNode(col2node, c);
    }

    static JHNode getNode(int c, JHBranch parent) {
        return getNode(col2node, c, parent);       
    }

    public int hashCode() {
        return _hue; //java.util.Arrays.hashCode(col.hues);
    }

    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            throw new RuntimeException();
        }
        JHNode n = ((JHNode) o);
        return (_hue==n._hue);
    }

    public JHBranch getBranch() {
        if (isleaf || pruned) {
            return null;
        } else if (b == null) {
            if (_hue==0){ //.hasContradiction()) {
                return null;
            }
            b = JHBranch.create(this);
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
            for (JHBranch p: parents) {
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
            out.format("WR: %s %d\n", toString(), _hue);
        }
        return ret;
    }

    public boolean isWorkRequired_() {
        if (isleaf || _hue==0){ //.hasContradiction()) {
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

    private static boolean isWorkRequired(Collection<JHNode> nodes) {
        for (JHNode n : nodes) {
            if (n.isWorkRequired()) {
                return true;
            }
        }
        return false;
    }

    String log_line(int depth, String open_or_close, String reason) {
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
        p += _hue + ":" + JHueEnum.e.toString(_hue);

        p += " P:" + parentNodesAsString();

        if (!">".equals(open_or_close)) {
            p += " D:" + directDecendantsAsString();
        }
        if (b!=null) p += " S:" + b.eventualityString() + b.hint_text();

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
                } else if (_hue==0 || !b.isCovered()) {
                    close = "N";
                } else {
                    close = "n";
                }
            }

            out.println(log_line(depth, close, reason));
            out.flush();
            //System.out.format("%d %d %s", depth, JHNode.col2node.size(), toString());
            //System.out.format("%d %s", depth, JHNode.col2node.size(), toString());
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
//            for (JHNode n : b.children) {
//                System.out.println(n.toString() + " " + n.col.toString());
//            }
//        }
        return complete;
    }

            
    public static void outputStatus(int depth, String p) {
                out.format("Nodes: %d=%d Current Depth: %d\n ", num_nodes, col2node.size(),depth);
                out.format("%d%s JHNodes, %d%s hues", 
                    JHNode.col2node.size(),p, JHueEnum.e.int2hue.size(),p);
                out.flush();
    }
    
    private boolean complete(int depth, String reason) {
       
            if (num_nodes%1000==0 && last_node_log!=num_nodes) {
                outputStatus(depth,"+");
                last_node_log=num_nodes;
            }

        
        if (_hue==0 || pruned) {
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
        JHNode child;
        while ((!b.isFull()) && (!b.isCovered())) {
            //while ((
            //while ((child=b.addchild())!=null) {)!=null) {
            //System.out.println("adding child");
            child = b.addchild();
            child.log_complete(depth + 1, "c."+toString());
        }
        if (!b.isCovered()) {
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
            HashSet<JHNode> eDecendants = new HashSet<JNode>();
            for (JHNode n: b.eChildren()) {
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
        HashSet<JHNode> eDecendants;
        if (pruned) return;
        while (!b.eventualitiesSatsified()) {
            eDecendants=new HashSet<JHNode>();
            eDecendants.add(this);
            eCompleteR(depth+2,this,eDecendants);
            update_eventualities();
            for (JHNode c: eDecendants) {
                    c.updateStatus();
            }

            if (!b.eventualitiesSatsified()) {
                // We must have built all eDecendants and still not been able to satisfy our eventualities
               if (log) out.println("Could not fulfill "+toString() + ", pruning;");
                for (JHNode c: eDecendants) {
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
                return;
                 
            }
            /*for (JHNode c: eDecendants) {
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
    
    
    private void eCompleteR(int depth, JHNode eNode, HashSet<JHNode> eDecendants) {
        if (log) out.println(log_line(depth, ">", "!"));
        getBranch();
        if (_hue==0) {
            prune();
            return;
        }
                while ((!b.eIsFull()) && (!eNode.b.eventualitiesSatsified())) {
                    JHNode child=b.addchild();
                    child.getBranch();
                    child.update_eventualities();
                }

        
            for (JHNode n: b.eChildren()) {
                if (eNode.b.eventualitiesSatsified()) {
                    return;
                }
                if (!n.pruned) {
                    if (!eDecendants.contains(n)) {
                        //out.println("Decendants do not yet contain: "+n);
                        eDecendants.add(n);
                        
                        n.eCompleteR(depth+1,eNode,eDecendants);
                    }
                }
            }
        
        
        //return eNode.b.eventualitiesSatsified();
        update_eventualities();
        return;
    }
    
}
