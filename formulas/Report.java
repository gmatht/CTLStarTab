/*
 * Report.java
 *
 * Created on 19 June 2005, 17:18
 */

package formulas;

/**
 *
 * @author  mark
 */
interface Report {
   
   void heading(int level, String s); //whole line and warns of some higher level stuff coming
   
   void sol(int level, String s); //starts a new line at level
   void seol(int level, String s); //whole line at level
   void mol(String s); //middle of current line
   void eol(String s); //ends current line
    
   void close();//end of output
}
