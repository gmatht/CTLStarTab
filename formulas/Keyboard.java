/*
 * Keyboard.java
 *
 * Created on 16 May 2005, 14:46
 */

package formulas;

import java.io.*;

/**
 *
 * @author  mark
 */
public class Keyboard {
    
    /** Creates a new instance of Keyboard */
    public Keyboard() {
    }
    
    public static String getInput(){
        try{ 
         BufferedReader br
                = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
        } catch (IOException e){
            return "";
        }
    }
}
