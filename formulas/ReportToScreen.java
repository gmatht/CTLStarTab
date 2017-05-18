/*
 * ReportToScreen.java
 *
 * Created on 20 June 2005, 10:59
 */

package formulas;

/**
 *
 * @author  mark
 */
public class ReportToScreen implements Report {
    
    /** Creates a new instance of ReportToScreen */
    public ReportToScreen() {
    }
    
    private String indent(int l){
        String s="";
        for (int i=0; i<l; i++) s=s+"   ";
        return s;
    }
    
    public void eol(String s) {
        System.out.println(s);
    }
    
    public void mol(String s) {
    	
        System.out.print(s);
    }
    
    public void sol(int level, String s){
    	if (level<3)
        System.out.print(indent(level)+s);
    }
    
    public void seol(int level, String s){
    	if (level<3)
    		System.out.println(indent(level)+s);
    }
    
    public void heading(int level, String s){
        System.out.println(indent(level)+s);
    }
    
    public void close() {
    }
    
}
