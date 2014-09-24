/*
 * FormulaTree.java
 *
 * Created on 15 May 2005, 12:49
 */

package formulas;

/**
 * Used to represent a Formula as a tree of nodes.
 *
 * @author  mark
 */
public class FormulaTree {
    
    private static Logic logic;
    private TreeNode root;
    
    public FormulaTree(){
        root=new TreeNode();
    }
    
    public static void setLogic(Logic l){
        logic=l;
    }
    
    public static Logic getLogic(){
        return logic;
    }
    
    /** Creates a new FormulaTree from a string*/
    public static FormulaTree parse(String s) throws ParseException {
        Tokenizer tokenizer=Tokenizer.getTokenizer(s);
        FormulaTree f=new FormulaTree();
            while (!tokenizer.isFinished()){
                Symbol sym=tokenizer.next();
                
                //System.out.println("DEBUG: sym="+sym);
                
                f=f.add(sym);
                //System.out.println("DEBUG: f="+f);
            }
            if (!f.isUnpending()) throw new ParseException("incomplete formula string");
            return f;   
    }//end parse
    
    private FormulaTree add(Symbol s) throws ParseException{
        if (root.isReal()){
            if (s.isOpenParenthesis()) 
                throw new ParseException("bad open parenthesis");
            if (s.isCloseParenthesis())
                throw new ParseException("bad close parenthesis 1");
            if (s.isZeroary())
                throw new ParseException("bad zero-ary");
            if (s.isUnary())
                throw new ParseException("bad unary");
            if (s.isBinaryInfix())
                return TreeBuildUnary(s,false,this);
            throw new ParseException("unimplemented symbol 1: `" + s +"'");
        }
        else { //root is imaginary
            if (s.isOpenParenthesis()) 
                return TreeBuildBinary(s,false,this,new FormulaTree());
            if (s.isCloseParenthesis()){
                if (root.getContent().isOpenParenthesis())
                    return reverse(leftSubtree(),rightSubtree());
                throw new ParseException("bad close parenthesis 2");
            }
            if (s.isZeroary()){
                if (root.getContent().isEmpty())
                    return TreeBuildZeroary(s,true);
                if (root.getContent().isOpenParenthesis()){
                    FormulaTree r2=rightSubtree().add(s);
                    return TreeBuildBinary(new Symbol('('),false,leftSubtree(),r2);
                }
                if ((root.isBinary()) || (root.isUnary()))
                    return reverse(this,TreeBuildZeroary(s,true));
                throw new ParseException("bad zero-ary");
            }
            if (s.isUnary()){
                if (root.getContent().isEmpty())
                    return TreeBuildUnary(s,false,this);
                if (root.getContent().isOpenParenthesis()){
                    FormulaTree r2=rightSubtree().add(s);
                    return TreeBuildBinary(new Symbol('('),false,leftSubtree(),r2);
                }
                if ((root.isBinary()) || (root.isUnary()))
                    return TreeBuildUnary(s,false,this);
                throw new ParseException("bad unary");
            }
            if (s.isBinaryInfix()){
                if (root.getContent().isOpenParenthesis()){
                       FormulaTree r2=rightSubtree().add(s);
                       return TreeBuildBinary(new Symbol('('),false,leftSubtree(),r2);
                }
                throw new ParseException("bad binary infix");
            }
            throw new ParseException("unimplemented symbol 2: `" + s + "'");
        }  
    }//end add
    
    private static FormulaTree reverse(FormulaTree t, FormulaTree u) throws ParseException {
        if (t.root.isReal()) throw new ParseException("syntax error in reverse");
        if (t.root.getContent().isEmpty()) return u;
        if (t.root.getContent().isOpenParenthesis()) {
            FormulaTree v=reverse(t.rightSubtree(),u);
            FormulaTree w=TreeBuildBinary(new Symbol('('),false,t.leftSubtree(),v);
            return w;
        }
        if (t.root.getContent().isBinaryInfix()){
            return TreeBuildBinary(t.root.getContent(),true,t.leftSubtree(),u);
        }
        if (t.root.getContent().isUnary()){
            FormulaTree v=TreeBuildUnary(t.root.getContent(),true,u);
            return reverse(t.leftSubtree(),v);
        }
        return null;
    }//end reverse
    
    private boolean isUnpending(){
        return root.isReal();
    }
    
      private static FormulaTree TreeBuildUnary(Symbol content,boolean reality,FormulaTree sub){
        FormulaTree f=new FormulaTree();
        f.root=new TreeNode(1,content,reality);
        f.root.setChild(0,sub.root);
        return f;
    }
    
    private static FormulaTree TreeBuildZeroary(Symbol content,boolean reality){
        FormulaTree f=new FormulaTree();
        f.root=new TreeNode(0,content,reality);
        return f;
    }
    
     private static FormulaTree TreeBuildBinary(Symbol content,boolean reality,FormulaTree left,FormulaTree right){
        FormulaTree f=new FormulaTree();
        f.root=new TreeNode(2,content,reality);
        f.root.setChild(0,left.root);
        f.root.setChild(1,right.root);
        return f;
    }
     
     public FormulaTree leftSubtree(){
         FormulaTree f=new FormulaTree();
         f.root=this.root.getChildNode(0);
         return f;
     }
     
     public FormulaTree rightSubtree(){
         FormulaTree f=new FormulaTree();
         f.root=this.root.getChildNode(1);
         return f;
     }
     
     public String oldToString(){
         return root.recToString();
     }
     
     public String toString(){
         if (root.isZeroary()){
             return ""+root.getContent().toChar();
          }
          if (root.isUnary()){
             return ""+root.getContent().toChar()+leftSubtree().toString();
          }
          if (root.isBinary()){
             return "("+leftSubtree().toString()+root.getContent().toChar()+rightSubtree().toString()+")";
          }
          throw new RuntimeException("unrecognized arity in FormulaTree.toString()");
     }
     
     public String abbrev(){
    	 return logic.abbreviate(this).toString();
     }
     
     public String inFull(){
    	 return toString();
     }
    	 
     /**
      * An implementation of hashCode so we can create Sets of FormulaTrees
      * Not at all efficient, but that shouldn't matter too much.
      */

     public int hashCode(){
    	 return (int)(this.topChar());
     }
     
     /**returns whether or not the other FormulaTree 
      *equals this one
      *
      */
     public boolean equals(java.lang.Object other){
    	    if (other == null) return false;
    	    if (other == this) return true;
    	    if (this.getClass() != other.getClass()) return false;
    	    return(equals((FormulaTree) other));
     }
     
     public boolean equals(FormulaTree other){
         if (root.isZeroary()){
             if (!other.root.isZeroary()) return false;
             return root.getContent().equals(other.root.getContent());
         }
         if (root.isUnary()){
             if (!other.root.isUnary()) return false;
             if (!root.getContent().equals(other.root.getContent())) return false;
             return leftSubtree().equals(other.leftSubtree());
         }
         if (root.isBinary()){
             if (!other.root.isBinary()) return false;
             if (!root.getContent().equals(other.root.getContent())) return false;
             if (!leftSubtree().equals(other.leftSubtree())) return false;
             return rightSubtree().equals(other.rightSubtree());
         }
         return false;
     }
     
     public int length(){
         if (root.isZeroary()){
            return 1;
         }
         if (root.isUnary()){
            return 1+leftSubtree().length();
         }
         if (root.isBinary()){
            return 1+leftSubtree().length()+rightSubtree().length();
         }
         throw new RuntimeException("unrecognized arity in length");
     }
     
     /**returns an array of FormulaTrees
      *being all the subformulas of this FormulaTree
      *and their negations
      *with no repeats
      */
     public FormulaTree[] getSubformulas(){
         int len=length();
         FormulaTree[] collector=new FormulaTree[len];
         int used=recCollect(collector,0);
         //remove repeats
         //add negations
         java.util.LinkedHashSet<FormulaTree> hs = new java.util.LinkedHashSet<FormulaTree>();
         //FormulaTree[] temp=new FormulaTree[used*2];
         //int pd=0;
         for (int i=0;i<used;i++){
        	 	 hs.add(collector[i]);
        	 	 if (collector[i].topChar()!='-') { 
        	 		 hs.add(TreeBuildUnary(new Symbol('-'),true,collector[i]));
        	 	 }
         }

         FormulaTree[] answ = hs.toArray(new FormulaTree[hs.size()]);
         
/*                 boolean already=false;
                 for (int j=0;j<pd;j++)
                     if (collector[i].equals(temp[j])) already=true;
                 if (!already){
                     temp[pd]=collector[i];
                     pd=pd+1;
                 }
                 FormulaTree negn=TreeBuildUnary(new Symbol('-'),true,collector[i]);
                 already=false;
                 for (int j=0;j<pd;j++)
                     if (negn.equals(temp[j])) already=true;
                 if (!already){
                     temp[pd]=negn;
                     pd=pd+1;
                 }
         }
         FormulaTree[] answ=new FormulaTree[pd];
         for(int i=0;i<pd;i++)
             answ[i]=temp[i];   
         */
         return answ;
     }
     
     private int recCollect(FormulaTree[] collector,int upto){
         collector[upto]=this;
         upto++;
         if (root.isZeroary()){
            return upto;
         }
         if (root.isUnary()){
            return leftSubtree().recCollect(collector,upto);
         }
         if (root.isBinary()){
            upto=leftSubtree().recCollect(collector,upto);
            return rightSubtree().recCollect(collector,upto);
         }
         return 0;
     }
     
     public char topChar(){
         return root.getContent().toChar();
     }
     
     public FormulaTree(char c){
        root=new TreeNode(0,new Symbol(c),true);
     }
     
     public FormulaTree(char c,FormulaTree sub){
        root=new TreeNode(1,new Symbol(c),true);
        root.setChild(0,sub.root);
     }
     
     public FormulaTree(char c,FormulaTree left,FormulaTree right){
        root=new TreeNode(2,new Symbol(c),true);
        root.setChild(0,left.root);
        root.setChild(1,right.root);
     }
     
}
