/*
 * ClosureSubset2.java
 *
 * Created on 20 May 2005, 18:55
 */

package formulas;

/**
 *
 * @author  mark
 */
public class ClosureSubset2 {
    
    private FormulaTree owner;
    private Subformulas sf;
    private PosSubF psf;
    private boolean[] member; //only store the positives
    
    
    /** Creates a new instance of ClosureSubset2 */
    public ClosureSubset2(FormulaTree owner, Subformulas sf, PosSubF psf) {
        this.owner=owner;
        this.sf=sf;
        this.psf=psf;
       
        member=new boolean[psf.count()];
        for(int i=0; i<member.length; i++) member[i]=false;
    }
    
    public ClosureSubset2 next(){
        ClosureSubset2 c=new ClosureSubset2(owner,sf,psf);
        for(int i=0; i<member.length;i++)
            c.member[i]=member[i];
        c.makeNext();
        return c;
    }
    
    public void makeNext(){
        int pos=member.length-1;
        while ((pos>=0) && (member[pos])) {
            member[pos]=false;
            pos--;
        }
        if (pos>=0) member[pos]=true;
    }
    
    public boolean isLast(){
        int pos=member.length-1;
        while ((pos>=0) && (member[pos])) {
            pos--;
        }
        return (pos<0);
    }
    
    //whether sf[i] is a member or not
    public boolean member(int sfnum){
        int psfnum=psf.sf2psf(sfnum);
        if (psfnum<0)
            return !member[-psfnum-1];
        else
            return member[psfnum-1];
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
    
    
    //temporal consistentcy
    public boolean isMTC(){
        return owner.getLogic().isMTC(this);
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
    
    
    //could other come one time unit after this
    public boolean precs(ClosureSubset2 other){
        return owner.getLogic().precs(this,other);
    }//end precs
    
}
