/*
 * Subformulas.java
 *
 * Created on 20 May 2005, 16:37
 */

package formulas;

/**
 *
 * @author  mark
 */
public class Subformulas {
    
    private FormulaTree owner;
    private FormulaTree[] closure;

    private int[] neg; //index of neg closure[i]
    private char[] topChar;
    private int[] left;
    private int[] right;
    
    
      /** Creates a new instance of Subformulas */
    public Subformulas(FormulaTree owner) {
        this.owner=owner;
        closure=owner.getSubformulas();
        createMaps();
    }

    
    
    //finds indices of related fmlas
    private void createMaps(){
        int len=closure.length;
        topChar=new char[len];
        neg=new int[len];
        left=new int[len];
        right=new int[len];
        
        for(int i=0;i<len;i++){
            
            topChar[i]=closure[i].topChar();
            left[i]=-1;
            right[i]=-1;
            
            neg[i]=-1;
            for (int j=0;j<closure.length;j++)
                if (closure[j].topChar()=='-'){
                    if (closure[j].leftSubtree().equals(closure[i]))
                        neg[i]=j;
                }
            if (neg[i]==-1){
                if (closure[i].topChar() != '-') throw new RuntimeException("error in finding negations in closure");
                for (int j=0;j<len;j++)
                    if (closure[i].leftSubtree().equals(closure[j]))
                        neg[i]=j;
                }
            
            for (int j=0;j<len;j++){
                if (new Symbol(topChar[i]).isUnary()){
                    if (closure[i].leftSubtree().equals(closure[j]))
                        left[i]=j;
                }
                if (new Symbol(topChar[i]).isBinaryInfix()){
                    if (closure[i].leftSubtree().equals(closure[j]))
                        left[i]=j;
                    if (closure[i].rightSubtree().equals(closure[j]))
                        right[i]=j;
                }
            }
            //System.out.println("DEBUG:  "+i+" maps to "+topChar[i]+", "+left[i]+", "+right[i]);
            }
        }//end createMaps
    
    public int count(){
        return closure.length;
    }
    
    public char topChar(int i){
        return topChar[i];
    }

    /**
     * This is mainly useful where you have a negation,
     *    so followString(i,"&-") would return the index of "&f"
     *    if the ith formula matched "-&...", and 0 other wise
     *
     * @param s
     * @param i
     * @return
     */

    public int followString(int i, String s) {
        switch (s.length()) {
            case 2:
                if (s.charAt(1)!=topChar[i]) return -1;
                i = left[i];
            case 1:
                if (s.charAt(0)!=topChar[i]) return -1;
                break;
            default:
                throw new RuntimeException();
        }
        return i;
    }

    
    public int left(int i){
        return left[i];
    }
    
    public int right(int i){
        return right[i];
    }
    
    public int negn(int i){
        return neg[i];
    }
    
    public boolean isZeroary(int i){
        return ((left[i]==-1) && (right[i]==-1));
    }
    
    public boolean isUnary(int i){
        return ((left[i]>=0) && (right[i]==-1));
    }
    
    public boolean isBinary(int i){
        return ((left[i]>=0) && (right[i]>=0));
    }
    
    public FormulaTree getFormula(int i){
        return closure[i];
    }
    
    public int getIndexOfOwner(){
        return 0;
    }
    
    //returns the list of the indices in some increasing order
    //ie if sf #3 is a subform of #2 then #3 will come first
    //the result[1] will be the index of the first in this order etc
    public int[] sortinc(){
    	int c=count();
    	int[] index=new int[c];
    	for(int i=0;i<c;i++){
    		index[i]=-1;
    	}
    	int u=0;
    	for (int rd=0; rd<c; rd++){
    		for (int i=0;i<c;i++){
    			if (index[i]<0){
    				if (isZeroary(i)) index[i]=u++;
    				if ((isUnary(i)) && (index[left(i)]>=0)) index[i]=u++;
    				if ((isBinary(i)) && (index[left(i)]>=0) && (index[right(i)]>=0)) index[i]=u++;
    			}
    		}
    	}
    	
    	int[] answ=new int[c];
    	for(int i=0;i<c;i++){
    		answ[index[i]]=i;
    	}
    	return answ;
    }


}
