package formulas;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
/**
 *
 * @author john
 */

public class JRunTab implements Runnable {
    String fs;
    Report r;
    boolean log;
    public enum Solver { BCTLOLD, BCTLNEW, CTL, RANDOM_TEST, BPATH, BPATHUE, BCTLHUE }
    Solver type;
    public int status=3;
    public boolean quit_on_problem=false;
    //public static final int BCTLOLD=0;
    //public static final int BCTLNEW=1;
    //public static final int CTL=2;
    //public static final int RANDOM_TEST=3;
    long milliseconds;
    
    private static PrintStream out=System.out;
    
    //int type_BCTLOLD=0;
    
    private static int addResult(int r, int s) {
    	if (s == 3) {
    		//Timeout 
    		return r;
    	}
    	
    	if (r == 3) {
    		return s;
    	}
    	
    	if (r == s) {
    		return r;
    	}
    	
    	if ( (r == 0 && s == 1) || (s == 0 && r == 1) || (r == 4) ) {
    		return 4; // mismatch
    	}
    	/*
    	if ( s == 0 || s == 1 ) {
    		if (r == 3) {
    			return r;
    		}
    		
    	}*/
    		
    	if (s == 2) {
    		return 2;
    	}
    	
    	return 8;
    	
    }
    
    public static void TestBCTL(int v, int l, PrintWriter out) {
    	JRandomFormula r = new JRandomFormula();
    	int status=0;
    	while (status<4) {
    		TestBCTL(r.nextFormula(v, l), out);
    	}		
    }
    
    
    public static int TestBCTL(String f, PrintWriter out) {
    	JRunTab o = new JRunTab(f,Solver.BCTLOLD);
    	JRunTab n = new JRunTab(f,Solver.BCTLNEW);
    	JRunTab h = new JRunTab(f,Solver.BCTLNEW);
    	int status;

       	System.out.println("!!!!!!!"+f);
       	System.out.flush();
    	
        Timeout.timeout_run(o);
       	//o.status=3;

       	System.out.println("DONE OLD: "+f);
       	JNode.use_hue_tableau=false;
    	Timeout.timeout_run(n);
    	
       	System.out.println("DONE NEW: "+f);


       	JNode.use_hue_tableau=true;
    	Timeout.timeout_run(h);
       	System.out.println("DONE HUE: "+f);
       	
       	status=addResult(o.status,n.status);
       	status=addResult(status,h.status);

    	out.format("%d:%d%d%d %04d,%04d,%04d %s\n",
    			status,
    			o.status,
    			n.status,
    			h.status,
    			o.milliseconds,
    			n.milliseconds,
    			h.milliseconds,
    			f);
    	
    	if (n.status == 2) {
    		out.println("PROBLEM: "+f);
    		//throw new RuntimeException(); //Hack!
    	}
    	
    	out.flush();
    	
    	return status;
    }
    
    public JRunTab(String fs, Report r, Solver type){
	    this(fs,r,type,false);
    }
    
    public JRunTab(String fs, Report r, Solver type, boolean log){
        this.fs = fs;
        this.r = r;
        this.type = type;
	this.log = log;
        
    }
    

    public JRunTab(String fs, Solver type){
        this(fs,new ReportToFile(new NullOutputStream()), type);   
    }
    
    private void clear_jsettings () {
	    // Some of this stuff should probably be integrated with the formulas/ package
                JNode.use_hue_tableau = false;
		JColour2.state_variables = true;
    }

	public void run() {
        //new BctlTab(fs,r);
        PrintWriter out = ((ReportToFile)r).pw;
        formulas.JNode.out=out;
        status=3;
	clear_jsettings();
	JNode.log = log;
        long time = java.util.Calendar.getInstance().getTimeInMillis();
        try {
        switch (type) {
            case BPATHUE: JColour2.state_variables = false;
            case BCTLHUE: JNode.use_hue_tableau = true;
            case BCTLNEW: status=formulas.JBMain.go(fs); break;
            case BPATH: JColour2.state_variables = false;
            	        status=formulas.JBMain.go(fs); break;
            //case BCTLOLD: new BctlTab(fs,r); break;
            //case BCTLNEW: status = formulas.JBMain.go(fs); break;
            case BCTLOLD: status = (new BctlTab(fs,r)).status; break;
            case CTL:    
                out.println("The CTL* Button in this applet may not work in your"
                        + "browser. If so, try: "
                        + "http://www.csse.uwa.edu.au/~mark/research/Online/startab/Stapplet.html");
                new StarTab(fs,r).go(); break;
            case RANDOM_TEST:
            	out.println("Beginning Randomised Self Test." + '\n' +
            			"Format: ConsensusVote:Result Times Formula" + '\n' +
            			"Results: 0:Unsatisfiable, 1:Satisfiable, 2: Exception, 3:Timeout, 4:Decision Procedures Disagree!" +
            			'\n'+
            			"In the unlikely event you find a formula where the decision producedures disagree, report it to: " +
            			'\n'+"john.mccabe-dansted@uwa.edu.au" + '\n'); 
            	TestBCTL(2,fs.length(),out); break;
            default: throw (new RuntimeException("Unknown Tableau Type"));

        }
 //       } catch (InterruptedException e) {
//        	status = 3; //Timeout
//                Thread.sleep(400);
        } catch (Exception e) {
        	if (Timeout.wasInterrupted() || e.getMessage() == "InterruptedException" ) {
        		status = 3; 
        	} else {
        		System.out.println("Not interrupted.");
        		e.printStackTrace(out);
        	}
        }
        out.flush();

        long time2 = java.util.Calendar.getInstance().getTimeInMillis();
        milliseconds=time2 - time;
        out.format("milliseconds: %d\n", milliseconds);
        
        out.flush();
    }
}

/**Writes to nowhere*/
class NullOutputStream extends OutputStream {
  @Override
  public void write(int b) throws IOException {
  }
}
