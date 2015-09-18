getmod () {
	for f in "$@"
	do
		c=${f%.java}.class
		[ $c -nt $f ] || echo $f
	done
}
#javac `getmod *java`
echo  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
javac FormulaTree.java AUXLogic.java Pair.java JColour2.java JBranch.java  JNode.java  JHue.java JHueEnum.java Subformulas.java Timeout.java Symbol.java FormulaTree.java JHNode.java JHBranch.java TreeNode.java ParseException.java Tokenizer.java  Logic.java ClosureSubset2.java PosSubF.java  && (

cd ..
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
java JApplet  '-E((cUE(cUa))>a)' BCTLNEW VERB.out ; cat VERB.out
#java JApplet  'AFGG--FEXb' BCTLNEW VERB.out ; cat VERB.out
#java JApplet  'E-A(Ec|(c>Ab))' BCTLNEW VERB.out ; cat VERB.out
#java JApplet `tail -n1 test_formulas.txt` BCTLNEW VERB.out ; cat VERB.out
#java JApplet 'AXp' BCTLNEW VERB.out ; cat VERB.out
)
 

