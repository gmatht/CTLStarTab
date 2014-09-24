/**
 * 
 */
package formulas;

import java.io.PrintWriter;
import java.util.*;
//import java.lang.*;

/**
 * @author John
 *
 */
public class JRandomFormula {

	private Random r_;
	final int max_variables=26;

	public JRandomFormula() {
		r_ = new Random(111); //HACK
	}
	
	

/**
 * Return the next randomly generated formula
 *
 * @param v The maximum number of variables
 * @param l The length of the formula
 */


	public String nextFormula(int v, int l) {
		if (v > max_variables) {
			throw new RuntimeException("Too many variables!");
		}
		if (l < 1) {
			throw new RuntimeException("Formula too short!");
		}

		switch (l) {
			case 1: return (""+nextAtomicFormula(v));
			case 2: return nextUnaryFormula(v,l); 
			default: if (r_.nextBoolean()) {return nextUnaryFormula(v,l);} else {return nextBinaryFormula(v,l);}
		}

	}

	private char nextAtomicFormula (int v) {
		return (char)((int)'a'+(int)r_.nextInt(v));
	}

	private char nextOperator(String s) {
		int i = r_.nextInt(s.length());
		return s.charAt(i);
	}

	private String nextUnaryFormula (int v, int l) {
		return nextOperator("-----AAEEXXGF")+nextFormula(v,l-1);
	}

	private String nextBinaryFormula (int v, int l) {
		int T=l-1; //Total length of left and right subformulae.
		int L=r_.nextInt(T-1)+1;
		int R=T-L;
		return '('+nextFormula(v,L)+nextOperator("&U")+nextFormula(v,R)+')';
	}

	public static void main(String[] args) {
		// Test harness
		
        Logic logic = new AUXLogic();
        try {
        FormulaTree ft = logic.disabbreviate(logic.parse("(-aU-a)"));
        Subformulas sf = new Subformulas(ft);
           if (sf.right(0) == -1) {
        	   System.out.println("Is the formula really really short?");
        	throw new RuntimeException("Is the formula really really short???");
           }
        } catch (Exception e) {
        }
		
		JNode.use_optional_hues=false;
		JNode.use_hue_tableau=false;
		JRunTab.TestBCTL(2,14, new PrintWriter(System.out));
		/*while (true) {
			System.out.println(new JRandomFormula().nextFormula(26,49));
		}*/
	}
}
