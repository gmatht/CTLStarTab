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
	
	if ((100<n) &&(n<500)) return theta(n);

    String[] eg=new String[23];
    
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
    eg[18]="-(AFAGq>AFGq)";
    eg[19]="AG(p=X-p)&AG(p>-q)&AG(p>-r)&AG(q>-r)&AG(Fq&Fr)&q";
    eg[20]="AGp&XEF-p";
    eg[21]="A(Fp>G-q)&-p&-q&XG-p&XG-q&XEFp&XEFq";
    eg[22]="AG(p>(EFp&EFq))&AG(q>(EFp&EFq))&AF(G-p|G-q)&p";
    
    
    
    if ((1<=n) && (n<=23)) return eg[n-1];
    else if ((n<=-1) && (n>=-23)) return "-("+eg[-1-n]+")";
    else return "p";
}

public static boolean isSatisfiable(int n){
	if ((1<=n) && (n<=18)) return true;
	if ((-14<=n) && (n<=-1)) return false;
	if ((-18<=n) && (n<=-15)) return true;
	if ((-23<=n) && (n<=-19)) return true; 
	if ((n==20) || (n==22) || (n==23)) return true; 
	
	return false; //default
}

public static String unSat(int n){
	if (isSatisfiable(n))
		return "SAT";
	else return "UNSAT";
}

public static String comment(int n){
	
    String[] cpeg=new String[19];
    
    //don't use cpeg[0]
    cpeg[1]="Valid. Normal rule for G.";
    cpeg[2]="Valid. Induction of G over X.";
    cpeg[3]="Valid. Property of Until.";
    cpeg[4]="Valid. Property of Until.";
    cpeg[5]="Valid. S5 of A.";
    cpeg[6]="Valid. S5 of A.";
    cpeg[7]="Valid. Interaction of X and A.";
    cpeg[8]="Valid. Necessity of atoms.";
    cpeg[9]="Valid. Until property.";
    cpeg[10]="Valid. Until property.";
    cpeg[11]="Valid. Induction.";
    cpeg[12]="Valid. Simplest limit closure test.";
    cpeg[13]="Valid. Limit closure.";
    cpeg[14]="Valid. Limit closure.";
    cpeg[15]="Sat. Immediate.";
    cpeg[16]="Sat. Simple delayed eventuality.";
    cpeg[17]="Sat. Needs careful loop checking.";
    cpeg[18]="Sat. Difficult brancher but has a small model.";
    cpeg[19]="? new one";
    cpeg[20]="? new one";
    cpeg[21]="? new one";
    cpeg[22]="? new one";
    
    if ((1<=n) && (n<=22)) return cpeg[n];
    
    return "No comment recorded on this example.";
}

public static void main(String[] args){
	
}

 public static String theta(int n){
	 if ((100<n) && (n<120)) return chi(n-100);
	 if ((200<n) && (n<220)) return "-"+chi(n-200);
	 if ((300<n) && (n<320)) return psi(n-300);
	 if ((400<n) && (n<420)) return "-"+psi(n-400);
	 return "p";
 }

 public static String chi(int n){
	 return "("+alpha(n)+">"+beta(n)+")";
 }
 
 public static String psi(int n){
	 return "("+beta(n)+">"+alpha(n)+")";
 }
 
 public static String alpha(int n){
	 if (n<=1) return "AFGq";
	 return "AFG"+alpha(n-1);
 }
 
 public static String beta(int n){
	 if (n<=1) return "AFAGq";
	 return "AFAG"+beta(n-1);
 }
    
}
