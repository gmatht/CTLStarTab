package quicktab;

import formulas.FormulaTree;
import formulas.Subformulas;



public class Node {
	
	private ClosureSubset[] label;
	
	private int[] successor; //maps hue numbers to main successor node number: go to hue 0 there
	private int[] predecessor; //list of all predecessors, parent is first one
	private int[][] preHueNum; //for i-th predecessor yi, for this node's j-th hue
								//preHueNum[i][j]=h tells us that hue h of yi  is 
	                            //the official predecessor hue there of hue j here
	
	private int index;
	private boolean virgin=true; //no rules have been applied here yet
	
	private int[] possMatchAncs; //ancestors who this matches, could be uplinks
	private boolean[] triedUpLinks; //which of those have been tried; maybe failed or still there
	
	private boolean isPseudo=false; //these nodes just correspond to a reordered version of another node
	private int pseudoProxy;
	private int pseudoHue;

	public Node(FormulaTree f, int idx) {
		
		index=idx;
		label=new ClosureSubset[1];
		label[0]=new ClosureSubset(f);
		label[0].add((label[0]).getSf().getIndexOfOwner());
		predecessor=null;
		successor=new int[1];
		successor[0]=-1;
		
		possMatchAncs=new int[0];
		triedUpLinks=new boolean[0];
	}

	public Node(FormulaTree f, int idx, int pnode, int phue) {
		index=idx;
		label=new ClosureSubset[1];
		label[0]=new ClosureSubset(f);
		//label[0].add((label[0]).getSf().getIndexOfOwner());
		
		successor=new int[1];
		successor[0]=-1;
		
		predecessor=new int[1];
		predecessor[0]=pnode;
		preHueNum= new int[1][];
		preHueNum[0]=new int[1];
		preHueNum[0][0]=phue;
		
		possMatchAncs=new int[0];
		triedUpLinks=new boolean[0];
		
	}

	public Node(FormulaTree owner, int idx, PseudoProxy pp) {
		index=idx;
		label=new ClosureSubset[1];
		label[0]=new ClosureSubset(owner);
		//label[0].add((label[0]).getSf().getIndexOfOwner());
		
		successor=new int[1];
		successor[0]=-1;
		
		predecessor=new int[1];
		predecessor[0]=-1;
		preHueNum= new int[1][];
		preHueNum[0]=new int[1];
		preHueNum[0][0]=-1;
		
		possMatchAncs=new int[0];
		triedUpLinks=new boolean[0];
		
		isPseudo=true; 
		pseudoProxy=pp.node;
		pseudoHue=pp.hue;
	}

	public ClosureSubset getLabel(int i) {
		
		if (i<label.length) return label[i];
		else {
			System.out.println("DEBUG: node "+index+" only has "
					+label.length+" hues now but you ask for "+i);
		}
    	return null;
	}
	
	public String toString(){
		
		if (isPseudo){
			return "Is a pseudonode, proxy for "+pseudoProxy+": hue "+pseudoHue+".\n";
			
		}
		
		String s="";
		s=s+"label {";
		for (int i=0; i<label.length;i++)
			s=s+" "+label[i]+" ";
		s=s+"} \n";
		
		s=s+" successors: ";
		for(int i=0;i<successor.length;i++)
			s=s+successor[i]+", ";
		s=s+"\n";
		
		if (predecessor==null)
			s=s+"no predecessors \n";
		else {
			s=s+"  "+predecessor.length+" predecessors: \n";
			for(int i=0;i<predecessor.length;i++){
				s=s+" "+predecessor[i]+": hues here traceback resp. to hues ";
				for(int j=0; j<numHues();j++){
					s=s+" "+preHueNum[i][j]+": ";
				}
				s=s+"\n";
			}
			s=s+"\n";
		}
		
		return s;
	}

	public Node deepCopy(FormulaTree f) {
		// TODO Auto-generated method stub
		
		Node n=new Node(f,index);
		n.label=new ClosureSubset[label.length];
		for(int i=0;i<label.length;i++)
			n.label[i]=label[i].deepCopy();
		
		n.successor=new int[successor.length];
		for(int i=0;i<successor.length;i++)
			n.successor[i]=successor[i];
		
		if (predecessor==null){
			n.predecessor=null;
		} else {
			n.predecessor=new int[predecessor.length];
			n.preHueNum=new int[predecessor.length][];
			for(int i=0;i<predecessor.length;i++){
				n.predecessor[i]=predecessor[i];
				n.preHueNum[i]=new int[numHues()];
				for(int j=0;j<numHues();j++)
					n.preHueNum[i][j]=preHueNum[i][j];  
			}
		}
		
		//possMatchAncs int[];
		if (possMatchAncs==null) n.possMatchAncs=null;
		else {
			n.possMatchAncs=new int[possMatchAncs.length];
			for(int i=0;i<possMatchAncs.length;i++)
				n.possMatchAncs[i]=possMatchAncs[i];  
		}
		
		//triedUpLinks boolean[];
		if (triedUpLinks==null) n.triedUpLinks=null;
		else {
			n.triedUpLinks=new boolean[triedUpLinks.length];
			for(int i=0;i<triedUpLinks.length;i++)
				n.triedUpLinks[i]=triedUpLinks[i];  
		}
		
		n.virgin=virgin;
		
		//pseudo
		n.isPseudo=isPseudo; 
		n.pseudoProxy=pseudoProxy;
		n.pseudoHue=pseudoHue;
		
		return n;
	}
	
	public int addNewHue(){
		Subformulas sf=label[0].getSf();
		FormulaTree owner=sf.getFormula(sf.getIndexOfOwner());
		int n=label.length;
		ClosureSubset[] newLabel=new ClosureSubset[n+1];
		for(int i=0;i<n;i++)
			newLabel[i]=label[i];
		newLabel[n]=new ClosureSubset(owner);
		label=newLabel;
		
		int[] nsucc=new int[n+1];
		for(int i=0;i<n;i++)
			nsucc[i]=successor[i];
		nsucc[n]=-1;
		successor=nsucc;
		
		if (predecessor!=null) for(int i=0;i<predecessor.length;i++){
			int[] nphn=new int[n+1];
			for(int j=0;j<n;j++)
				nphn[j]=preHueNum[i][j];
			nphn[n]=-1;
			preHueNum[i]=nphn;
		}
		
		
		return n;
	}

	public int numHues() {
		return label.length;
	}

	public int getSuc(int hue) {
		if (successor==null) return -1;
		if (successor.length<=hue) return -1;
		int sn=successor[hue];
		if (sn==-1) return -1;
		return sn;
	}

	private int getIndex() {
		return index;
	}

	public void putInZeroHue(int msf) {
		if ((label==null) || (label.length==0)){
			System.out.println("Programming error: node with empty label");
		} else {
			label[0].add(msf);
		}
		
	}
	
	public void putInHue(int ph, int msf) {
		if ((label==null) || (label.length==0)){
			System.out.println("Programming error: node with empty label");
		} else {
			label[ph].add(msf);
		}
		
	}

	public void addSucc(int phue, int sn) {
		successor[phue]=sn;
		
	}

	public int getParent() {
		if (predecessor==null) return -1;
		if (index==0) return -1;
		return predecessor[0];
	}
	
	public int getPredecessor(int i) {
		return predecessor[i];
	}

	public int getPredHue(int pn, int h) {
		return preHueNum[pn][h];
	}

	public void makePreHue(int hueHere, int predNum, int huePre) {	
		preHueNum[predNum][hueHere]=huePre;
	}

	public boolean exactMatches(Node node) {
		if (label.length!=node.label.length) return false;
		for(int i=0;i<label.length;i++)
			if (!label[i].equals(node.label[i])) return false;
		return true;
	}
	
	public boolean matches(Node higherNode) {
		if (label.length>higherNode.label.length) return false;
		for(int i=0;i<label.length;i++)
			if (!label[i].isSubsetOf(higherNode.label[i])) return false;
		return true;
	}

	public void setSucc(int yh, int chx) {
		successor[yh]=chx;
		
	}
	
	//for our purposes a leaf is a node
	//with only one hue and no successor
	public boolean isLeaf(){
		if (numHues()>1) return false;
		
		if (successor[0]>-1) return false;
		
		if (isPseudo()) return true;
		
		return true;
	}

	public boolean precs(int j) {
		for(int i=0;i<successor.length;i++)
			if (successor[i]==j) return true;
		return false;
	}

	public int numsucc() {
		return successor.length;
	}

	public int succ(int j) {
		return successor[j];
	}

	public void recordTriedUplinkTo(int an) {
		int indx=addMancs(an);
		triedUpLinks[indx]=true;
		
	}

	private int addMancs(int an) {
		for(int i=0;i<possMatchAncs.length;i++)
			if (possMatchAncs[i]==an) return i;
		
		int nl=possMatchAncs.length+1;
		int[] npma=new int[nl];
		boolean[] ntul=new boolean[nl];
		for(int i=0;i<possMatchAncs.length;i++){
			npma[i]=possMatchAncs[i];
			ntul[i]=triedUpLinks[i];
		}
		npma[nl-1]=an;
		ntul[nl-1]=false;
		
		possMatchAncs=npma;
		triedUpLinks=ntul;
		
		return npma.length-1;
	}

	public boolean alreadyTriedUpTo(int an) {
		for(int i=0;i<possMatchAncs.length;i++)
			if ((possMatchAncs[i]==an) && (triedUpLinks[i])) return true;
		
		return false;
	}

	//makes a new hue just the same as given one
	public int splitHue(int ch) {
		int nh=addNewHue();
		
		label[nh]=label[ch].deepCopy();
		
		return nh;
	}
	
	public boolean isVirgin(){
		return virgin;
	}
	
	public void unvirgin(){
		virgin=false;
	}

	public int getIndexOfPred(int currentNode) {
		for(int i=0;i<predecessor.length;i++)
			if (predecessor[i]==currentNode) return i;
		return -1;
	}

	public int[] getPreHueMap(int c) {
		return preHueNum[c];
	}

	//adds a new predecessor to this node
	//maps the hues in this map back according to 
	public void addPredecessor(int y, int[] preHueMap) {
		int ol=0;
		if (predecessor==null){			
		} else {
			ol=predecessor.length;
		}
		
		int[] npa=new int[ol+1];
		int[][] npha=new int[ol+1][];
		for(int i=0;i<ol;i++){
			npa[i]=predecessor[i];
			npha[i]=new int[preHueNum[i].length];
			for(int j=0;j<preHueNum[i].length;j++)
				npha[i][j]=preHueNum[i][j];
		}
		npa[ol]=y;
		int nal=preHueMap.length;
		int nphal=numHues();
		npha[ol]=new int[nphal];
		for(int j=0;j<nphal;j++){
			if (j<nal)
			npha[ol][j]=preHueMap[j];
			else npha[ol][j]=-1;
		}
		
		predecessor=npa;
		preHueNum=npha;
		
	}
	
	public void makePseudo(int pn, int ph){
		pseudoProxy=pn;
		pseudoHue=ph;
		isPseudo=true;
	}
	
	public boolean isPseudo(){
		return isPseudo;
	}
	
	public int pseudoProxy(){
		return pseudoProxy;
	}
	
	public int pseudoHue(){
		return pseudoHue;
	}

	public int addNewProxyHue(int currentNode, int currentHue) {

		Subformulas sf=label[0].getSf();
		FormulaTree owner=sf.getFormula(sf.getIndexOfOwner());
		int n=label.length;
		ClosureSubset[] newLabel=new ClosureSubset[n+1];
		for(int i=0;i<n;i++)
			newLabel[i]=label[i];
		newLabel[n]=new ClosureSubset(owner);
		label=newLabel;
	
		int[] nsucc=new int[n+1];
		for(int i=0;i<n;i++)
			nsucc[i]=successor[i];
		nsucc[n]=-1;
		successor=nsucc;
	
		if (predecessor!=null) for(int i=0;i<predecessor.length;i++){
			int[] nphn=new int[n+1];
			for(int j=0;j<n;j++)
			nphn[j]=preHueNum[i][j];
			nphn[n]=-1;
			preHueNum[i]=nphn;
		}
	
	
	return n;
	}

	public void setPreds(int proxy, int predHueNum) {
		predecessor[0]=proxy;
		preHueNum[0][0]=predHueNum;
		
	}

}
