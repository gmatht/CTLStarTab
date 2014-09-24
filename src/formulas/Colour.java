/*
 * Colour.java
 *
 * Created on 29 May 2005, 10:12
 */

package formulas;

/**
 *
 * @author  mark
 */
public class Colour {
    
    public static TemporalGraph theTG;
    
    private int whichEqClass;
    private boolean[] whoseIn;
    private boolean allDone;
    
    /** Creates a new instance of Colour */
    public Colour(TemporalGraph tg) {
        theTG=tg;  
    }
    
    public Colour(){
    }
    
    public boolean contains(int mtcsnum){
        int[] ms=theTG.contentsOfEquivClass(whichEqClass); 
        for(int i=0; i<ms.length; i++) 
            if ((ms[i]==mtcsnum) && (whoseIn[i])) return true;
        return false;
    }
    
    public int[] contents(){
        int ctr=0;
        for(int i=0;i<whoseIn.length;i++)
            if (whoseIn[i]) ctr++;
        int[] ms=theTG.contentsOfEquivClass(whichEqClass); 
        int[] answ=new int[ctr];
        ctr=0;
        for(int i=0;i<ms.length;i++)
            if (whoseIn[i]) answ[ctr++]=ms[i];
        return answ;
    }
    
    public void findFirstCandidate(){
        allDone=false;
        whichEqClass=0;
        if (theTG.numEquivClasses()==0) {
            allDone=true;
            return;
        }
        int sz=theTG.sizeOfEqClass(0);
        whoseIn=new boolean[sz];
        for(int i=0;i<sz;i++) whoseIn[i]=false;
        
    }
    
    public void makeNextCandidate(){
        int i=whoseIn.length-1;
        while ((i>=0) && (whoseIn[i])) {
            whoseIn[i]=false;
            i--;
        }
        if (i>=0) {
            whoseIn[i]=true;
            return;
        }
        whichEqClass++;
        if (theTG.numEquivClasses()<=whichEqClass) {
            allDone=true;
            return;
        }
        int sz=theTG.sizeOfEqClass(whichEqClass);
        whoseIn=new boolean[sz];
        for(int ij=0;ij<sz;ij++) whoseIn[ij]=false;
    }
    
    public Colour nextCandidate(){
        Colour c=new Colour();
        c.whichEqClass=whichEqClass;
        c.whoseIn=new boolean[whoseIn.length];
        for(int i=0;i<whoseIn.length;i++) c.whoseIn[i]=whoseIn[i];
        c.allDone=allDone;
        c.makeNextCandidate();
        return c;
    }
    
    public boolean allDone(){
        return allDone;
    }
    
    
    public String toString(){
        int[] ms=theTG.contentsOfEquivClass(whichEqClass); //the mtcs
        String s="{";
        boolean start=true;
        for(int i=0;i<whoseIn.length;i++)
            if (whoseIn[i]){
                if (!start) s=s+", ";
                start=false;
                s=s+ms[i];
            }
        s=s+"}";
        return s;
    }
    
    public boolean isAColour(){
        //is non-empty
        int i=0;
        while ((i<whoseIn.length) && !(whoseIn[i])) i++;
        if (i==whoseIn.length) return false;
        
        //every E defect is cured
             int[] ms=theTG.contentsOfEquivClass(whichEqClass); //the mtcs
             Subformulas sf=theTG.getSubformulas();
             for(int j=0;j<sf.count();j++)
                 if ((theTG.contains(ms[0],j)) && (sf.topChar(j)=='-') && (sf.topChar(sf.left(j))=='A')){
                     //System.out.print(sf.getFormula(j)+" ");
                     int cure=sf.negn(sf.left(sf.left(j)));
                     //System.out.print(" cured by "+sf.getFormula(cure)+" in ");
                     boolean cured=false;
                     for(int k=0;k<whoseIn.length;k++){
                         if ((whoseIn[k]) && (theTG.contains(ms[k],cure)))
                             cured=true;
                     }//end k
                     if (!cured) return false;
             }//end for j
        
        return true;
    }
    
    //RX reln between colours
    //this RX others
    //that is, for every hue h' in other, there is some hue h in this
    //such that h precs h' in the temporal graph
    public boolean precs(Colour other){
    	Timeout.yield();
        int[] ms1=theTG.contentsOfEquivClass(whichEqClass); 
        int[] ms2=theTG.contentsOfEquivClass(other.whichEqClass); 
        
        for(int i=0; i<ms2.length; i++) if (other.whoseIn[i]) {
            boolean fd=false;
            for(int j=0; j<ms1.length; j++){
                if ((theTG.succ(ms1[j],ms2[i])) && (whoseIn[j])) {
                    fd=true;
                }
            }
            if (!fd) return false;
        }
        return true; //temporary
    }//end precs

    
}

