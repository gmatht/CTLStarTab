package quicktab;

import java.util.ArrayList;

import formulas.Examples;
import formulas.Pair;
import formulas.ParseException;
import formulas.AUXLogic;
import formulas.FormulaTree;
import formulas.Logic;
import formulas.Keyboard;
import formulas.Subformulas;

public class QuickTab implements TransitionStructure {
	
	private FormulaTree owner;
	private Subformulas sf;
	
	private int currentNode=0;
	private int currentHue=0;
	private Node[] node;
	private boolean backtracking=false;
	
	private boolean finished=false;
	private int stepNo=0;
	private boolean uncheckedLoop=false;
	
	private static boolean silent=false;
	private boolean usedUnjustifiedBacktrack=false;
	private boolean backtrackOnRepetition=false;
	private boolean repDetected=false;
	private int maxrepeats=2;
	
	private static BackTrackStack bts=new BackTrackStack();
	
	private static int finalResult=4; 
	//0 parse error
	//1 satisfiable
	//2 unsatis
	//3 unsatis using repetit rule
	//4 ran out of time
	public static long timeTaken;
	public static int numSteps;
	public static int fmlaLength;
	
	private static ArrayList hueList=new ArrayList(); //only for experiments
	private static ArrayList colourList=new ArrayList(); //only for experiments
	private static ArrayList mosaicList=new ArrayList(); //only for experiments
	private static ArrayList startList=new ArrayList(); //only for experiments
	private static ArrayList earlyHueList=new ArrayList(); //only for experiments
	private boolean[] explored;
	private int[] latestEarlyHue=new int[0];
	
	public boolean dfs=true; //use depth first search to choose leaves
	//private boolean earlyHueRepeat;

	public QuickTab(FormulaTree f) {
		
		owner=f;
		fmlaLength=f.length();
		sf=new Subformulas(f);
		node=new Node[1];
		node[0]=new Node(f,0);
		//bts=new BackTrackStack();
	}
	
	public static int decide(String fs,long maxMillis, int version){
		//0 parse error
		//1 satisfiable
		//2 unsatis
		//3 unsatis using repetit rule
		//4 ran out of time
		
		silent=false;		
		bts=new BackTrackStack();		
		finalResult=4;
		
		sop(true,"Test satisfiability in CTL* using QuickTab of "+ fs);	
        try {
            Logic logic=new AUXLogic();
            FormulaTree f=logic.parse(fs);
            f=logic.disabbreviate(f);
            QuickTab qt=new QuickTab(f);         
            qt.processAutoLimit(maxMillis,version);
            
        } catch(ParseException e){
            System.out.println(e.getMessage()); 
            return 0;
        }
        
        return finalResult;
				
	}

	private void processAutoLimit(long maxMillis,int version) {
		
		if (version==2) {
			processAutoLimitBetaVersion(maxMillis);
			return;
		}
			
		backtrackOnRepetition=true;
		silent=true;
		int rctr=stepNo+1;
		System.out.println("At step "+stepNo+" current tableau:"+this);
		long stime=System.currentTimeMillis();
		while ((!finished) && (System.currentTimeMillis()-stime<maxMillis)){
			findIncompleteNode(); 
			if (!finished) oneStep(false);
			if (stepNo>=rctr){
				silent=false;
				sop(true,"At step "+stepNo+" current tableau:"+this);
				sop(true,"");
				silent=true;
				rctr=2*rctr;
			}
		}
		silent=false;
		long etime=System.currentTimeMillis();
		sop(true,"Elapsed time = "+((etime-stime))+" milliseconds.");
		numSteps=stepNo;
		timeTaken=etime-stime;
		
	}

	public static void main(String[] args){
		
		sop(true,"Test satisfiability in CTL* using QuickTab ");
		sop(true,"Enter formula to test.");
		
		String inps=Keyboard.getInput();
		
		if (inps.charAt(0)=='\'') //use a built in example numbered
			inps=bie(inps);
		
        try {

            sop(true,"Test satisfiability in CTL* of "+inps);

            Logic logic=new AUXLogic();

            FormulaTree f=logic.parse(inps);

            sop(true,"parsed to "+f.toString());
            f=logic.disabbreviate(f);

            sop(true,"Disabbreviates to = "+f.inFull());

            sop(true,"which has length = "+f.length());
            
            QuickTab qt=new QuickTab(f);
            
            qt.process();
            
        } catch(ParseException e){
            System.out.println(e.getMessage());       
        }
		
	}
	
	private void process() {
		
		while(!finished){
			System.out.println("At step "+stepNo+" current tableau:"+this);
			System.out.println("Enter instruction (or h to list options)");
			String ui=Keyboard.getInput();
			if (ui.equals("")) {//default input "" finds a rule and does it
				findIncompleteNode(); 
				if (!finished) oneStep();
			}
			else {
				char ins=ui.charAt(0);
				switch(ins){
					case 'h': helplist(); break;
					case 'q': finished=true; break;
					case 's': oneStep(); break; //do one rule here
					case 'e': hueMove(); break; //move right one hue
					case 'f': findHue(); break; //move to an incomplete hue here
					case 'd': moveDown(); break; //go to successor
					case 'u': moveUp(); break; //go to parent
					case 'w': findIncompleteNode(); break; //finds a rule at a node
					case 'l': don(ui); break; //does n steps w/o stopping, eg l50
					case 'x': goAnsw(); break; //keep going till finished
					case 'o': loopDetect(); break; //report on possibilities for loops above
					case 'p': loopUpHere(); break; //user requested loop
					case 'b': userBacktrack(); break; //user requested backtrack
					case 'g': lg(); break; //apply lg
					case 'r': backtrackOnRepetition=true; break; //
					case 'z': dfs=false; break; //
					case 'a': analyze(); break; //
					case 'm': mosaicCheck(); break; //
					default : System.out.println("Unrecognized instruction. Try again.");
				}
			}
		}//end while
		
		System.out.println("Finished. Good bye.");
		
	}

	private void analyze() {
		silent=true;
		int rctr=stepNo+1;

		while ((!finished)){
			findIncompleteNode(); 
			if (!finished) oneStep(false);
			pigeonHole();
			if (stepNo>=rctr){
				silent=false;
				sop(true,"At step "+stepNo+" current tableau:"+this);
				sop(true,""+hueAnalysis());		
				sop(true,""+colourAnalysis());
				sop(true,""+mosaicAnalysis());
				sop(true,"");
				silent=true;
				if (rctr>=1000) rctr+=1000; else rctr=2*rctr;
			}
		}
		silent=false;
		sop(true,"At step "+stepNo+" current tableau:"+this);
	}

	private void userBacktrack() {
		System.out.println("You have requested to abandon this direction and backtrack.");
		System.out.println("BEWARE: The tableau construction may thus fail incorrectly.");
		usedUnjustifiedBacktrack=true;
		backTrackOne(true);		
	}
	
	public boolean loopUpHere(){
		return loopUpHere(true);
	}
	
	public boolean autoLoopUpHere(){
		return loopUpHere(false);
	}
	

	private boolean loopUpHere(boolean wuip) {
		sop(wuip,"You have requested that this node is replaced by an up-link.");
		
		//System.out.println("DEBUG trying loop up from "+currentNode);
		//Keyboard.getInput();
		
		//where to
		int ynode=node[currentNode].getParent();
		int min=node.length;
		int max=-1;
		while (ynode!=-1){
			if (node[currentNode].matches(node[ynode])){
				if (node[currentNode].alreadyTriedUpTo(ynode)){
					sop(true,"Node "+currentNode+" has already tried "+ynode);
				} else {
					sop(true,"Node "+currentNode+" can be replaced by "+ynode);
					if (ynode<min) min=ynode;
					if (ynode>max) max=ynode;
				}
			}
			ynode=node[ynode].getParent();
		}
		
		if (max==-1) {
			System.out.println("No suitable ancestors.");
			return false;
		}
		
		int chx=max;
		
		if (wuip){
			chx=userChooseIntegerBtwn(true,"Choose destination", min,max);
		
			if (!node[currentNode].matches(node[chx])){
				sop(true,"That's not suitable. Looping cancelled.");
				return false;
			}
		} else {
			//use max for now
		}
		
		//check leftmost loop
		boolean leftmost=true;
		int at=currentNode;
		while ((leftmost) && (at!=chx)){
			int parent=node[at].getParent();
			if ((at!=node[parent].getSuc(0))) leftmost=false;
			at=parent;
		}
		if (leftmost){
			if (!allEventsWitnessed(chx,currentNode)){
				node[currentNode].recordTriedUplinkTo(chx); 
				//otherwise we keep trying to do this loop
				sop(true,"Leftmost branch without witnesses. Looping cancelled.");
				return false;
			}
		}
		
		//record it
		node[currentNode].recordTriedUplinkTo(chx);
		//save the qts for later
		extendBTS(new Rule("LOOP",currentNode),2,0);
		
		//now edit the qts
		ynode=node[currentNode].getParent();
		int yh=node[currentNode].getPredHue(0,0);
		node[ynode].setSucc(yh,chx);
		
		//chx gets a new predecessor	
		//use the pre hue map that currentNode had into ynode
		//for the new map that chx has into ynode
		int cniyn=node[currentNode].getIndexOfPred(ynode);
		node[chx].addPredecessor(ynode,node[currentNode].getPreHueMap(cniyn));
		//note that cniyn pre hue map might be too short
		
		
		//null it and all below
		boolean[] pds=properDescendentsOf(currentNode);
		for(int n=0;n<node.length;n++)
			if (pds[n]) node[n]=null;
		node[currentNode]=null;
		

		
		uncheckedLoop=true;
		currentNode=chx;
		currentHue=0;
		return true;
		
	}

	private boolean allEventsWitnessed(int top, int cn) {
    	//check the always zero thread is fulfilling
    	int[] events=getEvents(node[top].getLabel(0));
    	
    	boolean[] seen = new boolean[events.length];
    	int at=cn;
    	while(at!=top){
    		for(int j=0; j<events.length;j++){

    			if (node[at].getLabel(0).member(events[j])) seen[j]=true;   
    		}
    		at=node[at].getParent();
    	}
    	
    	for (int j=0; j<events.length;j++){
    		if (!seen[j]){
    			sop(true,"Loop is NOT allowed: unwitnessed eventuality "+sf.getFormula(events[j]));
    			return false;
    		}
    	}
    	
    	sop(true,"All eventualities witnessed in the always zero thread: so loop is allowed!");

    	return true;
	}

	private int[] getEvents(ClosureSubset sbs) {

	    	int ctr=0;
	    	for(int i=0; i<sf.count(); i++){
	    	
	    	  if ((owner.getLogic()).isEventuality(sf.getFormula(i))){
	    		  if (sbs.member(i)) ctr++;
	    	  }
	    		  
	    	}
	    	
	    	int[] e=new int[ctr];
	    	ctr=0;
	    	for(int i=0; i<sf.count(); i++){
	        	
	      	  if ((owner.getLogic()).isEventuality(sf.getFormula(i))){
	      		  if (sbs.member(i)) e[ctr++]=sf.right(i);
	      	  }
	      		  
	      	}
		return e;
	}

	//array saying if each node is prop desc of cn or not
	private boolean[] properDescendentsOf(int cn) {
		boolean[] inpds=new boolean[node.length];
		for(int h=0;h<node[cn].numHues();h++){
			int sn=node[cn].getSuc(h);
			if (sn>cn)		inpds[sn]=true;
		}
		boolean change=true;
		while (change){
			change=false;
			for(int nn=0;nn<node.length;nn++)
				if ((inpds[nn]) && (node[nn] !=null)){
					for(int h=0;h<node[nn].numHues();h++){
						int j=node[nn].getSuc(h);
						if (( j!=-1) 
								&& (j>nn)
								&& (!inpds[j])){
							inpds[j]=true;
							change=true;
						}
					}
				}
					
		}
		
		return inpds;
	}
	

	private void don(String inp) {
		
		int lctr=100;
		try {
			lctr=Integer.parseInt(inp.substring(1));
		} catch(Exception e){
			
		}
		while ((lctr>0) && (!finished)){
			lctr--;
			findIncompleteNode(); 
			if (!finished) oneStep(false);
			System.out.println("At step "+stepNo+" current tableau:"+this);
		}	
	}
	
	private void goAnsw() {
		silent=true;
		int rctr=stepNo+1;
		long stime=System.currentTimeMillis();
		while ((!finished)){
			findIncompleteNode(); 
			if (!finished) oneStep(false);
			if (stepNo>=rctr){
				silent=false;
				sop(true,"At step "+stepNo+" current tableau:"+this);
				sop(true,"");
				silent=true;
				rctr=2*rctr;
			}
		}
		silent=false;
		long etime=System.currentTimeMillis();
		sop(true,"Elapsed time = "+((etime-stime))+" milliseconds.");
	}

	private void moveUp() {
		int ncn=node[currentNode].getParent();
		if (ncn==-1){
			System.out.println("No way.");
		} else {
			currentHue=node[currentNode].getPredHue(0,currentHue);
			currentNode=ncn;
		}
		
	}

	private void moveDown() {
		int ncn=node[currentNode].getSuc(currentHue);
		if (ncn==-1){
			System.out.println("No way.");
		} else {
			currentNode=ncn;		
			currentHue=0;
		}
		
	}
	
	//finds inconsistency anywhere
	//if found returns true and moves there
	private boolean findInconsistency(){
		if (incons(currentNode,currentHue)) return true;
		
		for(int i=0;i<node[currentNode].numHues();i++){
			if (incons(currentNode,i)) {
				currentHue=i;
				return true;
			}
		}
		
		for(int n=0;n<node.length;n++)
			if (node[n]!=null){
				for(int i=0;i<node[n].numHues();i++){
					if (incons(n,i)) {
						currentNode=n;
						currentHue=i;
						return true;
					}
				}
			}
		return false;
	}
	
	private boolean incons(int n,int h) {
		for(int i=0;i<sf.count();i++)
			if ((node[n].getLabel(h).member(i))){
				
				if (sf.topChar(i)=='-'){
					int msf=sf.left(i);
					if ((node[n].getLabel(h).member(msf))){
						return true;
					} else if (sf.topChar(msf)=='1'){
						return true;
					}
				}
			}
			return false;
	}
	
	//the LG check can only be applied properly
	//if there are no immediate inconsistencies and
	//if all non-leaf nodes are complete
    private boolean canApplyLG(){
    	
    	if (findInconsistency()) return false;
    	
    	for(int i=0;i<node.length;i++){
    		if ((node[i]!=null) && (!node[i].isLeaf())){
    			if (!complete(i)) return false;
    		}
    	}
    	
    	//so it is ok to apply LG but ...
    	
    	//to use LG also have to make sure all nonleaf nodes
    	//have successors for all hues, even if empty
    	for(int i=0;i<node.length;i++){
    		if ((node[i]!=null) && (!node[i].isLeaf())){
    			for(int h=0;h<node[i].numHues();h++)
    				if (node[i].getSuc(h)==-1){
    					addNewNodeBelow(i,h);
    				}
    		}
    	}
    	
    	return true;
    }
    
    //checks that a node is complete
    private boolean complete(int n){
    	boolean complete=true;
    	for(int h=0;h<node[n].numHues();h++)
    		if (!applicableHere(n,h,false).isEmpty()) return false;
    	return true;
    }

	private boolean findHue() {
		
		//System.out.println("DEBG 21");
		
		if (!applicableHere(currentHue,false).isEmpty()) return true;
		
		//System.out.println("DEBG 22");
		
		for(int i=0;i<node[currentNode].numHues();i++){
			//System.out.println("Trying hue "+i);
			ArrayList ar=applicableHere(i,false);
			if (!ar.isEmpty()){
				currentHue=i;
				sop(true,"Move to hue "+i);
				return true;
			}
		}
		
		//System.out.println("DEBG 23");
		
		return false;		
	}
	
	private boolean findIncompleteNode(){
		
		//System.out.println("DEBG 1");
		
		if (findInconsistency()) return true;
		
		//System.out.println("DEBG 2");
		
		if (findHue()) return true;
		
		//System.out.println("DEBG 3");
		
		//attend to non-leaf nodes
		for(int n=0;n<node.length;n++)
		if ((node[n]!= null) && (!node[n].isLeaf())) {
			currentNode=n;
			currentHue=0;
			if (findHue()) {
				if (currentHue==0) sop(true,"Move to hue 0 ");
				sop(true,"...of node "+n);
				return true;
			} else {
				if (hasRedundantHues(n)){
					sop(true,"No redundant hues allowed on completed nonleaf nodes."+n);
				
					//System.out.println("REDUNDANT HUE DETECTED HERE "+stepNo);
					//String s=Keyboard.getInput();
				
				
					backtracking=true;
					return true;
				}
			}
		}	
		//are there any nonleafnodes with redundant hues
		//if so backtrack!

		//System.out.println("DEBG 4");
		
		//if we have looped recently without LG check then do it now
		if (uncheckedLoop){
			if (canApplyLG()){
				sop(true,"Will now apply LG check.");
				
				if (lg()){
					sop(true,"Passed LG check.");
					uncheckedLoop=false;
				} else {
					sop(true,"Failed LG check.");
					sop(true,"Need to backtrack.");
					uncheckedLoop=false;
					backtracking=true;
					return true;
				}
				
			} else {
				System.out.println("Something is not right as can not apply LG check.");
				return false;
			}
		}
		
		//System.out.println("DEBG 5");
		
		//are there any non-leaf nodes which could loop up
		repDetected=false;
		for(int n=0;n<node.length;n++)
			if ((node[n]!= null) && (!node[n].isLeaf())) {
				if (loopDetect(n)){
					sop(true,"Loop possible: move to node "+n);
					currentHue=0;
					return true;
				}
			}
		
		//System.out.println("DEBG 6");
		
		//
		if ((repDetected) && (backtrackOnRepetition)){
			sop(true,"Repetition has not been allowed and has been detected.");
			usedUnjustifiedBacktrack=true;
			sop(true,"Need to backtrack.");
			backtracking=true;
			return true;
		}
		
		//System.out.println("DEBG 7");
		
		//attend to leaf nodes
		//for now to move towards dfs here try one with biggest number
		if (dfs){
		for(int n=node.length-1;n>=0;n--)
		if ((node[n]!= null) && (!node[n].isPseudo()) && (node[n].isLeaf())) {
				currentNode=n;
				currentHue=0;
				if (findHue()) {
					if (currentHue==0) sop(true,"Move to hue 0 ");
					sop(true,"...of node "+n);
					return true;
				}
			}
		
		} else {
		for(int n=0;n<node.length;n++)
			if ((node[n]!= null) && (node[n].isLeaf())) {
					currentNode=n;
					currentHue=0;
					if (findHue()) {
						if (currentHue==0) sop(true,"Move to hue 0 ");
						sop(true,"...of node "+n);
						return true;
					}
				}
		}
		
		//System.out.println("DEBG 8");
		
		sop(true,"There are no incomplete nodes.");
		
		//System.out.println("DEBG 9");
		
		if (canApplyLG()){
			sop(true,"Will now apply LG check.");
			
			if (lg()){
				sop(true,"Passed LG check.");
			} else {
				sop(true,"Failed LG check.");
				sop(true,"Need to backtrack.");
				backtracking=true;
				return true;
			}
			
		} else {
			System.out.println("Something is not right as can not apply LG check.");
			return false;
		}
		
		//System.out.println("DEBG 10");
		
		System.out.println("The tableau is SUCCESSFUL. Took "+stepNo+" steps.");
		finished=true;
		System.out.println("We can conclude the formula is satisfiable.");
		finalResult=1;
		
		return false;
	}

	private boolean hasRedundantHues(int n) {
		
		for(int i=0;i<node[n].numHues();i++)
			for(int j=0;j<node[n].numHues();j++)
			if (j!=i){
				if (node[n].getLabel(i).equals(node[n].getLabel(j))){
					return true;
				}
			}
		return false;
	}

	private void hueMove() {
		currentHue++;
		if (currentHue>=node[currentNode].numHues())
			currentHue=0;
		
	}
	
	private void oneStep(){
		oneStep(true);
	}

	//do one step with or without user input
	private void oneStep(boolean wuip) {		
		if (backtracking)
			backTrackOne(wuip);
		else oneForward(wuip);
	}
		
	private void oneForward(boolean wuip) {
		
		ArrayList ar=prioritize(applicableHere(currentHue,false));
		
		
		int ctr=ar.size();
			
		if ((ctr>0) && ((Rule)(ar.get(0))).getName().equals("XXXX")) {
			sop(true,"IT'S INCONSISTENT. Will backtrack at the next step.");
			stepNo++;
			backtracking=true;
			return;
		}
		
		if ((ctr>0) && ((Rule)(ar.get(0))).getName().equals("BOT")) {
			sop(true,"IT'S INCONSISTENT. Will backtrack at the next step.");
			stepNo++;
			backtracking=true;
			return;
		}
		
		if (ar.isEmpty()){
			if ((!node[currentNode].isLeaf()) && (loopDetect())){
				autoLoopUpHere();
				return;
			} else {
				sop(true,"There are no applicable rules here. You have to move hues.");
				//finished=true;
				return;
			}
		}
		
		display(ar);
		sop(true,"That's all the rules applicable here.");
		
		int rn=userChooseIntegerBtwn(wuip,"Which to apply?",1,ctr);
		
		stepNo++;
		apply(wuip,(Rule)(ar.get(rn-1)));
		
	}
	
	private void display(ArrayList ar) {
		for(int i=0;i<ar.size();i++){
			int sfn=((Rule)ar.get(i)).getSFN();
			String name=((Rule)ar.get(i)).getName();
			String s=" applied here.";
			if ((!name.equals("START") && (!name.equals("PRED")))) 
				s=" applied to "+fms(sfn)+".";
			sop(true,"("+(i+1)+") "+name+""+s);
		}
		
	}

	//arraylist of rules gets ordered
	private ArrayList prioritize(ArrayList a) {
		//1 is inconsistency
		//2 is nonchoice
		//3 is choice
		
		ArrayList pal=new ArrayList();
		
		for(int i=0;i<a.size();i++){
			Rule r=(Rule)(a.get(i));
			if (r.getName().equals("XXXX")) pal.add(r);
			if (r.getName().equals("BOT")) pal.add(r);
		}
		
		for(int i=0;i<a.size();i++){
			Rule r=(Rule)(a.get(i));
			if (r.getName().equals("2NEG")) pal.add(r);
			if (r.getName().equals("NNX")) pal.add(r);
			if (r.getName().equals("NEX")) pal.add(r);
			if (r.getName().equals("CONJ")) pal.add(r);
			if (r.getName().equals("NEC")) pal.add(r);
			if (r.getName().equals("ATOM")) pal.add(r);
			if (r.getName().equals("NAT")) pal.add(r);
		}
		
		for(int i=0;i<a.size();i++){
			Rule r=(Rule)(a.get(i));
			if (r.getName().equals("DIS")) pal.add(r);
			if (r.getName().equals("POS")) pal.add(r);
			if (r.getName().equals("NUN")) pal.add(r);
			if (r.getName().equals("UNT")) pal.add(r);
			if (r.getName().equals("PRED")) pal.add(r);
		}		
		return pal;
	}

	private ArrayList applicableHere(int hue){
		
		return applicableHere(hue,true);
	}
	
	//checks that a hue of the current node has any rules
	private ArrayList applicableHere(int hue,boolean oup){
		return applicableHere(currentNode, hue, oup);
	}
			
	//checks that a hue of the node n has any rules
	private ArrayList applicableHere(int n, int hue,boolean oup){	
		//puts rule XXXX first if found
		
		ArrayList ar=new ArrayList(); //applicable rules
		int ctr=0;
		boolean incons=false;
		
		//System.out.println("DEBG 200");
		
		for(int i=0;i<sf.count();i++){
			if ((node[n].getLabel(hue).member(i))){
				
				//System.out.println("DEBG 201");
				
				if (sf.topChar(i)=='-'){
					int msf=sf.left(i);
					if ((node[n].getLabel(hue).member(msf))){
						ctr++;
						sop(oup,"("+ctr+") XXXX rule may be applied to "+fms(i));  //a and -a in the label
						Rule r=new Rule("XXXX",i);
						ar.add(0,r);
						incons=true;
					} else if (sf.topChar(msf)=='-'){
						int ssf=sf.left(msf);
						if (!(node[n].getLabel(hue).member(ssf))){
							ctr++;
							sop(oup,"("+ctr+") 2NEG rule may be applied to "+fms(i));  //--a in the label
							Rule r=new Rule("2NEG",i);
							ar.add(r);
						} else {}
					} else if (sf.topChar(msf)=='1'){
							ctr++;
							sop(oup,"("+ctr+") BOT rule may be applied to "+fms(i));  //-1 in the label
							Rule r=new Rule("BOT",i);
							ar.add(0,r);
							incons=true;
					} else if (sf.topChar(msf)=='&'){
						int lsf=sf.left(msf);
						int rsf=sf.right(msf);
						if (!(node[n].getLabel(hue).member(sf.negn(lsf)))
								&& !(node[n].getLabel(hue).member(sf.negn(rsf)))){
							ctr++;
							sop(oup,"("+ctr+") DIS rule may be applied to "+fms(i));  //-(a&b) in the label
							Rule r=new Rule("DIS",i);
							ar.add(r);
						} else {}
						
					} else if (sf.topChar(msf)=='X'){
						int ssf=sf.left(msf);
						int nsf=sf.negn(ssf);
						int xnode=node[n].getSuc(hue);
						
						boolean appnex=false;
						if ((xnode==-1) || (!node[xnode].getLabel(0).member(nsf))){
							appnex=true;
						} else {
							//also look in other hues who have this as a predecessor
							for(int bro=0;bro<node[n].numHues();bro++){
								int ynode=node[n].getSuc(bro);
								if ((ynode!=-1) && (!node[ynode].isPseudo())){
									int tnipn=node[ynode].getIndexOfPred(n);
									for(int ph=0;ph<node[ynode].numHues();ph++)
										if (node[ynode].getPredHue(tnipn,ph)==hue){
										  if (!node[ynode].getLabel(ph).member(nsf)) appnex=true;
									}
								}
							}
						}
						if (appnex){
							ctr++;
							sop(oup,"("+ctr+") NNX rule may be applied to "+fms(i));  //-(a&b) in the label
							Rule r=new Rule("NNX",i);
							ar.add(r);
						} else {}

					} else if (sf.topChar(msf)=='U'){
						int lsf=sf.left(msf);
						int rsf=sf.right(msf);
						int lnf=sf.negn(lsf);
						int rnf=sf.negn(rsf);
						
						//System.out.println("DEBG 205");
						
						boolean appunt=false;
						if ((!(node[n].getLabel(hue).member(rnf)))) {
							appunt=true;
						}
						else if ((!(node[n].getLabel(hue).member(lnf)))) {
								int xnode=node[n].getSuc(hue);
								if ((xnode==-1) || (!node[xnode].getLabel(0).member(i))){
									appunt=true;
								} else {
									//also look in other hues who have this as a predecessor
									for(int bro=0;bro<node[n].numHues();bro++){
										int ynode=node[n].getSuc(bro);
										if ((ynode!=-1) && (!node[ynode].isPseudo())){
											int tnipn=node[ynode].getIndexOfPred(n);
											for(int ph=0;ph<node[ynode].numHues();ph++)
												if (node[ynode].getPredHue(tnipn,ph)==hue){
												  if (!node[ynode].getLabel(ph).member(i)) {
													  appunt=true;
												  }
											}
										
									}
								}
								
							}
						}
						if (appunt){
								ctr++;
								sop(oup,"("+ctr+") NUN rule may be applied to "+fms(i));  // in the label
								Rule r=new Rule("NUN",i);
								ar.add(r);
						}
						
						//System.out.println("DEBG 206");
						
					} else if (sf.topChar(msf)=='A'){
						int ssf=sf.left(msf);
						int nsf=sf.negn(ssf);
						boolean foundnsf=false;
						for(int j=0;j<node[n].numHues();j++){
							if (node[n].getLabel(j).member(nsf)) foundnsf=true;
						}
						if (!foundnsf){
							ctr++;
							sop(oup,"("+ctr+") POS rule may be applied to "+fms(i));  //-Aa in the label
							Rule r=new Rule("POS",i);
							ar.add(r);
						} else {}

					} else if (((AUXLogic)owner.getLogic()).isAtom(sf.topChar(msf))){
						
						boolean missing=false;
						for(int j=0;j<node[n].numHues();j++){
							if (!node[n].getLabel(j).member(i)) missing=true;
						}
						if (missing){
								ctr++;
								sop(oup,"("+ctr+") NAT rule may be applied to "+fms(i));  //p in the label
								Rule r=new Rule("NAT",i);
								ar.add(r);
						} else {}
						
					} else {}	
					
				//end of cases nested under a negation
					
				}   else if (sf.topChar(i)=='&'){
					int lsf=sf.left(i);
					int rsf=sf.right(i);
					if ((!(node[n].getLabel(hue).member(lsf)))
						|| (!(node[n].getLabel(hue).member(rsf)))){
						ctr++;
						sop(oup,"("+ctr+") CONJ rule may be applied to "+fms(i));  //a&b in the label
						Rule r=new Rule("CONJ",i);
						ar.add(r);
					} else {}
				}  else if (sf.topChar(i)=='A'){
						int nf=sf.left(i);
						boolean foundnnf=false;
						for(int j=0;j<node[n].numHues();j++){
							if (!node[n].getLabel(j).member(nf)) foundnnf=true;
						}
						if (foundnnf){
								ctr++;
								sop(oup,"("+ctr+") NEC rule may be applied to "+fms(i));  //Aa in the label
								Rule r=new Rule("NEC",i);
								ar.add(r);
						} else {}
				}	else if (sf.topChar(i)=='X'){
						int xf=sf.left(i);
						int xnode=node[n].getSuc(hue);
						boolean appnex=false;
						if ((xnode==-1) || (!node[xnode].getLabel(0).member(xf))){
							appnex=true;
						} else {
							//also look in other hues who have this as a predecessor
							for(int bro=0;bro<node[n].numHues();bro++){
								int ynode=node[n].getSuc(bro);
								if ((ynode!=-1) && (!node[ynode].isPseudo())){
									int tnipn=node[ynode].getIndexOfPred(n);
									for(int ph=0;ph<node[ynode].numHues();ph++)
										if (node[ynode].getPredHue(tnipn,ph)==hue){
										  if (!node[ynode].getLabel(ph).member(xf)) appnex=true;
									}
								}
							}
						}
						if (appnex){
							ctr++;
							sop(oup,"("+ctr+") NEX rule may be applied to "+fms(i));  //Xa in the label
							Rule r=new Rule("NEX",i);
							ar.add(r);
						}
				} 	else if (sf.topChar(i)=='U'){
						int lsf=sf.left(i);
						int rsf=sf.right(i);
						if ((!(node[n].getLabel(hue).member(rsf)))){
							boolean appunt=false;
							if ((!(node[n].getLabel(hue).member(lsf)))) appunt=true;
							else {
								int xnode=node[n].getSuc(hue);
								if ((xnode==-1) || (!node[xnode].getLabel(0).member(i))){
									appunt=true;
								} else {
									//also look in other hues who have this as a predecessor
									for(int bro=0;bro<node[n].numHues();bro++){
										int ynode=node[n].getSuc(bro);
										if ((ynode!=-1) && (!node[ynode].isPseudo())){
											int tnipn=node[ynode].getIndexOfPred(n);
											for(int ph=0;ph<node[ynode].numHues();ph++)
												if (node[ynode].getPredHue(tnipn,ph)==hue){
												  if (!node[ynode].getLabel(ph).member(i)) appunt=true;
											}
										}
									}
								}
								
							}
							if (appunt){
								ctr++;
								sop(oup,"("+ctr+") UNT rule may be applied to "+fms(i));  //Xa in the label
								Rule r=new Rule("UNT",i);
								ar.add(r);
							}
							
						}
						
				} 	else if (((AUXLogic)owner.getLogic()).isAtom(sf.topChar(i))){
					
					boolean missing=false;
					for(int j=0;j<node[n].numHues();j++){
						if (!node[n].getLabel(j).member(i)) missing=true;
					}
					if (missing){
							ctr++;
							sop(oup,"("+ctr+") ATOM rule may be applied to "+fms(i));  //p in the label
							Rule r=new Rule("ATOM",i);
							ar.add(r);
					} else {}
				}

				
			}//end if sf i appears
		}//end for i
		
		//System.out.println("DEBG 202");
		
		//this rule may need to be changed when loops occur
		if ((node[n].getParent()!=-1) && (node[n].getPredHue(0,hue)==-1)){
			ctr++;
			sop(oup,"("+ctr+") PRED rule may be applied here. ");  
			Rule r=new Rule("PRED",0);
			ar.add(r);
		}
		
		//System.out.println("DEBG 203");
		
		//pre for loop/rep detection
		int ynode=node[n].getParent();
		while (ynode!=-1){
			if (node[n].matches(node[ynode])){
				sop(oup,"LATER: loop rule may be applied here. ");
				sop(oup,"Node "+n+" up to "+ynode);
			}
			ynode=node[ynode].getParent();
		}
		
		//System.out.println("DEBG 204");
		
		return ar;
	}//end method
		
	
	private boolean loopDetect(){
		return loopDetect(currentNode);
	}
	
	//should only be used for non-leaves
	private boolean loopDetect(int nn){
		
		int reps=0;
		int ynode=node[nn].getParent();
		while (ynode!=-1){
			if (node[nn].matches(node[ynode])){

				if (node[nn].alreadyTriedUpTo(ynode)){
					sop(true,"Node "+nn+" already tried up to "+ynode);
					reps++;
				} else {
					sop(true,"Loop rule may be applied here. ");
					sop(true,"Node "+nn+" up to "+ynode);
					
					currentNode=nn;
					return true;
				}
			}
			ynode=node[ynode].getParent();
		}
		if (reps>=maxrepeats) repDetected=true;
		return false;
	}


	private static void sop(boolean oup, String string) {
		if (silent) return;
		if (oup) System.out.println(string);
	}

	private void backTrackOne(boolean wuip) {
		
		stepNo++;
		
		if (bts.isEmpty()){
			System.out.println("Backtracked to before first choice point.");
			finished=true;
			System.out.println("Tableau construction FAILED after "+stepNo+" steps.");
			
			if (usedUnjustifiedBacktrack){
				System.out.println("However we can not conclude anything definite.");
				System.out.println("In this process, we used an unsafe/unjustified step.");
				finalResult=3;
			} else {
				System.out.println("We can conclude the formula is unsatisfiable.");
				finalResult=2;
			}
			
			return;
		}
		
		QuickTab rqt=(QuickTab) bts.peek1();
		Rule rule=bts.peek2();
		boolean[] ba=bts.peek3();
		//int oldStep=bts.peek4();
		
		
		reverseDeepCopy(rqt);
		
		sop(true,"Backtracked to previous choice point.");
		sop(true,"Current tableau:"+this);
		
		backtracking=false;
		
		if (!choice(wuip,rule,ba)) { //no more choices here, go back further
			sop(true,"No more choices here. Will backtrack further at the next step.");
			backtracking=true;
			return;
		}
		
		
	}

	private void reverseDeepCopy(QuickTab rqt) {
		
		currentNode=rqt.currentNode;
		currentHue=rqt.currentHue;
		
		node=new Node[rqt.node.length];
		for(int i=0;i<node.length;i++)
			if (rqt.node[i] != null)
				node[i]=rqt.node[i].deepCopy(owner);
			else node[i]=null;
		
		latestEarlyHue=rqt.latestEarlyHue;
		
	}

	private void apply(boolean wuip, Rule rule) {
		//System.out.println("DEBUG applying "+rule.getName());
		
		if (rule.getName().equals("XXXX")){
			int obj=rule.getSFN();
			int msf=sf.left(obj);
			return;
		} else if (rule.getName().equals("CONJ")){
			int obj=rule.getSFN();
			int lsf=sf.left(obj);
			int rsf=sf.right(obj);
			node[currentNode].getLabel(currentHue).add(lsf);
			node[currentNode].getLabel(currentHue).add(rsf);
		} else if (rule.getName().equals("2NEG")){
			int obj=rule.getSFN();
			int lsf=sf.left(obj);
			int ssf=sf.left(lsf);
			node[currentNode].getLabel(currentHue).add(ssf);
		} else if (rule.getName().equals("DIS")){
			//-(a&b) is in the hue
			//we have already checked that neither -a and -b are in already
			int obj=rule.getSFN(); //-(a&b)
			int msf=sf.left(obj); //a&b
			int lsf=sf.left(msf); //a
			int rsf=sf.right(msf); //b
			int lnf=sf.negn(lsf); //-a
			int rnf=sf.negn(rsf); //-b
			
			//several cases depending what is already in this hue
			if ((node[currentNode].getLabel(currentHue).member(lsf))
					&& (!node[currentNode].getLabel(currentHue).member(rsf))){ //a in, b not
				node[currentNode].getLabel(currentHue).add(rnf); //add -b
				sop(true,"DIS: only choice is to add "+fms(rnf));
			} else if ((!node[currentNode].getLabel(currentHue).member(lsf))
					&& (node[currentNode].getLabel(currentHue).member(rsf))){ //a not, b in
				node[currentNode].getLabel(currentHue).add(lnf); //add -a
				sop(true,"DIS: only choice is to add "+fms(lnf));
			} else if ((node[currentNode].getLabel(currentHue).member(lsf))
					&& (node[currentNode].getLabel(currentHue).member(rsf))){ //a,b both in
				sop(true,"DIS detects a contradiction.");
				backtracking=true;
				return;
			} else { //a, b not in
			
				boolean[] ff=new boolean[2];
				ff[0]=false; //put -a in
				ff[1]=false; //put a and -b in
				//ff[2]=false; //do both in separate hues
				choice(wuip,rule,ff);
			}
			
		} else if (rule.getName().equals("POS")){
						
			boolean[] ff=new boolean[node[currentNode].numHues()+1];
			for(int i=0;i<ff.length;i++) ff[i]=false;
			choice(wuip,rule,ff);
					
		} else if (rule.getName().equals("NEC")){
			int obj=rule.getSFN();
			int lsf=sf.left(obj);
			for(int j=0;j<node[currentNode].numHues();j++)
				if (!node[currentNode].getLabel(j).member(lsf))
					node[currentNode].getLabel(j).add(lsf);
			
		}  else if (rule.getName().equals("NEX")){
			int obj=rule.getSFN(); //Xa
			int msf=sf.left(obj);  //a
			int snn=node[currentNode].getSuc(currentHue);	
			
			if (snn==-1){
				//need to add a new successor node for the current hue
				int nnn=addNewNodeBelow(currentNode,currentHue);
				node[nnn].putInZeroHue(msf);
				
			} else if (!node[snn].getLabel(0).member(msf)){
				//need to put msf in hue0
				node[snn].putInZeroHue(msf);
				
			} 

			//also check other hues who have this as a predecessor
			for(int bro=0;bro<node[currentNode].numHues();bro++){
				int ynode=node[currentNode].getSuc(bro);
				if ((ynode!=-1) && (!node[ynode].isPseudo())){
					int tnipn=node[ynode].getIndexOfPred(currentNode);
					for(int ph=0;ph<node[ynode].numHues();ph++)
						if (node[ynode].getPredHue(tnipn,ph)==currentHue){
						  if (!node[ynode].getLabel(ph).member(msf)) 
							  node[ynode].putInHue(ph,msf);
					}
				}
			}

		}  else if (rule.getName().equals("NNX")){
			int obj=rule.getSFN();
			int msf=sf.left(obj);
			int ssf=sf.left(msf);
			int nsf=sf.negn(ssf);
			int snn=node[currentNode].getSuc(currentHue);	
			
			if (snn==-1){
				//need to add a new successor node for the current hue
				int nnn=addNewNodeBelow(currentNode,currentHue);
				node[nnn].putInZeroHue(nsf);
				
			} else if (!node[snn].getLabel(0).member(nsf)){
				//need to put msf in hue0
				node[snn].putInZeroHue(nsf);
			}
				
			//also check other hues who have this as a predecessor
			for(int bro=0;bro<node[currentNode].numHues();bro++){
					int ynode=node[currentNode].getSuc(bro);
					if ((ynode!=-1) && (!node[ynode].isPseudo())){
						int tnipn=node[ynode].getIndexOfPred(currentNode);
						for(int ph=0;ph<node[ynode].numHues();ph++)
							if (node[ynode].getPredHue(tnipn,ph)==currentHue){
							  if (!node[ynode].getLabel(ph).member(nsf)) 
								  node[ynode].putInHue(ph,nsf);
						}
					}
			}
			
		} else if (rule.getName().equals("PRED")){
			
			int predNodeNum=node[currentNode].getParent();
			boolean[] ff=new boolean[node[predNodeNum].numHues()+1];
			for(int i=0;i<ff.length;i++) ff[i]=false;
			choice(wuip,rule,ff);

		} else if (rule.getName().equals("UNT")){
			
			//U(a&b) is in the hue
			//we have already checked that b not in and ( a not in or aUb not in succ) already
			int obj=rule.getSFN(); //aUb
			int nobj=sf.negn(obj); //-(aUb)
			int lsf=sf.left(obj); //a
			int rsf=sf.right(obj); //b
			int lnf=sf.negn(lsf); //-a
			int rnf=sf.negn(rsf); //-b
			int snn=node[currentNode].getSuc(currentHue);	
			
			boolean binh=node[currentNode].getLabel(currentHue).member(rsf);
			boolean nbinh=node[currentNode].getLabel(currentHue).member(rnf);
			boolean ainh=node[currentNode].getLabel(currentHue).member(lsf);
			boolean nainh=node[currentNode].getLabel(currentHue).member(lnf);
			boolean aUbinsucc=(snn!=-1) && (node[snn].getLabel(0).member(obj));
			boolean naUbinsucc=(snn!=-1) && (node[snn].getLabel(0).member(nobj));
			
			//we just want to put b in h or (put a,-b in h and aUb in succ)
			
			//several cases depending what is already in this hue
			if (nbinh && ((nainh) || (naUbinsucc))){ 
				//-b in h and (-a in h or -aUb in succ)
				sop(true,"UNT detects a contradiction.");
				backtracking=true;
				return;
			} else if (nbinh){ //-b in h
				node[currentNode].getLabel(currentHue).add(lsf); //add a
				if (snn==-1){ //add aUb in succ
					int nnn=addNewNodeBelow(currentNode,currentHue);
					node[nnn].getLabel(0).add(obj);
				} else {
					node[snn].getLabel(0).add(obj);
				}
				
				//also other hues who have this as pred
				//also check other hues who have this as a predecessor
				for(int bro=0;bro<node[currentNode].numHues();bro++){
					int ynode=node[currentNode].getSuc(bro);
					if ((ynode!=-1) && (!node[ynode].isPseudo())){
						int tnipn=node[ynode].getIndexOfPred(currentNode);
						for(int ph=0;ph<node[ynode].numHues();ph++)
							if (node[ynode].getPredHue(tnipn,ph)==currentHue){
							  if (!node[ynode].getLabel(ph).member(obj)) 
								  node[ynode].putInHue(ph,obj);
						}
					}
				}
				
			} else if ((nainh) || (naUbinsucc)){
				node[currentNode].getLabel(currentHue).add(rsf); //add b
			} else {
		
				//in this case there is a choice of actions
				//put b in h
				//put -b in h, a in h, aUb in succ
				
				boolean[] ff=new boolean[2];
				ff[0]=false;
				ff[1]=false;
				choice(wuip,rule,ff);
			}
			
				
		} else if (rule.getName().equals("NUN")){
			//-(aUb) is in the hue
			//we have already checked that -b not in or ( -a not in and -(aUb) not in succ) already
			int obj=rule.getSFN(); //-aUb
			int nobj=sf.left(obj); //aUb
			int lsf=sf.left(nobj); //a
			int rsf=sf.right(nobj); //b
			int lnf=sf.negn(lsf); //-a
			int rnf=sf.negn(rsf); //-b
			int snn=node[currentNode].getSuc(currentHue);	
			
			boolean binh=node[currentNode].getLabel(currentHue).member(rsf);
			boolean nbinh=node[currentNode].getLabel(currentHue).member(rnf);
			boolean ainh=node[currentNode].getLabel(currentHue).member(lsf);
			boolean nainh=node[currentNode].getLabel(currentHue).member(lnf);
			boolean aUbinsucc=(snn!=-1) && (node[snn].getLabel(0).member(nobj));
			boolean naUbinsucc=(snn!=-1) && (node[snn].getLabel(0).member(obj));
			
			//we just want to put -a, -b in h or (a,-b in h and -(aUb) in succ))
			
			//several cases depending what is already in this hue
			if (binh){
				sop(true,"NUN detects a contradiction.");
				backtracking=true;
				return;
			} else if (ainh && aUbinsucc){ 
				//a in h and aUb in succ
				sop(true,"NUN detects a contradiction.");
				backtracking=true;
				return;
			} else if (!ainh && !nainh && aUbinsucc){ 
				node[currentNode].getLabel(currentHue).add(lnf); //add -a
				node[currentNode].getLabel(currentHue).add(rnf);
				
				//add naUb to other succs
				for(int bro=0;bro<node[currentNode].numHues();bro++){
					int ynode=node[currentNode].getSuc(bro);
					if ((ynode!=-1) && (!node[ynode].isPseudo())){
						int tnipn=node[ynode].getIndexOfPred(currentNode);
						for(int ph=0;ph<node[ynode].numHues();ph++)
							if (node[ynode].getPredHue(tnipn,ph)==currentHue){
							  if (!node[ynode].getLabel(ph).member(obj)) 
								  node[ynode].putInHue(ph,obj);
						}
					}
				}
				
			} else if (ainh && !aUbinsucc && !naUbinsucc){ 
					//add -(aUb) to succ
				node[currentNode].getLabel(currentHue).add(rnf);
				if (snn==-1){ 
					int nnn=addNewNodeBelow(currentNode,currentHue);
					node[nnn].getLabel(0).add(obj);
				} else {
					node[snn].getLabel(0).add(obj);
				}
				
				//add naUb to other succs
				for(int bro=0;bro<node[currentNode].numHues();bro++){
					int ynode=node[currentNode].getSuc(bro);
					if ((ynode!=-1) && (!node[ynode].isPseudo())){
						int tnipn=node[ynode].getIndexOfPred(currentNode);
						for(int ph=0;ph<node[ynode].numHues();ph++)
							if (node[ynode].getPredHue(tnipn,ph)==currentHue){
							  if (!node[ynode].getLabel(ph).member(obj)) 
								  node[ynode].putInHue(ph,obj);
						}
					}
				}
				
				
			} else if (ainh && !aUbinsucc){ 
				
				//add naUb to other succs
				for(int bro=0;bro<node[currentNode].numHues();bro++){
					int ynode=node[currentNode].getSuc(bro);
					if ((ynode!=-1) && (!node[ynode].isPseudo())){
						int tnipn=node[ynode].getIndexOfPred(currentNode);
						for(int ph=0;ph<node[ynode].numHues();ph++)
							if (node[ynode].getPredHue(tnipn,ph)==currentHue){
							  if (!node[ynode].getLabel(ph).member(obj)) 
								  node[ynode].putInHue(ph,obj);
						}
					}
				}
				
				
			} else if (sf.topChar(lsf)=='1'){ //a=1
		
				//in this case put
				//a,-b in h and -(aUb) in succ
				node[currentNode].getLabel(currentHue).add(lsf);
				node[currentNode].getLabel(currentHue).add(rnf);
				if (snn==-1){ 
					int nnn=addNewNodeBelow(currentNode,currentHue);
					node[nnn].getLabel(0).add(obj);
				} else {
					node[snn].getLabel(0).add(obj);
				}
				
				//add naUb to other succs
				for(int bro=0;bro<node[currentNode].numHues();bro++){
					int ynode=node[currentNode].getSuc(bro);
					if ((ynode!=-1) && (!node[ynode].isPseudo())){
						int tnipn=node[ynode].getIndexOfPred(currentNode);
						for(int ph=0;ph<node[ynode].numHues();ph++)
							if (node[ynode].getPredHue(tnipn,ph)==currentHue){
							  if (!node[ynode].getLabel(ph).member(obj)) 
								  node[ynode].putInHue(ph,obj);
						}
					}
				}
				
			} else if (!ainh && !nainh && !aUbinsucc && !naUbinsucc){ 
		
				//in this case there is a choice of actions
				//put -a, -b in h
				//put a,-b in h and -(aUb) in succ
				
				
				boolean[] ff=new boolean[2];
				ff[0]=false;
				ff[1]=false;
				
				choice(wuip,rule,ff);
				
			} else if(!nbinh){
				node[currentNode].getLabel(currentHue).add(rnf); //just add -b
			}
			
		} else if (rule.getName().equals("ATOM")){
			int obj=rule.getSFN();
			for(int j=0;j<node[currentNode].numHues();j++)
				if (!node[currentNode].getLabel(j).member(obj))
					node[currentNode].getLabel(j).add(obj);
				
		} else if (rule.getName().equals("NAT")){
			int obj=rule.getSFN();
			for(int j=0;j<node[currentNode].numHues();j++)
				if (!node[currentNode].getLabel(j).member(obj))
					node[currentNode].getLabel(j).add(obj);
				
		} else {}
		
	}

	private int addNewNodeBelow(int pnode, int phue) {
		int nh=node.length;
		Node[] nnode=new Node[nh+1];
		for(int i=0;i<nh;i++) nnode[i]=node[i];
		nnode[nh]=new Node(owner,nh,pnode,phue);
		node=nnode;
		
		node[pnode].addSucc(phue,nh);
		return nh;
	}

	private boolean choice(boolean wuip, Rule rule, boolean[] ff) {
		//updates the qt and bts after offering the user a choice within a rule
		//returns true if tableau can continue
		
		String rname=rule.getName();
		int obj=rule.getSFN();
		
		if (rname.equals("DIS")){
			
			sop(true,"Applying DIS to "+fms(obj));
			
			int cjf=sf.left(obj);
			int df1=sf.left(cjf);
			int nf1=sf.negn(df1);
			int df2=sf.right(cjf);
			int nf2=sf.negn(df2);
			
			//ff[0] put -a in
			//ff[1] put a and -b in
			
			sop(true,"Choice (0) is to add "+fms(nf1));
			sop(true,"Choice (1) is to add "+fms(df1)+" and "+fms(nf2));
			
			sop(true,"The following choices have not been tried yet.");
			
			int numch=2;
			
			int ctr=0;
			int min=numch;
			for(int i=0;i<numch;i++)
				if (!ff[i]) {
					ctr++;
					sop(true,"Choice "+(i));
					if (i<min) min=i;
				}
			
			if (ctr==0){
				sop(true,"All choices have been tried so now we have to backtrack");
				bts.pop();
				return false;
			}
			
			int chx=-1;
			while ((chx<min) || (ff[chx])){
				chx=userChooseIntegerBtwn(wuip,"You choose.",min,numch-1);
			}
			
			if (ctr==numch) extendBTS(rule,numch,chx);
			else updateBTS(rule,chx);
			
			if (chx==0){		
				node[currentNode].getLabel(currentHue).add(nf1);						
			} else {				
				node[currentNode].getLabel(currentHue).add(nf2);
				node[currentNode].getLabel(currentHue).add(df1);
			} 		
			
		}
		else if (rname.equals("POS")){
				
				sop(true,"Applying POS to "+fms(obj));
				
				int cjf=sf.left(obj);//Aa
				int df=sf.left(cjf); //a
				int nf=sf.negn(df); //-a
				
				int numch=node[currentNode].numHues()+1;
				sop(true,"There are "+numch+" choices.");
				sop(true,"Choices 0 to "+(numch-2)+" is to add "+fms(nf)+" to that hue");
				sop(true,"Choice "+(numch-1)+" is to add "+fms(nf)+" to a new hue");
				sop(true,"Also "+fms(df)+"will be added to all hues to the left.");
				
				sop(true,"The following choices have not been tried yet.");
				int ctr=0;
				int min=numch;
				for(int i=0;i<numch;i++)
					if (!ff[i]) {
						ctr++;
						sop(true,"Choice "+i);
						if (i<min) min=i;
					}
				
				if (ctr==0){
					sop(true,"All choices have been tried so now we have to backtrack");
					bts.pop();
					return false;
				}
				
				int chx=-1;
				while ((chx<min) || (ff[chx])){
					chx=userChooseIntegerBtwn(wuip,"You choose.",min,numch-1);
				}
				
				if (ctr==numch) extendBTS(rule,numch,chx);
				else updateBTS(rule,chx);
				
				if (chx<numch-1){
					node[currentNode].getLabel(chx).add(nf);
					for(int lh=0;lh<chx;lh++)
						node[currentNode].getLabel(lh).add(df);
					//currentHue=chx;
				} else {
					
					int newHue=addNewHueHere();
					node[currentNode].getLabel(newHue).add(nf);
					for(int lh=0;lh<newHue;lh++)
						node[currentNode].getLabel(lh).add(df);
					//currentHue=newHue;
				}
				
		} else if (rname.equals("PRED")){
			
			sop(true,"Applying PRED here.");
			
			int predNodeNum=node[currentNode].getParent();
			
			int numch=node[predNodeNum].numHues()+1;
			sop(true,"Predecessor node is "+predNodeNum+" which now has "+(numch-1)+" hues.");
			sop(true,"There are "+numch+" choices.");
			sop(true,"Choices 0 to "+(numch-2)+" is map back to that hue in the predecessor.");
			sop(true,"Choice "+(numch-1)+" is to go to a new hue there.");
			
			sop(true,"The following choices have not been tried yet.");
			int ctr=0;
			int min=numch;
			for(int i=0;i<numch;i++)
				if (!ff[i]) {
					ctr++;
					sop(true,"Choice "+i);
					if (i<min) min=i;
				}
			
			if (ctr==0){
				sop(true,"All choices have been tried so now we have to backtrack");
				bts.pop();
				return false;
			}
			
			
			int chx=-1;
			while ((chx<min) || (ff[chx])){
				chx=userChooseIntegerBtwn(wuip,"You choose.",min,numch-1);
			}
			
			if (ctr==numch) extendBTS(rule,numch,chx);
			else updateBTS(rule,chx);
			
			if (chx<numch-1){
				node[currentNode].makePreHue(currentHue,0,chx);
				currentNode=predNodeNum;
				currentHue=chx;
			} else {
				int proxy=addNewNode(new PseudoProxy(currentNode,currentHue));
				int predHueNum=node[predNodeNum].addNewHue();
				node[currentNode].makePreHue(currentHue,0,predHueNum);
				node[proxy].setPreds(predHueNum,predHueNum);
				node[predNodeNum].setSucc(predHueNum, proxy);
				currentNode=predNodeNum;
				currentHue=predHueNum;
			}
			
		} else if (rname.equals("UNT")){
			
			sop(true,"Applying UNT to "+fms(obj));
			
			int lsf=sf.left(obj);
			int rsf=sf.right(obj);
			int rnf=sf.negn(rsf);
			
			//in this case there is a choice of three actions
			//0 put b in h
			//1 put -b in h, a in h, aUb in succ
			
			sop(true,"Choice (0) is to add "+fms(rsf));
			sop(true,"Choice (1) is to add "+fms(lsf)+", "
					+fms(rnf)+" and adjust successors.");
			
			sop(true,"The following choices have not been tried yet.");
			
			int numch=2;
			
			int ctr=0;
			int min=numch;
			for(int i=0;i<numch;i++)
				if (!ff[i]) {
					ctr++;
					sop(true,"Choice "+(i));
					if (i<min) min=i;
				}
			
			if (ctr==0){
				sop(true,"All choices have been tried so now we have to backtrack");
				bts.pop();
				return false;
			}
			
			int chx=-1;
			while ((chx<min) || (ff[chx])){
				chx=userChooseIntegerBtwn(wuip,"You choose.",min,numch-1);
			}
			
			if (ctr==numch) extendBTS(rule,numch,chx);
			else updateBTS(rule,chx);
			
			if (chx==0){			
				node[currentNode].getLabel(currentHue).add(rsf);				
			} else if (chx==1) {			
				node[currentNode].getLabel(currentHue).add(lsf);
				node[currentNode].getLabel(currentHue).add(rnf);
				
				int snn=node[currentNode].getSuc(currentHue);	
				
				if (snn==-1){
					//need to add a new successor node for the current hue
					int nnn=addNewNodeBelow(currentNode,currentHue);
					node[nnn].putInZeroHue(obj);
					
				} else if (!node[snn].getLabel(0).member(obj)){
					//need to put obj in hue0
					node[snn].putInZeroHue(obj);
					
				} 
				
				//also check other hues who have this as a predecessor
				for(int bro=0;bro<node[currentNode].numHues();bro++){
					int ynode=node[currentNode].getSuc(bro);
					if ((ynode!=-1) && (!node[ynode].isPseudo())){
						int tnipn=node[ynode].getIndexOfPred(currentNode);
						for(int ph=0;ph<node[ynode].numHues();ph++)
							if (node[ynode].getPredHue(tnipn,ph)==currentHue){
							  if (!node[ynode].getLabel(ph).member(obj)) 
								  node[ynode].putInHue(ph,obj);
						}
					}
				}
			}
			
		} else if (rname.equals("NUN")){
			sop(true,"Applying NUN to "+fms(obj));
			
			int usf=sf.left(obj); //aUb
			int lsf=sf.left(usf); //a
			int rsf=sf.right(usf); //b
			int lnf=sf.negn(lsf);  //-a
			int rnf=sf.negn(rsf);  //-b
			
			//in this case there is a choice of three actions
			//0 put -a, -b in h
			//1 put a,-b in h and -(aUb) in succ
			
			sop(true,"Choice (0) is to add "+fms(lnf)
					+" and add "+fms(rnf));
			sop(true,"Choice (1) is to add "+fms(lsf)+" and "
					+fms(rnf)+" and add "+fms(obj)+" to successor.");
			
			sop(true,"The following choices have not been tried yet.");
			
			int numch=2;
			
			int ctr=0;
			int min=numch;
			for(int i=0;i<numch;i++)
				if (!ff[i]) {
					ctr++;
					sop(true,"Choice "+(i));
					if (i<min) min=i;
				}
			
			if (ctr==0){
				sop(true,"All choices have been tried so now we have to backtrack");
				bts.pop();
				return false;
			}
			
			int chx=-1;
			while ((chx<min) || (ff[chx])){
				chx=userChooseIntegerBtwn(wuip,"You choose.",min,numch-1);
			}
			
			if (ctr==numch) extendBTS(rule,numch,chx);
			else updateBTS(rule,chx);
				
			
			if (chx==0){	
				//0 put -a, -b in h
				node[currentNode].getLabel(currentHue).add(lnf);		
				node[currentNode].getLabel(currentHue).add(rnf);	
			} else {	
				//1 put a,-b in h and -(aUb) in succ
				node[currentNode].getLabel(currentHue).add(lsf);
				node[currentNode].getLabel(currentHue).add(rnf);
				
				int snn=node[currentNode].getSuc(currentHue);	
				
				if (snn==-1){
					//need to add a new successor node for the current hue
					int nnn=addNewNodeBelow(currentNode,currentHue);
					node[nnn].putInZeroHue(obj);
					
				} else if (!node[snn].getLabel(0).member(obj)){
					//need to put obj in hue0
					node[snn].putInZeroHue(obj);
					
				} 
				
				//also check other hues who have this as a predecessor
				for(int bro=0;bro<node[currentNode].numHues();bro++){
					int ynode=node[currentNode].getSuc(bro);
					if ((ynode!=-1) && (!node[ynode].isPseudo())){
						int tnipn=node[ynode].getIndexOfPred(currentNode);
						for(int ph=0;ph<node[ynode].numHues();ph++)
							if (node[ynode].getPredHue(tnipn,ph)==currentHue){
							  if (!node[ynode].getLabel(ph).member(obj)) 
								  node[ynode].putInHue(ph,obj);
						}
					}
				}
				
			} 
			
		} if (rname.equals("LOOP")){
			
			sop(true,"Applying LOOP");
			
			if (!(ff[0])){//loop choice has not been used ???			
			} else if (ff[1]){
				sop(true,"Both looping and not have been tried so now we have to backtrack");
				bts.pop();
				return false;
				
			} else if (!ff[1]){
				
				sop(true,"Looping has been tried so now we just continue.");
				updateBTS(rule,1);
				
			} 
		}
			
		
		return true;
		
	}


	private int addNewNode(PseudoProxy pp) {
		int nh=node.length;
		Node[] nnode=new Node[nh+1];
		for(int i=0;i<nh;i++) nnode[i]=node[i];
		nnode[nh]=new Node(owner,nh,pp);
		node=nnode;
		
		return nh;
	}

	private int addNewHueHere() {
		// TODO Auto-generated method stub
		return node[currentNode].addNewHue();
	}

	private void updateBTS(Rule rule, int chx) {
       boolean[] uc = bts.peek3();
       uc[chx]=true;
       bts.modify3(uc);
		
	}
	
	private void extendBTS(Rule rule, int numch, int firstChoice){
		
		//System.out.println("EXTENDING BTS from "+bts);
		
		boolean[] uc=new boolean[numch];
		uc[firstChoice]=true;
		
		QuickTab cqt=deepCopy();
		bts.add(cqt,rule,uc);
		
		//System.out.println("new BTS is "+bts);
	}

	private QuickTab deepCopy() {
		
		QuickTab cqt=new QuickTab(owner);
		cqt.currentNode=currentNode;
		cqt.currentHue=currentHue;
		
		cqt.node=new Node[node.length];
		for(int i=0;i<node.length;i++)
			if (node[i] != null)
				cqt.node[i]=node[i].deepCopy(owner);
			else cqt.node[i]=null;
		
		cqt.latestEarlyHue=latestEarlyHue;
		
		return cqt;
	}
	
	private int userChooseIntegerBtwn(boolean wuip, String msg,int min, int max){
		if(!wuip) return min;
		
		int chx=min-1;
		while ((chx<min) || (chx>max)){
			try{ 
				System.out.println(msg+" ("+min+" to "+max+"):");
				String ui=Keyboard.getInput();
				if (ui.equals("")) {
					chx=min;
					System.out.println("Default "+chx+" chosen.");
				}
				else chx= Integer.parseInt(ui);
			} catch(Exception e){
				System.out.println(e.getMessage());
			}
			if ((chx<min) || (chx>max)) System.out.println("Incorrect user entry. Please try again.");
		}
		
		return chx;
	}

	private void helplist() {
		
		System.out.println("h = list options");
		System.out.println("q = quit");
		System.out.println("s = find an applicable rule here");
		System.out.println("e = move to next hue here");
		//System.out.println("r = list rules");
	}

	
	public String toString(){
		String s="";
		
		s=s+"There are "+numNonNullNodes()+" nodes.\n";
		
		for(int i=0;i<node.length;i++)
			if (node[i]!=null)
			s=s+"Node "+i+": "+node[i]+"\n";
		
		s=s+"Current node is "+currentNode+". ";
		s=s+"Current hue is "+currentHue+". ";
		s=s+"Back Track Stack size="+bts.size()+".";
		
		return s;
	}
	
	//store away info about hues, colours, starts, mosaics
	public void pigeonHole(){
		
		int[] coloured=new int[node.length];
		for(int i=0;i<node.length;i++) coloured[i]=-1;
		
		//keep list of hues and colours seen
		for(int i=0;i<node.length;i++)
			if ((node[i]!=null) && (!node[i].isPseudo()) && (!node[i].isLeaf())){
				if (complete(i)){
					boolean consistentColour=true;
					int[] hue=new int[node[i].numHues()];
					for(int j=0;j<node[i].numHues();j++)
						if (!incons(i,j)) hue[j]=storeHue(node[i].getLabel(j));
						else consistentColour=false;
					if (consistentColour) {
						coloured[i]=storeColour(hue);					
					}
				}
			}
		
		if (coloured[0]>-1) {
			//System.out.println(" new start colour "+coloured[0]);
			//System.out.println(" current list of colours:"+colourAnalysis());
			
			storeStartColour(coloured[0]);
			
		}
		
		//now store mosaics colours with successor colours in order
		for(int i=0;i<node.length;i++)	if (coloured[i]>-1){
					int[] sucol=new int[node[i].numHues()+1];
					sucol[0]=coloured[i];	
					boolean worthStoring=false;
					for(int j=0;j<node[i].numHues();j++) {
						int sjp=coloured[node[i].getSuc(j)];
						sucol[j+1]=sjp;
						if (sjp>-1) worthStoring=true;
					}
					if (worthStoring) storeMosaic(sucol);
			}
		
		
	}
	
	
	private int storeHue(ClosureSubset label) {
		int a=whichHue(label);
		if (a>-1) return a;
		hueList.add(label.deepCopy());
		return hueList.size()-1;
		//System.out.println("DEBUG adding "+label);
		//System.out.println("even though it is at posn "+whichHue(label)+" out of "+hueList.size());
		//printHueList();
	}
	
	private int whichHue(ClosureSubset l){
		//System.out.println("DEBUG size of hue list now"+hueList.size());
		for(int i=0;i<hueList.size();i++){
			if (l.equals((ClosureSubset)(hueList.get(i)))) return i;
		}
		return -1;
	}
	
	private String hueAnalysis(){
		String s=" HERE are the hues in the labels in each node:\n";
		
		for(int i=0;i<node.length;i++)
			if ((node[i]!=null) && (!node[i].isPseudo()) && (!node[i].isLeaf())){
				if (complete(i)){
					s=s+"Node "+i+": {";
					for(int j=0;j<node[i].numHues();j++){
						
						s=s+" "+whichHue(node[i].getLabel(j));
					}
					s=s+"} \n";
				}
			}
		
		s=s+" where the hues are as follows: \n";
		
		for(int j=0;j<hueList.size();j++)
			s=s+" "+j+"= "+((ClosureSubset)hueList.get(j))+"\n";
		
		return s;
	}
	
	public void printHueList(){
		String s=" Hues are as follows: \n";
		
		for(int j=0;j<hueList.size();j++)
			s=s+" "+j+"= "+((ClosureSubset)hueList.get(j))+"\n";
		
		System.out.println(s);
	}

	private int storeStartColour(int c) {
		int a=whichInt(c);
		if (a>-1) return a;
		startList.add(new Integer(c));
		
		//debug
		//System.out.println("storing start "+c+" "+node[0]);		
		//Keyboard.getInput();
		
		return startList.size()-1;
	}
	
	private int whichInt(int c){
		for(int i=0;i<startList.size();i++){
			if (c==((Integer)(startList.get(i))).intValue()) return i;
		}
		return -1;
	}
	
	private int storeColour(int[] c) {
		int a=whichColour(c);
		if (a>-1) return a;
		colourList.add(c);
		return colourList.size()-1;
	}
	
	private int whichColour(int[] c){
		for(int i=0;i<colourList.size();i++){
			if (sameColour(c,(int[])(colourList.get(i)))) return i;
		}
		return -1;
	}
	
	private boolean sameColour(int[] c, int[] is) {
		if (c.length!=is.length) return false;
		for(int i=0;i<c.length;i++)
			if (c[i]!=is[i]) return false;
		return true;
	}
	
	private String colourAnalysis(){
		String s="\n and HERE are the colours seen so far:\n";
		
		for(int j=0;j<colourList.size();j++){
			s=s+" colour #"+j+"= {";
			int[] hue=(int[])colourList.get(j);
			for(int i=0;i<hue.length;i++)
				s=s+" "+hue[i];
			s=s+" }\n";
		}
		
		s=s+"\n and the initial colours are:";
		for(int j=0;j<startList.size();j++)
			s=s+" "+((Integer)(startList.get(j))).intValue();
		s=s+"\n";
		
		return s;
	}
	
	private int storeMosaic(int[] c) {
		int a=whichMosaic(c);
		if (a>-1) return a;
		mosaicList.add(c);
		return mosaicList.size()-1;
	}
	
	private int whichMosaic(int[] c){
		for(int i=0;i<mosaicList.size();i++){
			if (sameColour(c,(int[])(mosaicList.get(i)))) return i;
		}
		return -1;
	}
	
	private boolean sameMosaic(int[] c, int[] is) {
		if (c.length!=is.length) return false;
		for(int i=0;i<c.length;i++)
			if (c[i]!=is[i]) return false;
		return true;
	}
	
	private String mosaicAnalysis(){
		String s="\n and HERE are the mosaics seen so far:\n";
		
		for(int j=0;j<mosaicList.size();j++){
			int[] m=(int[]) mosaicList.get(j);
			s=s+" colour #"+m[0]+" can lead to colours {";
			for(int i=1;i<m.length;i++)
				s=s+" "+m[i];
			s=s+" }\n";
		}
		
		return s;
	}
	
	private String mosaicFinalAnalysis(){
		String s="\n and HERE are the mosaics seen so far:\n";
		
		for(int c=0;c<colourList.size();c++)
		for(int j=0;j<mosaicList.size();j++){
			int[] m=(int[]) mosaicList.get(j);
			if (c==m[0]){
				s=s+" colour #"+m[0]+" can lead to colours {";
				for(int i=1;i<m.length;i++)
					s=s+" "+m[i];
				s=s+" }\n";
			}
		}
		
		return s;
	}

	private void mosaicReport(){
		silent=false;
		sop(true,"At step "+stepNo+" current tableau:"+this);
		sop(true,""+hueAnalysis());		
		sop(true,""+colourAnalysis());
		sop(true,""+mosaicFinalAnalysis());
		sop(true,""+earlyHueAnalysis());
		sop(true,"");
		silent=true;
	}
	
	
	public int numNodes(){
		return node.length;
	}
	
	//not null ones
	public int numNonNullNodes(){
		int ctr=0;
		for(int i=0;i<node.length;i++)
			if (node[i] !=null) ctr++;
		return ctr;
	}
	
	public boolean lg(){
		if (!canApplyLG()){
			sop(true,"LG can not be applied yet. Attend to the non-leaves.");
			return false;
		}
		
		if (new LG(this).check()) {
			sop(true,"LG succeeds");
			return true;
		}
		sop(true,"LG FAILS");
		return false;
	}

	public Node getNode(int i) {
		return node[i];
	}

	public FormulaTree getOwner() {
		return owner;
	}
	
	public static String bie(String inp){

		String s=inp.substring(1);
	    int num=Integer.parseInt(s);
	    

	    String egf;
	    if (num>0)
	        egf=Examples.getExample(num);
	    else
	        egf="-("+Examples.getExample(-num)+")";
	    
	    return egf;
	}
	
	public String fms(int i){
		return sf.getFormula(i).abbrev();
	}
	
	public void mosaicCheck(){
		
		backtrackOnRepetition=true;
		//collect all mosaics		
		//silent=true;
		
		explored=new boolean[0];

		while ((!finished)){
			chooseNode(); 
			
			silent=false;
			sop(true,"At step "+stepNo+" current tableau:"+this);
			sop(true,""+hueAnalysis());		
			sop(true,""+colourAnalysis());
			sop(true,""+mosaicFinalAnalysis());
			sop(true,"");
			
		System.out.println("Debug in mosaicCheck(); press enter");
			Keyboard.getInput();
			
			if (!finished) oneStep(false);
		}
		silent=false;

		sop(true,"At step "+stepNo+" current tableau:"+this);
		sop(true,""+hueAnalysis());		
		sop(true,""+colourAnalysis());
		sop(true,""+mosaicFinalAnalysis());
		sop(true,"");
		
	}

	
	private boolean chooseNode(){
				
		if (findInconsistency()) return true;		
		
		if (findHue()) return true;
		
		//attend to non-leaf nodes
		for(int n=0;n<node.length;n++)
		if ((node[n]!= null) && (!node[n].isLeaf())) {
			currentNode=n;
			currentHue=0;
			if (findHue()) {
				if (currentHue==0) sop(true,"Move to hue 0 ");
				sop(true,"...of node "+n);
				return true;
			} else {
				if (hasRedundantHues(n)){
					sop(true,"No redundant hues allowed on completed nonleaf nodes."+n);
				
					backtracking=true;
					return true;
				}
			}
		}	
		//are there any nonleafnodes with redundant hues
		//if so backtrack!

		//System.out.println("DEBG 4");
		
		//if we have looped recently without LG check then do it now
		if (uncheckedLoop){
			if (canApplyLG()){
				sop(true,"Will now apply LG check.");
				
				if (lg()){
					sop(true,"Passed LG check.");
					uncheckedLoop=false;
				} else {
					sop(true,"Failed LG check.");
					sop(true,"Need to backtrack.");
					uncheckedLoop=false;
					backtracking=true;
					return true;
				}
				
			} else {
				System.out.println("Something is not right as can not apply LG check.");
				return false;
			}
		}
		
		
		//are there any non-leaf nodes which could loop up
		repDetected=false;
		for(int n=0;n<node.length;n++)
			if ((node[n]!= null) && (!node[n].isLeaf())) {
				if (loopDetect(n)){
					sop(true,"Loop possible: move to node "+n);
					currentHue=0;
					return true;
				}
			}
		
		//System.out.println("DEBG 6");
		
		//
		if ((repDetected) && (backtrackOnRepetition)){
			//sop(true,"Repetition has not been allowed and has been detected.");
			sop(true,"Partial mosaic is being stored.");
			pigeonHole();
			
			mosaicReport();
			
			//using early hue repetition instead of this
			//usedUnjustifiedBacktrack=true;
			//sop(true,"Need to backtrack.");
			//backtracking=true;
			//return true;
		}
		
		
		//update colours finished
		//not finished yet
		boolean[] nex=new boolean[colourList.size()];
		for(int i=0;i<explored.length;i++)
			nex[i]=explored[i];
		explored=nex;
		
		//attend to leaf nodes
		//for now to move towards dfs here try one with biggest number

		for(int n=node.length-1;n>=0;n--)
		if ((node[n]!= null) && (!node[n].isPseudo()) && (node[n].isLeaf())) {
				currentNode=n;
				currentHue=0;
				if (findHue()) {
					if (currentHue==0) sop(true,"Move to hue 0 ");
					sop(true,"...of node "+n);
					int cneh=storeEarlyHue();
					int earlyHueRepeat=recordLatestEarlyHue(currentNode,cneh);
					
					if (earlyHueRepeat>=2){
						System.out.println("Backtracking on multiple repeat of early hue");
						backtracking=true;
						return true;
					}
					return true;
				}
			}
		
		
		
		sop(true,"There are no incomplete nodes.");
		
		
		if (canApplyLG()){
			sop(true,"Will now apply LG check.");
			
			if (lg()){
				sop(true,"Passed LG check.");
			} else {
				sop(true,"Failed LG check.");
				sop(true,"Need to backtrack.");
				backtracking=true;
				return true;
			}
			
		} else {
			System.out.println("Something is not right as can not apply LG check.");
			return false;
		}
		
		//System.out.println("DEBG 10");
		
		System.out.println("The tableau is SUCCESSFUL. Took "+stepNo+" steps.");
		finished=true;
		System.out.println("We can conclude the formula is satisfiable.");
		finalResult=1;
		
		return false;
	}

	//return number of repeats of that hue at ancestors
	private int recordLatestEarlyHue(int c, int cneh) {
		
		int nr=0;
		System.out.println("New early hue at node "+c);
		
		if (c>=latestEarlyHue.length){
			int[] nleh=new int[c+1];
			for(int i=0;i<latestEarlyHue.length;i++){
				nleh[i]=latestEarlyHue[i];
				if (nleh[i]==cneh){
					//System.out.println("We have seen this new early hue already at node "+i);
					if (isAncestor(i,c)){
						System.out.println("We have seen this new early hue already at ancestor node "+i);
						nr++;
					}
				}
			}
			latestEarlyHue=nleh;
		}
		
		latestEarlyHue[c]=cneh;
		return nr;
		
	}

	private boolean isAncestor(int i, int c) {
		if (i==c) return true;
		while (c!=0){
			int p=node[c].getParent();
			if (p==i) return true;
			c=p;
		}
		return false;
	}

	private int storeEarlyHue() {
		
		ClosureSubset label=node[currentNode].getLabel(currentHue);
		
		System.out.println("New early hue"+label);
		
		int a=whichEarlyHue(label);
		if (a>-1) return a;
		earlyHueList.add(label.deepCopy());
		return earlyHueList.size()-1;
		
	}
	
	private int whichEarlyHue(ClosureSubset l){
		//System.out.println("DEBUG size of hue list now"+hueList.size());
		for(int i=0;i<earlyHueList.size();i++){
			if (l.equals((ClosureSubset)(earlyHueList.get(i)))) return i;
		}
		return -1;
	}
	
private void processAutoLimitBetaVersion(long maxMillis) {
			
		backtrackOnRepetition=true;
		
		explored=new boolean[0];
		
		silent=true;
		int rctr=stepNo+1;
		System.out.println("At step "+stepNo+" current tableau:"+this);
		long stime=System.currentTimeMillis();
		while ((!finished) && (System.currentTimeMillis()-stime<maxMillis)){
			chooseNode(); 
			if (!finished) oneStep(false);
			if (stepNo>=rctr){
				silent=false;
				sop(true,"At step "+stepNo+" current tableau:"+this);
				sop(true,"");
				silent=true;
				rctr=2*rctr;
			}
		}
		silent=false;
		long etime=System.currentTimeMillis();
		sop(true,"Elapsed time = "+((etime-stime))+" milliseconds.");
		numSteps=stepNo;
		timeTaken=etime-stime;
		
	}

private String earlyHueAnalysis(){
	String s=" HERE are the early hues recorded for each node:\n";
	
	for(int i=0;i<latestEarlyHue.length;i++){
				s=s+"Node "+i+": "+latestEarlyHue[i]+"\n";
		}
	
	s=s+" where the hues are as follows: \n";
	
	for(int j=0;j<earlyHueList.size();j++)
		s=s+" "+j+"= "+((ClosureSubset)earlyHueList.get(j))+"\n";
	
	return s;
}

}
