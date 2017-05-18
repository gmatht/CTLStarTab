/*
 * Symbol.java
 *
 * Created on 15 May 2005, 12:41
 */

package formulas;

/**
 *
 * @author  mark
 */
public class Symbol {
    char c;
    
    /** Creates a new instance of Symbol */
    public Symbol(char c) {
        this.c=c;
    }
    
    public boolean isOpenParenthesis(){
        return c=='(';
    }
 
    public boolean isCloseParenthesis(){
        return c==')';
    }
    
   public boolean isZeroary(){
        return FormulaTree.getLogic().isZeroary(c);
    }
   
   public boolean isUnary(){
        return FormulaTree.getLogic().isUnary(c);
    }
   
   public boolean isBinaryInfix(){
        return FormulaTree.getLogic().isBinaryInfix(c);
    }
   
   public boolean isEmpty(){
       return c=='*';
   }
   
   public boolean isNegation(){
       return c=='-';
   }
   
   public String toString(){
       return ""+c;
   }
    
   public boolean equals(Symbol other){
       return (c==other.c);
   }
   
   public char toChar(){
       return c;
   }
        
}
