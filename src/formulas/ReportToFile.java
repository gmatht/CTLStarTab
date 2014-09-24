/*
 * ReportToFile.java
 *
 * Created on 20 June 2005, 16:02
 */

package formulas;

import java.io.*;

/**
 *
 * @author  mark
 */
public class ReportToFile implements Report {
    
    private static int thresh=2;
    public PrintWriter pw;
    private boolean tosca=true;
    
    /** Creates a new instance of ReportToFile */
    public ReportToFile(String fname) {
        try {
            pw=new PrintWriter(new FileOutputStream(fname));
            System.out.println("Output file opened OK.");
            pw.print("Start of File");
        } catch(IOException e){
            System.exit(1);
        }
        
    }

    /** Creates a new instance of ReportToFile */
    public ReportToFile(OutputStream os) {
        pw=new PrintWriter(os);
    }
    
    private String indent(int l){
        String s="";
        for (int i=0; i<l; i++) s=s+"   ";
        return s;
    }
    
    public void eol(String s) {
        pw.println(s);
        if (tosca) System.out.println(s);
    }
    
    public void mol(String s) {
        pw.print(s);
        if (tosca) System.out.print(s);
    }
    
    public void sol(int level, String s){
        tosca=(level<=thresh);
        pw.print(indent(level)+s);
        if (tosca) System.out.print(indent(level)+s);
    }
    
    public void seol(int level, String s){
        tosca=(level<=thresh);
        pw.println(indent(level)+s);
    	Timeout.yield();    
        if (tosca) System.out.println(indent(level)+s);
    }
    
    public void heading(int level, String s){
        tosca=(level<=thresh);
        pw.println(indent(level)+s);
        if (tosca) System.out.println(indent(level)+s);
    }
    
    public void close() {
        pw.close();
    }

	public void nextStep() {
		// TODO Auto-generated method stub
		
	}
    
}
