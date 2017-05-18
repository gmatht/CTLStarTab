/*
 * PosSubF.java
 *
 * Created on 20 May 2005, 19:09
 */

package formulas;

/**
 *
 * @author  mark
 */
public class PosSubF {
    
    private Subformulas sf;
    private int[] psf2sf;
    private int[] sf2psf;
    
    /** Creates a new instance of PosSubF */
    public PosSubF(Subformulas s) {
        sf=s;
        sf2psf=new int[s.count()];
        
        //count the +ve, adding 1
        int c=0;
        for (int i=0; i<s.count(); i++)
            if (sf.topChar(i) !='-') {
                c++;
                sf2psf[i]=c;
            }
        
        psf2sf=new int[c+1];
        for(int i=0;i<s.count(); i++)
            psf2sf[sf2psf[i]]=i;
        
        //fill in the negated sfs
        for(int i=0; i<s.count(); i++){
            boolean parity=true;
            int sfnum=i;
            while (sf.topChar(sfnum)=='-'){
                sfnum=sf.left(sfnum);
                parity=!parity;
            }
            sf2psf[i]=sf2psf[sfnum];
            if (!parity) sf2psf[i]=-sf2psf[i];  
        }
        
        //debug
        //System.out.println("positive subforms");
        //System.out.println("There are "+(psf2sf.length-1)+" positive subforms");
        
        //psf[0] is not used
        /*
        for(int i=1; i<psf2sf.length; i++)
            System.out.println("psf["+i+"]= sf["+psf2sf[i]+"]");
        
        for(int i=0; i<sf2psf.length; i++){
            if (sf2psf[i]>0)
                System.out.println("sf["+i+"]=psf["+sf2psf[i]+"]");
            else 
                System.out.println("sf["+i+"]= - psf["+(-sf2psf[i])+"]");
        }
            
         System.out.println("End of info about positive subforms");
         */
    }
    
    public int count(){
        return psf2sf.length-1;
    }
    
    public int sf2psf(int i){
        return sf2psf[i];
    }
    
}
