/*
 * AUXLogic.java
 *
 * Created on 26 May 2005, 18:46
 */

package formulas;
/**
 *
 * @author  mark
 *  Some modifications by John to and B Y I operators
 */
public class AUXLogic implements Logic {
    
    /** Creates a new instance of AUXLogic */
    public AUXLogic() {
    }
    
       public FormulaTree parse(String s) throws ParseException{
        FormulaTree.setLogic(this);
        return FormulaTree.parse(s);
    }
    
    public FormulaTree disabbreviate(FormulaTree f) {
	char c = f.topChar();
        if ((c>='a') && (c<='z')) return f;
        
        switch (c){
            case '0':
                return new FormulaTree('-',
                           new FormulaTree('1'));
            case '1':
                return f;
            case '-':
                return new FormulaTree('-',disabbreviate(f.leftSubtree()));
            case '&':
                return new FormulaTree('&',
                    disabbreviate(f.leftSubtree()),
                    disabbreviate(f.rightSubtree()));
            case '|':
                return new FormulaTree('-',
                        new FormulaTree('&',
                            new FormulaTree('-',disabbreviate(f.leftSubtree())),
                            new FormulaTree('-',disabbreviate(f.rightSubtree()))));
            case '>':
                return new FormulaTree('-',
                        new FormulaTree('&',
                            disabbreviate(f.leftSubtree()),
                            new FormulaTree('-',disabbreviate(f.rightSubtree()))));
            case '<':
                return new FormulaTree('-',
                        new FormulaTree('&',
                            disabbreviate(f.rightSubtree()),
                            new FormulaTree('-',disabbreviate(f.leftSubtree()))));
            case '=':
                 return new FormulaTree('&',
                        new FormulaTree('-',
                            new FormulaTree('&',
                                disabbreviate(f.leftSubtree()),
                                new FormulaTree('-',disabbreviate(f.rightSubtree())))),
                        new FormulaTree('-',
                         new FormulaTree('&',
                                disabbreviate(f.rightSubtree()),
                                new FormulaTree('-',disabbreviate(f.leftSubtree())))));
                 
            case 'F':
                return new FormulaTree('U',new FormulaTree('1'),disabbreviate(f.leftSubtree())); 
            case 'G':
                return new FormulaTree('-',
                        new FormulaTree('U',new FormulaTree('1'),
                            new FormulaTree('-',
                                    disabbreviate(f.leftSubtree()))));
            case 'X':
                return new FormulaTree('X',disabbreviate(f.leftSubtree()));
            case 'U': case 'Y': case 'I':
                return new FormulaTree(c,disabbreviate(f.leftSubtree()),disabbreviate(f.rightSubtree())); 
            case 'A': case 'B':
                return new FormulaTree(c,disabbreviate(f.leftSubtree()));
            case 'E':
                return new FormulaTree('-',
                        new FormulaTree('A',
                            new FormulaTree('-',
                                    disabbreviate(f.leftSubtree()))));
                
            default:
                throw new RuntimeException("unknown sysmbol in disabbrevaite");
        }//end switch
    }//end disabbreviate
 
    
    public boolean isEventuality(FormulaTree s) {
        return (s.topChar()=='U');
    }
    
    public boolean isMTC(ClosureSubset2 m) {
        Subformulas sf=m.getSf();
        for (int i=0; i<sf.count(); i++){
            if ((m.member(i)) && (m.member(sf.negn(i)))) return false;
            if ((!m.member(i)) && (!m.member(sf.negn(i)))) return false;
            if (m.member(i) && (sf.topChar(i)=='-') && (sf.topChar(sf.left(i))=='1')) return false;
        }
        for (int i=0; i<sf.count(); i++){
            if (sf.topChar(i) =='&'){
                if ((m.member(i)) && !((m.member(sf.left(i))) && (m.member(sf.right(i))))) return false;
                if (!(m.member(i)) && ((m.member(sf.left(i))) && (m.member(sf.right(i))))) return false;
            }
            
            //pUq -> p or q
            if (sf.topChar(i) =='U'){
                if ((m.member(i)) && !(m.member(sf.left(i)))
                    && !(m.member(sf.right(i)))) return false;
            }
            
            //-(pUq) -> -q
            if ((sf.topChar(i)=='-') && (sf.topChar(sf.left(i)) =='U')){
                if ((m.member(i)) && (m.member(sf.right(sf.left(i))))) return false;
            }
            
            // A \alpha  -> \alpha
            if (sf.topChar(i)=='A'){
                if ((m.member(i)) && !(m.member(sf.left(i)))) return false;
            }
            
            // p -> Ap for an atom p or -p
            //leave for equiv reln
            
        }
        return true;
    }
    
    public boolean isAtom(char c){
       if (('a'<=c) && (c<='z')) return true;
       return false;    
    }
   
    
   public boolean isZeroary(char c){
       if ((c=='0') || (c=='1')) return true;
       if (('a'<=c) && (c<='z')) return true;
       return false;    
    }
   
   public boolean isUnary(char c){
       if ((c=='-') || 
       (c=='F') || (c=='P') || (c=='G') || (c=='H') || (c=='X') || (c=='B') ||
       (c=='E') || (c=='A')) return true;
        return false;
    }
   
   public boolean isBinaryInfix(char c){
       return ("&|><=UYI".indexOf(c)!=-1);
    }
    
   //assumes both from same closure
    public boolean precs(ClosureSubset2 m1, ClosureSubset2 m2) {
         Subformulas sf=m1.getSf();
        
         for (int i=0; i<sf.count(); i++){  
            // pUq and -q in this => pUq in other
            if (sf.topChar(i) =='U'){
                if ((m1.member(i)) && (m1.member(sf.negn(sf.right(i))))
                  && !(m2.member(i))) return false;
            }
            
            // -(pUq) and p in this => -(pUq) in other
            if ((sf.topChar(i)=='-') && (sf.topChar(sf.left(i))=='U')){
                if ((m1.member(i)) && (m1.member(sf.left(sf.left(i)))) && !(m2.member(i))) return false;
            }
            
            //X\alpha in this -> \alpha in other
            if (sf.topChar(i)=='X'){
                if ((m1.member(i)) && !(m2.member(sf.left(i)))) return false;
            }
            
            //-X\alpha in this -> -\alpha in other
            if ((sf.topChar(i)=='-') && (sf.topChar(sf.left(i))=='X')){
                if ((m1.member(i)) && (m2.member(sf.left(sf.left(i))))) return false;
            }
            
        }
        return true;
    }//end precs
    
    //assumes both from same closure
    public boolean equiv(ClosureSubset2 m1, ClosureSubset2 m2) {
         Subformulas sf=m1.getSf();
        
         for (int i=0; i<sf.count(); i++){  
            // Ap in this => Ap and p in other and vv
            if (sf.topChar(i) =='A'){
                if ((m1.member(i)) && !(m2.member(sf.left(i)))) return false;
                if ((m1.member(i)) && !(m2.member(i))) return false;
                if ((m2.member(i)) && !(m1.member(sf.left(i)))) return false;
                if ((m2.member(i)) && !(m1.member(i))) return false;
            }
            
            //atom p in this => p in other 
            if (isAtom(sf.topChar(i))){
                if ((m1.member(i)) && !(m2.member(i))) return false;
                if ((m2.member(i)) && !(m1.member(i))) return false;
            }
        }//end for i
        return true;
    }//end equiv
    
    //sunformula number sbfnum (which is assumed to be
    //an eventuality ie pUq) is unfullfilled in mtcs
    //ie pUq and -q in mtcs
    public boolean unfullfilled(ClosureSubset2 mtcs, int sbfnum) {
                    Subformulas sf=mtcs.getSf();
                   if (mtcs.member(sbfnum)){
                        int a=sf.negn(sf.right(sbfnum));
                        
                        if (mtcs.member(a))
                            return true;
                        else
                            return false;
                    }
                    else return false;
    }//end unfullfilled
    
    public FormulaTree abbreviate(FormulaTree f) {
	char c=f.topChar();
        if ((c>='a') && (c<='z')) return f;
        
        switch (f.topChar()){

            case '1':
                return f;
            case '-': {
            	FormulaTree g=f.leftSubtree();
            	if ((g.topChar()>='a') && (g.topChar()<='z')) return f;
            	
            	switch (g.topChar()){
            		case '1': return new FormulaTree('0');
            		case '-': return new FormulaTree('-',abbreviate(g)); // why not just abbreviate(g)?
            		case 'Y': case 'I': return new FormulaTree('-',abbreviate(g));
            		case '&': {
            			FormulaTree h1=g.leftSubtree();
            			FormulaTree h2=g.rightSubtree();
            			if ((h1.topChar()=='-') && (h2.topChar()=='-')) {
            				return new FormulaTree('|',abbreviate(h1.leftSubtree()),abbreviate(h2.leftSubtree()));
            			} else if ((h1.topChar()!='-') && (h2.topChar()=='-')){
            				return new FormulaTree('>',abbreviate(h1),abbreviate(h2.leftSubtree()));
            			}
            			return new FormulaTree('-',abbreviate(f.leftSubtree()));
	            	}
            		case 'X': case 'B': return new FormulaTree(c,abbreviate(g));
            		case 'U': {
            			if ((g.leftSubtree().topChar()=='1') && (g.rightSubtree().topChar()=='-')){
            				return new FormulaTree('G',abbreviate(g.rightSubtree().leftSubtree()));
            			}
            			return new FormulaTree('-',abbreviate(f.leftSubtree()));
            		}
            		case 'A':{
            			if ((g.leftSubtree().topChar()=='-')) return new FormulaTree('E',abbreviate(g.leftSubtree().leftSubtree()));
            			return new FormulaTree('-',abbreviate(f.leftSubtree()));
            		}
            	}
                return new FormulaTree('-',abbreviate(f.leftSubtree()));
            }
            case '&':
            	//still to do =
            	return new FormulaTree('&',abbreviate(f.leftSubtree()),abbreviate(f.rightSubtree()));


            case 'X': case 'B':
            	return new FormulaTree(c,abbreviate(f.leftSubtree()));
            case 'Y': case 'I':
            	return new FormulaTree(c,abbreviate(f.leftSubtree()),abbreviate(f.rightSubtree()));
            case 'U':{
            	FormulaTree g=f.leftSubtree();
            	if ((g.topChar()>='1')) return new FormulaTree('F',abbreviate(f.rightSubtree()));	
            	}
            	return new FormulaTree('U',abbreviate(f.leftSubtree()),abbreviate(f.rightSubtree()));
            case 'A':
                return new FormulaTree('A',abbreviate(f.leftSubtree()));

                
            default:
                throw new RuntimeException("unknown sysmbol in abbreviate"+f.topChar());
        }//end switch
    }//end abbreviate
   
}
