/*
 * Logic.java
 *
 * Created on 24 May 2005, 17:51
 */

package formulas;

/**
 *
 * @author  mark
 */
public interface Logic {
    
    public boolean isZeroary(char c);
    public boolean isUnary(char c);
    public boolean isBinaryInfix(char c);
    
    public FormulaTree parse(String s) throws ParseException;
   
    public FormulaTree disabbreviate(FormulaTree f);
    boolean isMTC(ClosureSubset2 m);
    boolean precs(ClosureSubset2 m1,ClosureSubset2 m2);
    boolean isEventuality(FormulaTree s);
    boolean unfullfilled(ClosureSubset2 mtcs,int sbfnum);
    
    public FormulaTree abbreviate(FormulaTree f);
    
    
}
