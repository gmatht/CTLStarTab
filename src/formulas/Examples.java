/*
 * Examples.java
 *
 * Created on 19 June 2005, 17:01
 */

package formulas;

/**
 *
 * @author  mark
 */
public class Examples {
    
        
    
public static String getExample(int n){

    String[] eg=new String[18];
    
    eg[0]="G(p>q)>(Gp>Gq)";
    eg[1]="Gp>(p&Xp&XGp)";
    eg[2]="(pUq)=(q|(p&X(pUq)))";
    eg[3]="(pUq)>Fq";
    eg[4]="p>AEp";
    eg[5]="Ap>AAp";
    eg[6]="AXp>XAp";
    eg[7]="p>Ap";
    eg[8]="E(pU(E(pUq)))>E(pUq)";
    eg[9]="(AG(p>(qUr))&(qUp))>(qUr)";
    eg[10]="G(EFp>XFEFp)>(EFp>GFEFp)";
    eg[11]="AG(p>EXp)>(p>EGp)";
    eg[12]="AG(Ep>EX((Eq)U(Ep)))>(Ep>EG((Eq)U(Ep)))";
    eg[13]="(AG(p>EXr)&AG(r>EXp))>(p>EG(Fp&Fr))";
    eg[14]="p";
    eg[15]="p&Xp&F-p";
    eg[16]="AG((p&X-p&-q&-r)|(-p&Xp&q&-r)|(-p&Xp&-q&r))&E(Fq&Fr)";
    eg[17]="AG(EXp&EX-p)&AG(Gp|((-r)U(r&-p)))";
    
    if ((1<=n) && (n<=18)) return eg[n-1];
    else return "p";
}


    
}
