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
    private boolean[] state_formula;
    private boolean[] path_sensitive;
    private boolean[] temporal_op;
    
    
      /** Creates a new instance of Subformulas */
    public Subformulas(FormulaTree owner) {
        this.owner=owner;
        closure=owner.getSubformulas();
        createMaps();
    }

   
   private boolean state_formula_(int i){ 
	return AUXLogic.state_formula(closure[i]);
	    //switch (topChar[i]) {
		//case 'A': case 'E': case 'B': /*AX*/ return true;
		//case 'Y': case 'I': /*AU, EU*/ return true;
		//case 'U': case 'F': case 'G': case 'X': case 'W': return false; 
		//default:  if (JColour2.state_variables && (topChar[i] >= 'a' && topChar[i] <= 'z')) return false;
			  //return (
			//(left[i]  < 0 || state_formula_(left[i] )) &&
			//(right[i] < 0 || state_formula_(right[i])));
	   //}
   }
    
    //finds indices of related fmlas
    private void createMaps(){
	int len=closure.length;
	topChar=new char[len];
	neg=new int[len];
	left=new int[len];
	right=new int[len];

	state_formula = new boolean[len];
	//AE_no_star = new boolean[len]; /* must be treated as a BCTL* operator, rather than just BCTL */
	//full_op = new boolean[len]; /* not just part of a BCTL joined operator */
	path_sensitive = new boolean[len];
	temporal_op = new boolean[len];
	
	for(int i=0;i<len;i++){
	//for(int i=len-1;i>=0;i--){
	    
	    topChar[i]=closure[i].topChar();
	    left[i]=-1;
	    right[i]=-1;
	    //AE_no_star[i]=false;
	    //full_op=(i==0);
	    
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

	    temporal_op[i] = ("UFGWX".indexOf(topChar[i])) >= 0;

	   //if (!AE_no_star[i]) {
		//if (left [i]>=0) full_op[left[i] ]=true;
		//if (right[i]>=0) full_op[right[i]]=true;
	   //}
	
	    //System.out.println("DEBUG:  "+i+" maps to "+topChar[i]+", "+left[i]+", "+right[i]);
	}
	System.out.println("");
	for(int i=0;i<len;i++){
	        state_formula[i]=state_formula_(i);
		path_sensitive[i]=(!state_formula[i]);
	   	//if (state_formula[i]) {
	   	//System.out.print("  SF: "+topChar[i]);
	   	//} else System.out.print(" ~sf: "+topChar[i]);
	}
	for(int j=0;j<len*2;j++){ //so it converges
	for(int i=0;i<len;i++){
		//path_sensitive[i]=false;
		//if (full_op[i]) {
			//if(temporal_op[i]) {
			if(!state_formula[i]) {
				path_sensitive[i]=true;
				if (left [i]>=0) path_sensitive[left[i] ]=true;
				if (right[i]>=0) path_sensitive[right[i]]=true;
			}

			if(path_sensitive[neg[i]]) {path_sensitive[i]=true;}
			if(path_sensitive[i]) {path_sensitive[neg[i]]=true;}
			//if (topChar[i] == 'A' || (topChar[i] == '-' && topChar[left[i]] == 'A')) path_sensitive[i]=true; //May not be needed?
		//}
	}}
	System.out.println("");
	//for(int i=0;i<len;i++){
	//   	if (path_sensitive[i]) System.out.print("  PS: "+topChar[i]);
	//	else System.out.print(" ~ps: "+topChar[i]);
	//}

	       
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

    public boolean state_formula(int i){
	return state_formula[i];
    }

	public boolean path_sensitive(int i){
		return path_sensitive[i];
	}

    //public boolean full_op(int i){
	//return full_op[i];
	//return true;
    //}
    
    
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
