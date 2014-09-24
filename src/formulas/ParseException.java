/*
 * ParseException.java
 *
 * Created on 15 May 2005, 17:29
 */

package formulas;

/**
 *
 * @author  mark
 */
public class ParseException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>ParseException</code> without detail message.
     */
    public ParseException() {
    }
    
    
    /**
     * Constructs an instance of <code>ParseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ParseException(String msg) {
        super(msg);
    }
}
