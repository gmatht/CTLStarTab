tput reset
getmod () {
	for f in "$@"
	do
		c=${f%.java}.class
		[ $c -nt $f ] || echo $f
	done
}
#javac `getmod *java`
echo  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
javac FormulaTree.java AUXLogic.java Pair.java JColour2.java JBranch.java  JNode.java  JHue.java JHueEnum.java Subformulas.java Timeout.java Symbol.java FormulaTree.java JHNode.java JHBranch.java TreeNode.java ParseException.java Tokenizer.java  Logic.java ClosureSubset2.java PosSubF.java && (

cd ..
rm VERB.out
#java JApplet '(Bp)Y(Bq)&(B-p)&(B-q)' ; cat default.out
#java JApplet '(pYq)&AG-q' BCTLNEW VERB.out ; cat VERB.out
#java JApplet '(pYq)&G-q' BCTLNEW VERB.out ; cat VERB.out
#java JApplet '-(pYq)&A(pUq)' BCTLNEW VERB.out ; cat VERB.out
#java JApplet '-(pYq)&p&BA(pUq)' BCTLNEW VERB.out ; cat VERB.out
#java JApplet '-Bp&p' BCTLNEW VERB.out ; cat VERB.out
#java JApplet 'A(pUq)&G-q' BCTLNEW VERB.out ; cat VERB.out
#java JApplet 'G-q' BCTLNEW VERB.out ; cat VERB.out
#java JApplet 'AG(EXp&EX-p)&AG(Gp|((-r)U(r&-p)))' BCTLNEW VERB.out ; cat VERB.out
#java JApplet 'E(pU(E(pUq)))>E(pUq)' BCTLNEW VERB.out ; cat VERB.out
#java JApplet 'AG(EXp&EX-p)&AG(Gp|((-r)U(r&-p)))' BCTLNEW VERB.out ; cat VERB.out
#java JApplet 'EXp' BCTLNEW VERB.out ; cat VERB.out
#java JApplet  'EGFXEF(Ac|a)' BCTLNEW VERB.out ; cat VERB.out
#java JApplet  'EF((c|(a|Xb))>a)' BCTLNEW VERB.out ; cat VERB.out
#java JApplet  '((a|a)&(FbUFFc))' BCTLNEW VERB.out ; cat VERB.out
#java JApplet -enableassertions  '-E((cUE(cUa))>a)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '-E((cUE(cUa))>a)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'AA-(Ga|A-Ea)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'AG((AGb>a)&Xb)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '(c&FFAX(a&-a))' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '-G(c>E(c|GEa))' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '-((a>-b)U(cU-b))' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '((b>a)|EE(Xa>c))' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '--FEA(-a>-a)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '-AFAAE(cUGc)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'F-F(c>EGGXc)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'E-E(Xc|FE-c)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '-(Xc|FE-c)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet '-(AFa>XAFa)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'FG-XX(b>GEb)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'X(-E(Eb|Fc)&b)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'X((E-c|Ea)UAb)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'F-AE(-Xa|Fa)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'A-GF(a>(b>AGa))' BCTLNEW VERB.Eout ; cat VERB.out
#java -enableassertions JApplet 'A-GF(a>AGa)' BCTLNEW VERB.Eout ; cat VERB.out
#java -enableassertions JApplet 'AFG-(a>AGa)' BCTLNEW VERB.Eout ; cat VERB.out
#java -enableassertions JApplet 'F-X((cUFa)>Fa)' BCTLNEW VERB.Eout ; cat VERB.out
#java -enableassertions JApplet 'a&EX-a&AFG(a&EX-a)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'AFG(a&EX-a)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'EGA(A((AaUb)U-b)&GFb)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'EGA(AF-b&Fb)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'EGA(AF-b&Fb)' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'AF((b>X-Ab)&EGA(b&a))' BCTLNEW VERB.out ; cat VERB.out
java -enableassertions JApplet 'AF((-Xb)&EGb)' BCTLNEW VERB.out ; cat VERB.out
rm default.out
#java -enableassertions JApplet 'EGE((Fa&-a)&E(XXbUb))' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'EGE((Fa&-a))' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'AF((b>X-b)&EGA(b&a))' BCTLNEW VERB.out ; cat VERB.out
#java -enableassertions JApplet 'EGA(AF-b&Fb)' BCTLNEW default.out ; cat default.out
#java -enableassertions JApplet 'A-GF(Aa>E(b>AGAAa))' BCTLNEW default.out ; cat default.out
java -enableassertions JApplet '-(AFAGAFAGq>AXAFAGAFGq)' BCTLNEW default.out ; cat default.out
echo foo
#java JApplet  'AFGG--FEXb' BCTLNEW VERB.out ; cat VERB.out
#java JApplet  'E-A(Ec|(c>Ab))' BCTLNEW VERB.out ; cat VERB.out
#java JApplet `tail -n1 test_formulas.txt` BCTLNEW VERB.out ; cat VERB.out
#java JApplet 'AXp' BCTLNEW VERB.out ; cat VERB.out
)
 

