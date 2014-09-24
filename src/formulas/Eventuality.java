/*
 * Eventuality.java
 *
 * Created on 20 June 2005, 14:12
 */

package formulas;

/**
 *
 * @author  mark
 */
public class Eventuality {
    public int colour;
    public int hue;
    public int sbf;
    public boolean cured=false;
    public int[] succ;
    
    /** Creates a new instance of Eventuality */
    public Eventuality(int colour, int hue, int sbf) {
        this.colour=colour;
        this.hue=hue;
        this.sbf=sbf;
    }
    
    
}
