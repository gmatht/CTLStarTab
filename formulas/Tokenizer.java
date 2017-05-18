/*
 * Tokenizer.java
 *
 * Created on 15 May 2005, 12:58
 */

package formulas;

/**
 *
 * @author  mark
 */
public class Tokenizer {
        
    String input;
    int index=0;
    
    
    public static Tokenizer getTokenizer(String s ){
             return new Tokenizer(s);
    }

    
    /** Creates a new instance of FXTokenizer */
    public Tokenizer(String s) {
        input=s;
        index=0;
    }
      
    public boolean isFinished(){
        return (index>=input.length());
    }
    
    public Symbol next(){
        index++;
        return new Symbol(input.charAt(index-1));
    }
    
    
    
}
