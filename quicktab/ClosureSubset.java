package quicktab;

import formulas.FormulaTree;
import formulas.Subformulas;

/**
*
* @author  mark
*/
public class ClosureSubset {
   
   private FormulaTree owner;
   private Subformulas sf;
   private boolean[] member; 
   
   
   /** Creates a new instance of ClosureSubset2 */
   public ClosureSubset(FormulaTree owner, Subformulas sf) {
       this.owner=owner;
       this.sf=sf;
      
      
       member=new boolean[sf.count()];
       for(int i=0; i<member.length; i++) member[i]=false;
   }
   
   
   public ClosureSubset(FormulaTree owner) {
	   this(owner, new Subformulas(owner));
	   
   }
 
   
   //whether sf[i] is a member or not
   public boolean member(int sfnum){
           return member[sfnum];
   }
   
   public String toString(){
       String s="{";
       boolean start=true;
       for(int i=0; i<sf.count(); i++){
           if (member(i)) {
               if (!start) s=s+",  ";
               start=false;
               s=s+sf.getFormula(i).abbrev();
           }
       }
       s=s+"}";
       return s;
   }
   
   
   
   public int negn(int i){
       return sf.negn(i);
   }
   
   public int left(int i){
       return sf.left(i);
   }
   
   public int right(int i){
       return sf.right(i);
   }
   
   public char topChar(int i){
       return sf.topChar(i);
   }
   
   //does the cs contain the owner?
   public boolean isInitial(){

       return member(sf.getIndexOfOwner());
   }
   
   public Subformulas getSf(){
       return sf;
   }
   
   public void add(int sfnum){
	   member[sfnum]=true;
   }


   public ClosureSubset deepCopy() {
	
	ClosureSubset ncs=new ClosureSubset(owner,sf);
	ncs.member=new boolean[member.length];
	for(int i=0;i<member.length;i++)
		ncs.member[i]=member[i];
	
	return ncs;
	}

	public boolean equals(ClosureSubset other){
		for(int i=0;i<member.length;i++)
			if (member[i] != other.member[i]) return false;
		return true;
	}


	public boolean isSubsetOf(ClosureSubset other) {
		for(int i=0;i<member.length;i++)
			if ((member[i]) && (!other.member[i])) return false;
		return true;
	}


	public int count() {
		int ct=0;
		for(int i=0;i<member.length;i++)
			if (member[i]) ct++;
		return ct;
	}
   
   
}
