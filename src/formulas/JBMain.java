package formulas;
//import java.lang.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * A rooted tableau for sat checking BCTL* formulas.
 * @author John
 */
public class JBMain implements Runnable {

    /**
     * A test harness added by John
     *   
     * @param args
     */
    //private static final String default_input = "-(G(p>q)>(Gp>Gq))";
    //private static final String default_input = "-(E(pU(E(pUq)))>E(pUq))";
    private static final String default_input = "AG(EXp&EX-p)&AG(Gp|((-r)U(r&-p)))";
    ///private static final String default_input = "-((AG(p>EXr)&AG(r>EXp))>(p>EG(Fp&Fr)))";

//    private static final String default_input = "-((AFGq>AFAGq))";
    //private static final String default_input = "G(p&q)&F-p";
    
    private String run_input_;

    public volatile int status;
	// 0: Not Satisfiable
	// 1: Satisfiable
	// 2: Exception
	// 3: Not Finished.
	

    public void run () {
  	status = go(run_input_);
    }

    public JBMain (String input) {
        run_input_ = input;
        status=3;
    }

    private static String toString(boolean b) {
        if (b) {
            return "true";
        } else {
            return "false";
        }
    }

    public static void go_LTL(String input) {
        Logic logic = new AUXLogic();
        //boolean problem_occured = false;

        try {
            JHNode.static_reset();
            JHNode.log=JNode.log;
            FormulaTree ft = logic.disabbreviate(logic.parse(input));

            Subformulas sf = new Subformulas(ft);
            JHueEnum he = new JHueEnum(sf, ft);
            JHueEnum.e = he;
            JHue h = new JHue(sf);
            h.set(0);
            if (JNode.use_optional_hues) h.setEssential(true);
            //JColour2 c = new JColour2(he, he.hue2Int(h), 1);
            //c.normalise();
            JHNode node = JHNode.getNode(he.hue2Int(h));

            node.complete();
            long time_wasted = 0;
            while (!JHNode.isTableauComplete()) {
                LinkedList<JHNode> S = new LinkedList<JHNode>(JHNode.unfulfilled);
                JHNode.out.println("\nFulfilling Nodes (" + S.size() + ")");
                for (JHNode n : S) {
                    JHNode.out.format(n.toString() + " ");

                }
                JHNode.out.println();

                for (JHNode n : S) {
                    n.eComplete(1);
                }
                S = new LinkedList<JHNode>(JHNode.uncovered);

                JHNode.out.println("\nCovering Nodes(" + S.size() + ")");
                for (JHNode n : S) {
                    n.log_complete(1, "!");
                }
                //if ((JNode.uncovered.isEmpty()) && (JNode.unfulfilled.isEmpty())){
                //We should remove these from the final version
                if (JHNode.isTableauComplete()) {
                    long time = java.util.Calendar.getInstance().getTimeInMillis();

		    // I have not been able to prove the the chain of eventualities does not get broken,
		    // So clear all eventualities and re-build before finishing.
                    for (JHNode n :  JHNode.col2node.values()) { if (n.b != null) { n.b.clear_eventualities(); } }
                    for (JHNode n :  JHNode.col2node.values()) { if (n.b != null) { n.b.update_eventualities(); } }

                    for (JHNode n : JHNode.col2node.values()) {
                        n.updateStatus();                        
                    }
                    //if ((!JNode.uncovered.isEmpty()) || (!JNode.unfulfilled.isEmpty())) {
                    if ((!JHNode.isTableauComplete())) {
                        long time2 = java.util.Calendar.getInstance().getTimeInMillis();
                        time_wasted += (time2 - time);
                        JHNode.out.println("Harmless bug: Lists of unfinished nodes incomplete, fixed with brute force (Wasted " + time_wasted + "ms)");
                        //One cause of this could be update_eventualities not calling updateStatus()
                    }
                }
                //}
            }

            if (!node.pruned) {
                JHNode.out.println("The formula is satisfiable.");
                JHNode.out.println("\nCovering Nodes ????  (" + JHNode.uncovered.size() + ")");
                node.updateStatus();
                JHNode.out.println("\nCovering Nodes ????  (" + JHNode.uncovered.size() + ")");
                if (!node.b.isCovered()) {
                    JHNode.out.println("BUT NOT COVERED?? The formula is satisfiable.");
                }
            } else {
                JHNode.out.println("The formula is unsatisfiable.");
            }
            JHNode.outputStatus(-1, "");
        
        } catch (Exception e) {
            JHNode.out.println();
            JHNode.out.flush();
            e.printStackTrace(JHNode.out);
            e.printStackTrace(System.out);
            JHNode.outputStatus(-1, "+");

            // System.out.println("Danger Will Robinson!" + e.toString());
        }

        JHNode.static_reset();

    }


    /* Check that no eventualities are missing */

    public static void redo_eventualities(JNode node) {

		long time_wasted=0;
                if (JNode.isTableauComplete()) {
                    long time = java.util.Calendar.getInstance().getTimeInMillis();

		    // I have not been able to prove the the chain of eventualities does not get broken,
		    // So clear all eventualities and re-build before finishing.
                    for (JNode n :  JNode.col2node.values()) { if (n.b != null) { n.b.clear_eventualities(); } }
                    for (JNode n : JNode.col2nodeX.values()) { if (n.b != null) { n.b.clear_eventualities(); } }
		    JNode.out.println("Post clear");
			node.log_graph();
		    JNode.out.println("");
                    for (JNode n :  JNode.col2node.values()) { if (n.b != null) { n.b.update_eventualities(); } }
                    for (JNode n : JNode.col2nodeX.values()) { if (n.b != null) { n.b.update_eventualities(); } }
		    JNode.out.println("Post update");
			node.log_graph();
		    JNode.out.println("");

                    for (JNode n : JNode.col2node.values()) {
                        n.updateStatus();                        
                    }
                    for (JNode n : JNode.col2nodeX.values()) {
                        n.updateStatus();
                    }
                    //if ((!JNode.uncovered.isEmpty()) || (!JNode.unfulfilled.isEmpty())) {
                    if ((!JNode.isTableauComplete())) {
                        long time2 = java.util.Calendar.getInstance().getTimeInMillis();
                        time_wasted += (time2 - time);
                        JNode.out.println("Harmless bug: Lists of unfinished nodes incomplete, fixed with brute force (Wasted " + time_wasted + "ms)");
                        //One cause of this could be update_eventualities not calling updateStatus()
                    }
                }
	}


    public static int go(String input) {
        Logic logic = new AUXLogic();
        boolean problem_occured = false;
        int ret_status=3;
        boolean satisfiable;
         JNode.out.println("\nDEBUG1");

        try {
            JNode.static_reset();
            FormulaTree ft = logic.disabbreviate(logic.parse(input));

            Subformulas sf = new Subformulas(ft);
/*            if (sf.right(0) == -1) {
            	throw new RuntimeException("Is the formula really really short???");
            }*/
            JHueEnum he = new JHueEnum(sf, ft);
            JHueEnum.e = he;
            //JHue h = new JHue(sf);
            //h.set(0);
            //JColour2 c = new JColour2(he, he.hue2Int(h), 1);
            JColour2 c = new JColour2(he, sf);
            c.normalise();
            JNode node = JNode.getNode(c);

            long time_wasted = 0;
         	JNode.out.println("\nDEBUG2");
            node.complete();
            redo_eventualities(node);
            while (!JNode.isTableauComplete()) {
                LinkedList<JNode> S = new LinkedList<JNode>(JNode.unfulfilled);
                JNode.out.println("\nFulfilling Nodes (" + S.size() + ")");
                for (JNode n : S) {
                    JNode.out.format(n.toString() + " ");

                }
                JNode.out.println();

                for (JNode n : S) {
                	n.col.check();
                    n.eComplete(1);
                }
                S = new LinkedList<JNode>(JNode.uncovered);

                JNode.out.println("\nCovering Nodes(" + S.size() + ")");
                for (JNode n : S) {
                	n.col.check();
                    n.log_complete(1, "!");
                }
                //if ((JNode.uncovered.isEmpty()) && (JNode.unfulfilled.isEmpty())){
                //We should remove these from the final version
                redo_eventualities(node);
                /*if (JNode.isTableauComplete()) {
                    long time = java.util.Calendar.getInstance().getTimeInMillis();

		    // I have not been able to prove the the chain of eventualities does not get broken,
		    // So clear all eventualities and re-build before finishing.
                    for (JNode n :  JNode.col2node.values()) { if (n.b != null) { n.b.clear_eventualities(); } }
                    for (JNode n : JNode.col2nodeX.values()) { if (n.b != null) { n.b.clear_eventualities(); } }
		    JNode.out.println("Post clear");
			node.log_graph();
		    JNode.out.println("");
                    for (JNode n :  JNode.col2node.values()) { if (n.b != null) { n.b.update_eventualities(); } }
                    for (JNode n : JNode.col2nodeX.values()) { if (n.b != null) { n.b.update_eventualities(); } }
		    JNode.out.println("Post update");
			node.log_graph();
		    JNode.out.println("");

                    for (JNode n : JNode.col2node.values()) {
                        n.updateStatus();                        
                    }
                    for (JNode n : JNode.col2nodeX.values()) {
                        n.updateStatus();
                    }
                    //if ((!JNode.uncovered.isEmpty()) || (!JNode.unfulfilled.isEmpty())) {
                    if ((!JNode.isTableauComplete())) {
                        long time2 = java.util.Calendar.getInstance().getTimeInMillis();
                        time_wasted += (time2 - time);
                        JNode.out.println("Harmless bug: Lists of unfinished nodes incomplete, fixed with brute force (Wasted " + time_wasted + "ms)");
                        //One cause of this could be update_eventualities not calling updateStatus()
                    }
                }

            	if (JNode.isTableauComplete()) {
			JNode.out.println("FINAL RUN (Just for output)");
			node.log_graph();
		}
*/
                //}
            }

            /*HashSet<JNode> test = new HashSet<JNode>(JNode.col2node.values());
            test.addAll(JNode.col2nodeX.values());
            JNode.out.format("AA %d=%d+%d\n",test.size(),JNode.col2node.size(),JNode.col2nodeX.size());
            JNode.out.format("AA %d=%d+%d\n",test.size(),new HashMap(JNode.col2node).size(),new HashMap(JNode.col2nodeX).size());
            
            JNode.out.format("AA %d=%d+%d\n",test.size(),new HashSet(JNode.col2node.keySet()).size(),new HashSet(JNode.col2nodeX.keySet()).size());
            JNode.out.format("AA %d=%d+%d\n",test.size(),new HashSet(JNode.col2node.values()).size(),new HashSet(JNode.col2nodeX.values()).size());
            
            if (!JNode.isTableauComplete()) {
                JNode.out.println("NOT COMPLETE???");
            } else {
                JNode.out.println("COMPLETE.");
            }

                                for (JNode n : JNode.col2node.values()) {
                        n.updateStatus();
                    }
                    for (JNode n : JNode.col2nodeX.values()) {
                        n.updateStatus();
                    }

            if (!JNode.isTableauComplete()) {
                JNode.out.println("NOT COMPLETE???");
            } else {
                JNode.out.println("COMPLETE.");
            }
*/
                    
            JNode.out.println("Checking Rule Nodes");
            for (JNode n : JNode.col2node.values()) {
                n.checkFinished();
                if (!n.type.equals("R")) {
                    JNode.out.println("!=R !!!!!!!!");
		    problem_occured=true;
                }
            }

            JNode.out.println("Checking Temporal Nodes");
            for (JNode n : JNode.col2nodeX.values()) {
                if (!n.type.equals("T")) {
                    JNode.out.println("!=T !!!!!!!!");
		    problem_occured=true;
                }
                n.checkFinished();
            }

            if (!JNode.isTableauComplete()) {
                JNode.out.println("NOT COMPLETE???");
		    problem_occured=true;
            } else {
                JNode.out.println("FINALE COMPLETE.");
		node.log_graph();
                JNode.out.println("COMPLETE.");
            }
            
            if (JNode.getNode(c).equals(JNode.getNodeX(c,null))) {
		    problem_occured=true;
                JNode.out.println("Types differ, but they equal?");
            }

	    JNode.out.format("---SUMMARY---\n");
	    //JNode.outputStatus(0,"");
            //if (node.complete()) {
            if (!node.pruned) {
        	satisfiable = true;
                JNode.out.println("The formula is satisfiable.");
                JNode.out.println("\nCovering Nodes ????  (" + JNode.uncovered.size() + ")");
                node.updateStatus();
                JNode.out.println("\nCovering Nodes ????  (" + JNode.uncovered.size() + ")");
                if (!node.b.isCovered()) {
		    problem_occured=true;
                    JNode.out.println("BUT NOT COVERED?? The formula is satisfiable.");
                }
            } else {
                satisfiable = false;
                JNode.out.println("The formula is unsatisfiable.");
            }
            //System.out.format("%d colours, %d hues, Max# hues in colour: %d\n", 
            //        JNode.col2node.size(), JHueEnum.e.int2hue.size(), JColour2.max_num_hues_in_colour);
            JNode.outputStatus(-1, "");
        	if (problem_occured) {
        		return 2;
        	} else {
        		if (satisfiable) {
        			return (1);
        		} else {
        			return (0);
        		}
        	} 
 //       } catch (InterruptedException e) {
 //       	return 3; //Timeout
//          Thread.sleep(400);
        }         catch (Exception e) {
        	if (Timeout.wasInterrupted(e)) {
        		return 3; // Timeout.
        	}
            JNode.out.println();
            JNode.out.flush();
            e.printStackTrace(JNode.out);
            e.printStackTrace(System.out);
            JNode.outputStatus(-1, "+");

	        problem_occured=true;

            // System.out.println("Danger Will Robinson!" + e.toString());
	        return (2);
        }

        //JNode.static_reset();

		

    }

    public static void main(String[] args) {
    	
    	///JRunTab.TestBCTL(2, 9);
try {
	
	
	
    boolean use_marks_tab = false;
    boolean ltl = false;

        /*  for (int    i=0;i<9;i++) {
        System.out.format("%d %d\n", i, new java.math.BigInteger(Integer.toString(i)).bitCount());
        }
        System.exit(0);*/
        //FormulaTree ft = logic.parse("(A(Xp)&A(Xp>Xq))&-A(Xq)");
        //FormulaTree ft = logic.parse("A(Xp&Xq)&-A(Xq)");
        //FormulaTree ft = logic.parse("((-A(p&q)&p)&q)");
//				FormulaTree ft = logic.parse("(((pUq)&-p)&-q)");
        //FormulaTree ft = logic.parse("((((pUq)&-Xp)&-Xq)&-q)");
        //Ban double negations --? ;
        //FormulaTree ft = logic.parse("((pUq)&-((p&p)Uq))");
        String input;

        for (String s : args) {
            if (s.equals("--BCTL1")) {
                use_marks_tab = true;
            }
            if (s.equals("--LTL")) {
                ltl = true;
            }
            if (s.equals("--nolog")) {
                JNode.log = false;
            }
            if (s.equals("--nohue")) {
                JNode.use_hue_tableau = false;
            }
            if (s.equals("--hue")) {
                JNode.use_hue_tableau = true;
            }

            if (s.equals("--log")) {
                JNode.log = true;
            }
            //if (s.startsWith("--timeout-ms=")) {
            if (s.startsWith("-t")) {
            	String t=s.substring(2);
            	System.out.println("Setting Abort in '"+t+"' milliseconds");
            	Long L=Long.decode(t);
            	long l=L;
            	Timeout.delayedAbort(l);
            }
        }

        if (args.length == 0) {
            System.out.format("%d+\n", 9);
            System.out.println("Formula not specified");
            JNode.log = true;
/*            input = "(Xp&XXXXXXX-q)";
            input = "-(AFGFGp<AFAGAFAGp)";
            input = "AG(p>EXp)&p&EF-p";*/
            input = default_input;
            System.out.println("Using '" + input + "' as input");

            //return;
        } else {
            input = args[0];
        }


        long time = java.util.Calendar.getInstance().getTimeInMillis();
        if (use_marks_tab) {
            Parameters.BFSon = false;
            BctlTab junk = new BctlTab(input, new ReportToScreen());
        } else {
            if (JNode.log) {
                System.out.println("To output proof, run with --log (disabled by default as it is currently very time expensive).");
            }
            //go(input);
            if (ltl) {
            	go_LTL(input);
            } else {
            	go(input);
            }
            JNode.out.flush();
            if (JNode.log) {
                System.out.println("Note: proof output is currently very time consuming, try running with --nolog.");
            }
        }
        long time2 = java.util.Calendar.getInstance().getTimeInMillis();
        System.out.format("milliseconds: %d\n", time2 - time);

    /* TODO: Quicktab
     *    time = java.util.Calendar.getInstance().getTimeInMillis();
        quicktab.QuickTab.go(input);
        time2 = java.util.Calendar.getInstance().getTimeInMillis();
        System.out.format("milliseconds: %d\n", time2 - time);
        */


        //FormulaTree ft = logic.parse("X(p&-p)"); 

        //         System.out.println("Parsed!");


//        System.out.format("hue2Int(h)=%d\n", he.hue2Int(h));

        //JColour c = new JColour(sf);

        //c.addHue(h);

        /*            System.out.println(h.toString());
        h.set(0);
        //System.out.println(h.toString());
        System.out.println(h.toString());
        for (int i = h.nextSetBit(0); i != -1; i = h.nextSetBit(i + 1)) {
        System.out.println("h has: " + ft.getSubformulas()[i].toString());
        }
        int n = 1; //numeric hue
        for (int f = 0; f < sf.count(); f++) {
        n = he.addFormula2Hue(f, n);
        System.out.format("%s: %d\n", ft.getSubformulas()[f].toString(), f);
        }
        System.out.println(ft.getSubformulas()[0].toString());
        System.out.println("h.contradiction: " + toString(h.hasContradiction()));
        //System.out.println("c.contradiction: "+toString(c.hasContradiction()));
         */
        /*       if (JNode.isWorkRequired_ALL()) {
        System.out.println("WORK REQUIRED STILL!!!");
        }*/

//                                System.out.println("args0"+args[0]);



        //System.out.println("SingleX Hue Interval Repeat Check is off. To switch on enter any character.");
} catch (Exception e) {
	e.printStackTrace(System.out);
}
    }
}
