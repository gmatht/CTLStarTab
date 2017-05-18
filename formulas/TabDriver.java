package formulas;

public class TabDriver {
	
    

    Report report;

    TemporalGraph t;

    

    private Node[] node;

    
    int widthRestriction=-1; //-1 for no restriction



	/** Creates a new instance of TabDriver */


    public TabDriver(String fs, Report r) {

        report=r;

        try {

            

        
            report.seol(0,"Test satisfiability in CTL* of "+fs);

            Logic logic=new AUXLogic();

            FormulaTree f=logic.parse(fs);

            report.seol(1,"parsed to "+f.toString());

            

            f=logic.disabbreviate(f);

            report.seol(1,"Disabbreviates to = "+f.inFull());

            

            report.seol(1,"which has length = "+f.length());

            

            t=new TemporalGraph(f,report);



        	node=new Node[1];

        	node[0]=new Node(-1,-1);

        	

            go();

            

            	

            report.seol(1,"That is all that is implemented: THE END.");

            

            report.close();

            

        } catch(ParseException e){

            System.out.println(e.getMessage());

        }

    }



	private void go() {
		// TODO Auto-generated method stub
		int status=-1;
		
		while (status>-2) {
			report.seol(1,"Keep building a tableau:");
			report.seol(1,"Current tableau: "+this);
		
			System.out.println("USER MENU: choose a character" );
			System.out.println("x: extend");
		    //System.out.println("b: backtack to reconsider some node");
		    System.out.println("q: quit");
	    
		    char log=Keyboard.getInput().charAt(0);
		    	switch (log) {
		    		case 'x': extend(); break;
		    		//case 'b': backtrack(); break;
		    		case 'q': report.close(); System.exit(0);
		    		default:
		    			System.out.println("choice not recognized");
		    }
		}
		
	}
	
    public String toString(){

    	String s="There are "+node.length+" nodes\n";

    	for(int i=0;i<node.length;i++)

    		s=s+"Node "+i+"\n"+node[i]+"\n";

    	return s;

    }
    
    public void extend(){
    	report.seol(1,"You chose to extend the tableau by (re)processing a node");
    	System.out.println("USER MENU: choose a node number" );
    	
    	int nn=Integer.parseInt(Keyboard.getInput());
    	report.seol(1,"You chose to process node "+nn);

    	NodeStore ns=storeState(nn);
    	node[nn].branch(0);
    	chooseAcolourFor(nn);
    	if (!loopUp(nn)) propagate(nn);
    	if (passesChecks(nn)) {
    		report.seol(1,"Finished process node ");
    	} else {
    		report.seol(1,"Processing node went wrong: back to old tableau. ");
    		restoreState(nn,ns);
    	}
    	
    }
    
    private void restoreState(int nn, NodeStore ns) {
		// TODO Auto-generated method stub
		
	}



	private boolean passesChecks(int nn) {
		// TODO Auto-generated method stub
		return true;
	}


    //tries to find the first/next colour for this node
    //returns -3 if successful and looped up this leaf node
    //returns -2 if successful but this leaf now has leaf kids
    // parent>=0 if can't do
    // -1 if can't do root
	private void chooseAcolourFor(int nn) {
		// TODO Auto-generated method stub
		/*
		  if (nn==0){
		 
			
			System.out.println("You need to choose a colour for the root");
			
			System.out.println("To start with, you need to choose its initial hue to contain phi");
			
			System.out.println("The perfect initial hues are:");
			
			System.out.println("USER MENU: choose a number" );

		    System.out.println("q: quit");
		    
		} else {
			
		}
		*/
		
		int parent=node[nn].getParent();
		int dirn=node[nn].getDirn();

    	//pick hue and colour
    	
    	RestrictedHueGenerator rhg=node[nn].getRHG();
    	RestrictedOrderedColourGenerator rocg=node[nn].getROCG();
    	
    	boolean done=false;	
    	while (!done){
    	
    		if (rhg==null){ //give it one
    			if (nn==0){
    	    		report.seol(2,"Choosing initial hue at root ");

    				int[] ji=new int[1];
    				ji[0]=t.getSubformulas().getIndexOfOwner();
    				rhg=new RestrictedHueGenerator(t,ji);
    				rhg.userSetHue();
		    	
    			} else {

    	    		report.seol(2,"Choosing initial hue below "+parent+" in direction "+dirn);
    	    		int hte=node[parent].oc().getHue(dirn);
    	    		int[] forms=t.reqdNext(hte);
    	    			//list of fmlas that must be carried over
    	    		rhg=new RestrictedHueGenerator(t,forms); 
    	    		rhg.userSetHue();
    			}
    			node[nn].setRHG(rhg); //sets colour null too  			
    		}//end rhg is null
    		
    		if (rocg==null){	
    			//now get first/next hue from rhg
    			if (!(rhg.hasNext())){
    				report.seol(1,"No (more) suitable hues: have to backtrack to parent");
    				node[nn].setROCG(null);
    				return;
    			}
				int fih=rhg.hue();
				report.seol(1,"A suitable hue is number "+fih);   	
				if (nn==0) rocg=new RestrictedOrderedColourGenerator(fih,t,widthRestriction);
				else 
					rocg=new RestrictedOrderedColourGenerator(fih,t,node[parent].oc().getAllHueNumbers(),widthRestriction); //and any other hues in colour must succ from parent 
				node[nn].setROCG(rocg);
    		}//end rocg null
    		
    		if (parent>-1)
    			rocg.userSetColour(node[parent].oc());
    		else rocg.userSetColour();
	    		
		    if (!(rocg.hasNext())){
		          report.seol(1,"No (more) colours: have to choose different initial hue");
		          rocg=null;
		    } else {
		    	
		    	//check that colour RX from parent's colour
		    	if (parent>-1){
		    		done=(node[parent].oc().rx(rocg.oc())) ;			    		
		    	} else    	done=true;
		    }
		    
    	}//end while !done
    	  

        	report.seol(2,"There is a suitable colour: "+rocg.oc());
		
		
		return; //but have to add kids
		
	}



	private void propagate(int nn) {   

		//give it new successors

		int numh=node[nn].oc().size();

		Node[] tmp=new Node[numh+node.length];

		for(int i=0;i<node.length;i++) tmp[i]=node[i];

		tmp[nn].branch(numh);

	

		for(int i=0;i<numh;i++){

			tmp[node.length+i]=new Node(nn,i);

			tmp[nn].givesucc(i,node.length+i);

		}

		node=tmp;

	}



	private boolean loopUp(int nn) {
	
		
		RestrictedOrderedColourGenerator rocg=node[nn].getROCG();
		int parent=node[nn].getParent();
		int dirn=node[nn].getDirn();
		
		//try a loop up instead of this node

		int depth=0;

		int cn=nn;

		while (cn>0) {

			cn=node[cn].getParent();

			depth++;

		}

		boolean loopOk=false;

		int d=depth+1;

		while ((!loopOk) && (1<d)){

			d--;

			//try to loop up d steps

			int tn=nn;

			for(int i=0;i<d;i++) tn=node[tn].getParent();

			if (node[tn].oc().match(rocg.oc())){

				

				node[parent].givesucc(dirn,tn);

				loopOk=tryLoop(parent,dirn,tn);

				if (!loopOk) //put back leaf

					node[parent].givesucc(dirn,nn);

			}

		}

        if (loopOk) return true;
        
		return false;
	}



	public NodeStore storeState(int nn){
    	int[] suc=new int[node[nn].numsucc()];
    	for(int i=0;i<suc.length;i++) suc[i]=node[nn].succ(i);
    	NodeStore ns= new NodeStore(nn,  node[nn].oc(), suc, node[nn].getParent(), node[nn].getDirn());
    	return ns;
    }
    
	  private boolean tryLoop(int parent, int dirn, int destn){

	    	report.seol(2,"Trying loop from "+parent+" in direction "+dirn+" up to node "+destn);


	    	//we need to model check the tableau via the partial hue idea of the paper

	    	

	    	//first we get the subformulas in increasing order

	    	Subformulas sf=t.getSubformulas();

	    	int[] sfi=sf.sortinc();

	    	int c=sfi.length;

	    	

	    	//each node gets a set of partial hues which start off as one empty one

	    	PartialHue[][] ph=new PartialHue[node.length][];

	    	for(int i=0;i<node.length;i++ ){

	    		ph[i]= new PartialHue[1];

	    		ph[i][0]=new PartialHue(c,i); //empty one

	    	}

	    	for(int i=0;i<node.length;i++ ){

	    		for(int j=0;j<node[i].numsucc();j++)

	    			ph[i][0].makeSucc(ph[node[i].succ(j)][0]);

	    	}

	    	

	    	//in each of the c rounds we update the partial hues at each node

	    	// via the fmla sfi[rd]

	    	for(int rd=0;rd<c;rd++){

	    		char tc=sf.topChar(sfi[rd]);

	    		report.seol(2,"PH round for number "+rd+" ie "+sfi[rd]+" "+t.getSubformulas().getFormula(sfi[rd]));

  			//debug
	    		
  			for(int i=0; i< node.length; i++)
  				for (int j=0;j<ph[i].length;j++){
  					report.seol(2, "debug: ph["+i+"]["+j+"]="+ph[i][j]);
  					int ns=ph[i][j].numsucc();
  					report.seol(2, "debug: its "+ns+" successors are: ");
  					for(int k=0;k<ns;k++){
  						report.seol(2, "debug: "+k+"th is "+ph[i][j].getSucc(k));
  					}
  				}  

	    		if ((('a'<=tc) && (tc<='z'))){

	    			for(int i=0;i<node.length;i++){

	    				if (!node[i].isLeaf()){

	    				//put sfi[rd] into all phs if atom is in any hue label at node i

	    				if (t.contains(node[i].oc().getFirstHue(),sfi[rd])){

	    					for(int k=0;k<ph[i].length;k++) 

	    						ph[i][k].add(sfi[rd]);			

	    				}

	    				}

	    			}

	    		} else if (tc=='1'){

	    			for(int i=0;i<node.length;i++){

	    				if (!node[i].isLeaf()){

	    				//put sfi[rd] into all phs 

  					for(int k=0;k<ph[i].length;k++) 

  						ph[i][k].add(sfi[rd]);	

	    				}

	    			}

	    		} else if (tc=='-'){

	    			int pf=sf.left(sfi[rd]);

	    			for(int i=0;i<node.length;i++){

	    				if (!node[i].isLeaf()){

	    				//put sfi[rd] into all phs which do not have pf

  					for(int k=0;k<ph[i].length;k++) {

  						if (!ph[i][k].has(pf)) ph[i][k].add(sfi[rd]);

  					}

	    				}

	    			}	

	    		} else if (tc=='&'){

	    			int lf=sf.left(sfi[rd]);

	    			int rf=sf.right(sfi[rd]);

	    			for(int i=0;i<node.length;i++){

	    				if (!node[i].isLeaf()){

	    				//put sfi[rd] into all phs which have both lf and rf

  					for(int k=0;k<ph[i].length;k++) {

  						if ((ph[i][k].has(lf)) && (ph[i][k].has(rf))) ph[i][k].add(sfi[rd]);

  					}

	    				}

	    			}	

	    		} else if (tc=='X'){

	    			int lf=sf.left(sfi[rd]);
	    			

	    			//make an array [][] of boolean to record poss and another for neg

	    			boolean[][] addpos=new boolean[node.length][];

	    			boolean[][] addneg=new boolean[node.length][];
	    			

	    			for(int i=0;i<node.length;i++){

	    				if (!node[i].isLeaf()){

	    				//id  which have lf in a next ph
	    					addpos[i]=new boolean[ph[i].length];
	    					addneg[i]=new boolean[ph[i].length];
	    				

  					for(int k=0;k<ph[i].length;k++) {



  						addpos[i][k]=false;

  						addneg[i][k]=false;

  						int numsucc=ph[i][k].numsucc();

  						for(int l=0;l<numsucc;l++){

  							PartialHue xph=ph[i][k].getSucc(l);

  						    int succnode=xph.getOwnNum();

  							if (node[succnode].isLeaf()){

  								//get Xp from the corresponding actual hue here

  								//find the hue at node[i] which created need for node[succnode]

  								int d=-1;

  								for (int j=0;j<node[i].numsucc();j++)

  									if (node[i].succ(j)==succnode) d=j;

  								int hn=node[i].oc().getHue(d);

  								

  								//see if Xp ie sfi[rd] is in that hue

  								if (t.contains(hn,sfi[rd])) addpos[i][k]=true; else addneg[i][k]=true;

  								

  								//and make possucc or negsucc true depending

  								

  							} else { //succ node is not a leaf

  								if (xph.has(lf)) {

  									addpos[i][k]=true;

  								} else {

  									addneg[i][k]=true;

  								}

  							}//end not a leaf

  						}//end of loop thru successors
  					}//end for loop thru ph[i] [...]
	    				}//end node not leaf
	    			}//end for node i
	    			
	    			//update the new phs but not successor relation
	    			int[] kn=new int[node.length];
	    			int[][] splitTo=new int[node.length][];
	    			for(int i=0;i<node.length;i++){

	    				kn[i]=ph[i].length;
	    				splitTo[i]=new int[kn[i]];
	    				splitTo[i][0]=-1;
	    				
	    				if (!node[i].isLeaf()){

	    				//put sfi[rd] into all phs which have lf in a next ph

	    				//but split and don't add sfi[rd] if there is a next without lf
	    				 
	    				 

  					for(int k=0;k<kn[i];k++) {
  						
  						splitTo[i][k]=-1;

							if (addpos[i][k]) {

								if (addneg[i][k]){

									//split to make both a + and a -				

									//split ph[i][k] into two,

									int phil=ph[i].length;

									PartialHue[] tphi=new PartialHue[phil+1];

									for(int g=0;g<phil;g++) tphi[g]=ph[i][g];

									tphi[phil]=ph[i][k].split(); //ph[i][kn] is the new one

									ph[i]=tphi;

									splitTo[i][k]=phil;
									

									//put sfi[rd]=Xalpha in one new ph (=old one) and not the other (=kn)

									ph[i][k].add(sfi[rd]);

									

									

								} else { //only possucc

									//just add +

									ph[i][k].add(sfi[rd]);

								}//end only possucc

							}//end possucc

							//if only negsucc then we don't need do anything

  					}//end of loop thru phs at node

	    				}//end of if node is a leaf

	    			}//end of loop thru nodes
	    			
	    			//now loop around sorting out successors
	    			
	    			for(int i=0;i<node.length;i++){

	    				if (!node[i].isLeaf()){

	    				//put sfi[rd] into all phs which have lf in a next ph

	    				//but split and don't add sfi[rd] if there is a next without lf
	    				

  					for(int k=0;k<kn[i];k++) {
  						
  						int phikns=ph[i][k].numsucc();
  						for(int h=0;h<phikns;h++){
  							PartialHue phsuc=ph[i][k].getSucc(h);
  							int onn=phsuc.getOwnNum();
  							int indx=phsuc.getIndex(ph[onn]);
  							int sn=splitTo[onn][indx];
  							//report.seol(2,"debug8:"+i+" "+k+" "+onn+" "+indx+" "+sn);
  							if (sn>-1) ph[i][k].makeSucc(ph[onn][sn]);
  						}

							if (addpos[i][k]) {

								if (addneg[i][k]){

									//split to make both a + and a -				

									//split ph[i][k] into two,
								
									

									//update all the successors from one or the other depending

									
		    						int numsucc=ph[i][k].numsucc();
		    						PartialHue[] phsuc=new PartialHue[numsucc];
		    						for (int l=0; l<numsucc;l++) phsuc[l]=ph[i][k].getSucc(l);
		    						
									for(int l=0;l<numsucc;l++){

		    							PartialHue xph=phsuc[l];

		    						    int succnode=xph.getOwnNum();

		    							if (!node[succnode].isLeaf()){

		    								if (xph.has(lf)) {

		    									//possucc=true;

		    									//nothing to do: k already goes to xph

		    								} else {

		    									//negsucc=true;

		    									//make kn go to xph

		    									ph[i][splitTo[i][k]].makeSucc(xph);

		    									//make k no longer go to xph

		    									ph[i][k].scrapSucc(xph);

		    								}

		    							}//end not a leaf
		    							
		    							else { //succnode is a leaf
		      								//get Xp from the corresponding actual hue here

		      								//find the hue at node[i] which created need for node[succnode]

		      								int d=-1;

		      								for (int j=0;j<node[i].numsucc();j++)

		      									if (node[i].succ(j)==succnode) d=j;

		      								int hn=node[i].oc().getHue(d);

		      								

		      								//see if Xp ie sfi[rd] is in that hue

		      								if (t.contains(hn,sfi[rd])) {
		      								//nothing to do: k already goes to xph
		      								}
		      								else {
		    									//make kn go to xph

		    									ph[i][splitTo[i][k]].makeSucc(xph);

		    									//make k no longer go to xph

		    									ph[i][k].scrapSucc(xph);
		      								}
		    							}//end succnode is a leaf

		    						}//end of loop thru successors

								

									

								} else { //only possucc

									

									

								}//end only possucc

							}//end possucc

							//if only negsucc then we don't need do anything

  					}//end of loop thru phs at node

	    				}//end of if node is a leaf

	    			}//end of loop thru nodes

	    		} else if (tc=='U'){




	    			int lf=sf.left(sfi[rd]);

	    			int rf=sf.right(sfi[rd]);

	    			

	    			//a + will be put sfi[rd] into all phs which have a lfUrf path (and into all along path)

	    			//a - will be don't add sfi[rd] if there is a path falsifying lfUrf

	    			//split if both



	    			//make an array [][] of boolean to record poss and another for neg

	    			//also need to collect list of eventual reached phs to find loops with -rf all along

	    			boolean[][] addpos=new boolean[node.length][];

	    			boolean[][] addneg=new boolean[node.length][];

	    			//PartialHue[][][] er=new PartialHue[node.length][][];
	    			
	    			ThreadRec[][] tr=new ThreadRec[node.length][];

	    			for(int i=0;i<node.length;i++){

	    				addpos[i]=new boolean[ph[i].length];

	    				addneg[i]=new boolean[ph[i].length];

	    				//er[i]=new PartialHue[ph[i].length][];
	    				
	    				tr[i]=new ThreadRec[ph[i].length];

	    				for (int j=0;j<ph[i].length;j++){

	    					addpos[i][j]=false;

	    					addneg[i][j]=false;

	    					//er[i][j]=new PartialHue[0];
	    					tr[i][j]=new ThreadRec(t.getAllEvents(),t.getAllUntils());

	    				}

	    			}

					

	    			//repeatedly loop thru all phs looking at their successors and

	    			//noting need for adding new + or new - 

	    			//until no more changes

	    			boolean change=true;

	    			while (change){

	    				//report.seol(2,"into change loop again");

	    				change=false;

		    			for(int i=0;i<node.length;i++){
		    				
		    				if (!node[i].isLeaf()){

		    				for (int j=0;j<ph[i].length;j++){

		    					if (!addpos[i][j]){

		    						//rf is here

		    						if (ph[i][j].has(rf)) {

		    							addpos[i][j]=true;

		    							change=true;

		    						} else {

		    						//lf is here and ...	

		    							if (ph[i][j].has(lf)){

		    								//some successor ph has addpos true

		    								for(int k=0;k<ph[i][j].numsucc();k++){
		    									
		    									//report.seol(2,"debug1: i="+i+" j="+j+" k="+k);

		    									PartialHue sw=ph[i][j].getSucc(k);
		    									
		    									//report.seol(2,"debug2: sw="+sw);

		    									int onn=sw.getOwnNum();
		    									
		    									//report.seol(2, "debug3: owner=node #"+onn);
		    									
		    									if (node[onn].isLeaf()){
		    										
		    										//find the hue at node[i] which created need for node[succnode]

				      								int d=-1;

				      								for (int s=0;s<node[i].numsucc();s++)

				      									if (node[i].succ(s)==onn) d=s;

				      								int hn=node[i].oc().getHue(d);

				      								

				      								//see if aUb ie sfi[rd] is in that hue

				      								if (t.contains(hn,sfi[rd])) {
				      								
				      									addpos[i][j]=true;
				      									change=true;
				      								}
				      								else { //hue does not contain aUb
				      									//can not conclude anything
				      								}
		    										
		    									} else {//node onn not a leaf

		    										int indx=sw.getIndex(ph[onn]);
		    									
		    										//report.seol(2, "debug4: indx="+indx);
	


		    										if (addpos[onn][indx]){

		    											addpos[i][j]=true;

		    											change=true;

		    										}
		    									}

		    								}

		    							}	

		    						}				

		    					}//end addpos

		    					if (!addneg[i][j]){

		    						

		    						//addneg if neither lf and rf is here

		    						if (!(ph[i][j].has(rf)) && !(ph[i][j].has(lf))) {

		    							addneg[i][j]=true;

		    							change=true;

		    						} else if (!(ph[i][j].has(rf))) {

		    						//addneg if rf not here, lf is here and addneg true at a successor

	    								for(int k=0;k<ph[i][j].numsucc();k++){

	    									PartialHue sw=ph[i][j].getSucc(k);

	    									int onn=sw.getOwnNum();
	    									
	    									if (node[onn].isLeaf()){
	    										//find the hue at node[i] which created need for node[succnode]

			      								int d=-1;

			      								for (int s=0;s<node[i].numsucc();s++)

			      									if (node[i].succ(s)==onn) d=s;

			      								int hn=node[i].oc().getHue(d);

			      								

			      								//see if aUb ie sfi[rd] is not in that hue

			      								if (!t.contains(hn,sfi[rd])) {
			      								
			      									addneg[i][j]=true;
			      									change=true;
			      								}
			      								else { //hue does contain aUb
			      									//can not conclude anything
			      								}
	    									}
	    									else { //node onn not leaf
	    									int indx=sw.getIndex(ph[onn]);

	    									if (addneg[onn][indx]){

	    		    							addneg[i][j]=true;

	    		    							change=true;

	    									}
	    									}

	    								}

		    						} 

		    						//addneg if there is a loop from here back to here via all -rf

		    						if (!addneg[i][j])

		    							if (!(ph[i][j].has(rf))) {

		    								//update list of eventually reached via -rf (not needed if addneg already here)

		    								for(int k=0;k<ph[i][j].numsucc();k++){

		    									PartialHue sw=ph[i][j].getSucc(k);

		    									int onn=sw.getOwnNum();
		    									if (!node[onn].isLeaf()){
		    									int indx=sw.getIndex(ph[onn]);

					    						/*if (!isin(ph[onn][indx],er[i][j])){

					    							er[i][j]=addin(ph[onn][indx],er[i][j]);

					    							change=true;

					    						}*/
		    									//report.seol(2,"debug 2: change="+change+" i="+i+" j="+j+" k="+k);
					    						change=change || tr[i][j].addinOneStep(ph[i][j],ph[onn][indx]);
					    						//report.seol(2,"debug 3: change="+change);

		    									// and copy all of er[onn][indx] into er[i][j]

		    					    			/*
		    					    			 for(int ii=0;ii<node.length;ii++){
		    					    			

		    					    				for (int jj=0;jj<ph[ii].length;jj++){

		    					    					if (isin(ph[ii][jj],er[onn][indx]))

		    					    						if (!isin(ph[ii][jj],er[i][j])){

		    					    							er[i][j]=addin(ph[ii][jj],er[i][j]);

		    					    							change=true;

		    					    						}

		    					    				}

		    					    			}*/
		    					    			
		    					    			change=change || tr[i][j].mergeIn(tr[onn][indx]);
		    					    			//report.seol(2,"debug 4: change="+change);
		    									}
		    		    						//now check if ph[i][j] is now in er[i][j]

		    					    			/*
  					    					if (isin(ph[i][j],er[i][j])){

  					    							er[i][j]=addin(ph[i][j],er[i][j]);

  					    							addneg[i][j]=true;

  					    							change=true;

  					    						}//end if loop found
  					    						*/
  					    					if (tr[i][j].fulfilledLoop(ph[i][j],ph[i][j])){
  					    						//report.seol(2,"debug 5: change="+change);
					    							addneg[i][j]=true;

					    							change=true;
  					    					}

		    									}//end for k (successor)

		    								}//end 2nd addneg

		    						}//end addneg	



		    				}//end for j
		    				}//not leaf

		    			}//end for i

	    				

	    			}//end change loop

	    			

	    			//this next bit now only needs deal with the updates

	    			//report.seol(2,"now onto updates");

	    			

	    			//update the phs

	    			

	    			//but remember who got split into whom

	    			int[][] splitTo =new int[node.length][];
	    			int[] pl=new int[node.length]; //how many phs in i before splitting

	    			for(int i=0;i<node.length;i++){

	    				

	    				if (!node[i].isLeaf()){

	    					splitTo[i]=new int[ph[i].length];

	    					


	    					pl[i]=ph[i].length;
  					for(int k=0;k<pl[i];k++) {

  						//if pos 

  						if (addpos[i][k]){

  							if (addneg[i][k]){

  							// if neg as well then split 

									int kn=ph[i].length;

									PartialHue[] tphi=new PartialHue[kn+1];

									for(int g=0;g<kn;g++) tphi[g]=ph[i][g];

									tphi[kn]=ph[i][k].split(); //ph[i][kn] is the new one

									ph[i]=tphi;

									splitTo[i][k]=kn;

  							} else {

  								//if not neg then just put aUb into +

  								splitTo[i][k]=-1;

  							}

     							ph[i][k].add(sfi[rd]);

  						}//end addpos

  						else {splitTo[i][k]=-1;} //just -ve
  					}//end for k

	    				}//end is not leaf

	    			}//end for i

	    			

	    			//now update delta

	    			//report.seol(2,"now update delta");

	    			

	    			for(int i=0;i<node.length;i++){

	    				

	    				if (!node[i].isLeaf()){

	    				//report.seol(2,"look at non leaf node "+i+" with (up until now) "+pl[i]+" phs");


  					for(int k=0;k<pl[i];k++) {

  						//report.seol(2,"look at its "+k+"th ph "+ph[i][k]);

  						//if pos 

  						if (addpos[i][k]){ //'cause lf here and addpos next

  							if (addneg[i][k]){  //'cause -rf&-lf, or -rf&lf&X+ or -rfLoop

  								//report.seol(2,"a splitter");

  							// if neg as well then split 

									int kn=splitTo[i][k];

									
									int phikns=ph[i][k].numsucc();
									for(int l=0;l<phikns;l++){

		    							PartialHue xph=ph[i][k].getSucc(l);

		    						    int succnode=xph.getOwnNum();

		    							if (!node[succnode].isLeaf()){

		    								int sspl=splitTo[succnode][xph.getIndex(ph[succnode])];

		    								

		    								//can not have rf here as that won't allow addneg

		    								

		    								//case: we have -rf&lf&X+

		    								//link ik to +ve

		    								//no need to do anything

		    								

		    								//case: we have -rf&-lf

		    								//link ikn to all

		    								if ((!(ph[i][k].has(rf)) && !(ph[i][k].has(lf)))){

		    									

		    									if (sspl==-1){

		    										ph[i][kn].makeSucc(ph[succnode][xph.getIndex(ph[succnode])]);

		    										

		    									} else {

		    										ph[i][kn].makeSucc(ph[succnode][xph.getIndex(ph[succnode])]);

		    										ph[i][kn].makeSucc(ph[succnode][sspl]);

		    									}

		    								}

		    								

		    								//case: we have -rf&lf&X-

		    								//link ikn to -ve

		    								//also 

		    								//case: we have -rf Loop ahead

		    								//link ikn to -ve

		    								if (addneg[succnode][xph.getIndex(ph[succnode])]){

		    									if (sspl==-1){

		    										ph[i][kn].makeSucc(ph[succnode][xph.getIndex(ph[succnode])]);
		    										ph[i][k].scrapSucc(ph[succnode][xph.getIndex(ph[succnode])]);
		    										l--;
		    										phikns--;
		    										

		    									} else {

		    										ph[i][kn].makeSucc(ph[succnode][sspl]);

		    									}

		    									

		    								}//end addneg



		    							}//end not a leaf
		    							else {		//succnode is a leaf
		    								//find the hue at node[i] which created need for node[succnode]

		      								int d=-1;

		      								for (int s=0;s<node[i].numsucc();s++)

		      									if (node[i].succ(s)==succnode) d=s;

		      								int hn=node[i].oc().getHue(d);

		      								

		      								//see if aUb ie sfi[rd] is  in that hue

		      								if (t.contains(hn,sfi[rd])) {
		      								
		      									
		      									
		      								}
		      								else { //hue does not contain aUb
		      									ph[i][kn].makeSucc(ph[succnode][0]);
		      									ph[i][k].scrapSucc(ph[succnode][0]);
		      									l--;
		      									phikns--;
		      								}
		    							}

		    						}//end of loop thru successors

									

  							} else {

  								//if not neg then was not split

  								//report.seol(2,"just +ve");

  								
  								int phikns=ph[i][k].numsucc();
									for(int l=0;l<phikns;l++){

		    							PartialHue xph=ph[i][k].getSucc(l);

		    						    int succnode=xph.getOwnNum();

		    							if (!node[succnode].isLeaf()){

		    								int sspl=splitTo[succnode][xph.getIndex(ph[succnode])];

		    								

		    								//case: rf here

		    								//link ik to any

		    								if (ph[i][k].has(rf)) {

		    									

		    									if (sspl==-1){

		    										ph[i][k].makeSucc(ph[succnode][xph.getIndex(ph[succnode])]);

		    										

		    									} else {

		    										ph[i][k].makeSucc(ph[succnode][xph.getIndex(ph[succnode])]);

		    										ph[i][k].makeSucc(ph[succnode][sspl]);

		    									}

		    								}

		    								

		    								//case: we have -rf&lf&X+

		    								//link ik to +ve

		    								//nothing to do

		    							

		    								



		    							}//end not a leaf

		    						}//end of loop thru successors

									

  							}

  						}//end addpos

	    				    else { //addneg only, not split

	    				    	//report.seol(2,"just -ve");

	    				    			
	    				    	int phikns=ph[i][k].numsucc();
								for(int l=0;l<phikns;l++){

	    							PartialHue xph=ph[i][k].getSucc(l);

	    						    int succnode=xph.getOwnNum();

	    							if (!node[succnode].isLeaf()){

	    								int sspl=splitTo[succnode][xph.getIndex(ph[succnode])];

	    								

	    								

	    								//case: we have -rf&-lf

	    								//link ik to all

	    								if ((!(ph[i][k].has(rf)) && !(ph[i][k].has(lf)))){

	    									

	    									if (sspl==-1){

	    										ph[i][k].makeSucc(ph[succnode][xph.getIndex(ph[succnode])]);

	    										

	    									} else {

	    										ph[i][k].makeSucc(ph[succnode][xph.getIndex(ph[succnode])]);

	    										ph[i][k].makeSucc(ph[succnode][sspl]);

	    									}

	    								}

	    								

	    								//case: we have -rf&lf&X-

	    								//link ik to -ve

	    								//and

	    								//case: we have -rf Loop ahead

	    								//link ik to -ve

	    								if (addneg[succnode][xph.getIndex(ph[succnode])]){

	    									if (sspl==-1){

	    										ph[i][k].makeSucc(ph[succnode][xph.getIndex(ph[succnode])]);

	    										

	    									} else {

	    										ph[i][k].makeSucc(ph[succnode][sspl]);

	    									}

	    									

	    								}//end addneg



	    							}//end not a leaf

	    						}//end of loop thru successors

								

  						}

  						

  					}//end for k

	    				}//end is not leaf

	    			}//end for i

	    			

	    			//report.seol(2,"finished update delta and the U round");

	   

	    			

	    		} else if (tc=='A'){

	    			int lf=sf.left(sfi[rd]);

	    			for(int i=0;i<node.length;i++){

	    				if (!node[i].isLeaf()){

		    				//put sfi[rd] into all ph here iff lf is in all phs at that node

	    						boolean inall=true;

	    						for(int k=0;k<ph[i].length;k++) 

	    							if (!ph[i][k].has(lf)) inall=false;

	    						if (inall)

	    							for(int k=0;k<ph[i].length;k++) 

	    								ph[i][k].add(sfi[rd]);			

		    				}

	    			}//end of node loop

	    		}//end of case A

	    		

	    	}//end of round

	    	



			boolean match=true;

			for(int i=0;i<node.length;i++){

				if (!node[i].isLeaf()){

					//try to match this node's hues with phs

					//nodes phs are ph[i][0] ... ph[i][ph[i].length-1] as sets of fmlas

					//nodes hues are from node[i].oc() get hues

					int[] nodesHues=node[i].oc().getAllHueNumbers();

					

					//first turn phs into hues

					int[] hueFromPH =new int[ph[i].length];

					for(int j=0;j<ph[i].length;j++)

						hueFromPH[j]=hueFromPH(ph[i][j]);

			

					

					//see if all hues are phs

					for(int k=0;k<nodesHues.length;k++){

						boolean seen=false;

						for (int l=0;l<hueFromPH.length;l++){

							if (nodesHues[k]==hueFromPH[l]) seen=true;

						}

						if (!seen){

				    		report.seol(2,"Loop up not grounded: no path for label hue "+nodesHues[k]+" at node "+i);

				    	    match=false;

						}

					}

					

					//see if all phs are hues

					for(int l=0;l<hueFromPH.length;l++){

						boolean seen=false;

						for (int k=0;k<nodesHues.length;k++){

							if (nodesHues[k]==hueFromPH[l]) seen=true;

						}

						if (!seen){

				    		report.seol(2,"Loop up not grounded: no label hue at node "+i+" for a path having actual hue "+hueFromPH[l]);

				    	    match=false;

						}

					}

				}

				

			}

			

	    	

	    	if (match){
	    		report.seol(2,"Loop grounded: just a few more checks");

	    	} else {

	    		report.seol(2,"Loop not grounded: no loop up allowed");
	    		return false;

	    	}

	    	
	    	//now check the nominated thread property
	    	boolean alwaysLeftmost=(dirn==0);
	    	int loopLong=1;
	    	int at=parent;
	    	while ((alwaysLeftmost) && (at!=destn)){
	    		if (node[at].getDirn()==0) {
	    			at=node[at].getParent();
	    			loopLong++;
	    		} else alwaysLeftmost=false;
	    		
	    	}
	    	if (!alwaysLeftmost){
	    		report.seol(2,"This is not an always leftmost loop so it is allowed!");
	    		return true;
	    	}
	    
	    	//check the always zero thread is fulfilling
	    	int[] events=t.getEvents(node[parent].oc().getFirstHue());
	    	report.seol(2,"debug11: evenst are: "+list(events));
	    	boolean[] seen = new boolean[events.length];
	    	at=parent;
	    	for(int i=0; i< loopLong; i++){
	    		for(int j=0; j<events.length;j++){
	    			//report.seol(2,"debug11: i,j are: "+i+" "+j);
	    			//report.seol(2,"debug11: events[j] is: "+events[j]);
	    			//report.seol(2,"debug11: at is: "+at);
	    			//report.seol(2,"debug11: node[at].oc().getFirstHue() is: "+node[at].oc().getFirstHue());
	    			if (t.contains(node[at].oc().getFirstHue(),events[j])) seen[j]=true;   
	    		}
	    		at=node[at].getParent();
	    	}
	    	for (int j=0; j<events.length;j++){
	    		if (!seen[j]){
	    			report.seol(2,"Loop is NOT allowed: unwitnessed eventuality "+t.getSubformulas().getFormula(events[j]));
	    			return false;
	    		}
	    	}
	    	
	    	report.seol(2,"All eventualities witnessed in the always zero thread: so loop is allowed!");

	    	return true;

	    }//end tryloop


	    //finds hue numebr in t which matches partialHue
	private int hueFromPH(PartialHue partialHue) {

			

			for(int i=0; i<t.numHues();i++){

				boolean match=true;

				for(int j=0;j<t.getSubformulas().count();j++){

					if (t.contains(i,j)){

						if (!partialHue.has(j)) match=false;

					} else {

						if (partialHue.has(j)) match=false;

					}

				}//end for j

				if (match) return i;	

				

			}

			return -1;

		}





	private PartialHue[] addin(PartialHue partialHue, PartialHue[] partialHues) {

		int l=partialHues.length;

		PartialHue[] tmp=new PartialHue[l+1];

		for(int i=0; i<l;i++)

			tmp[i]=partialHues[i];

		tmp[l]=partialHue;

		return tmp;

			

		}





	private boolean isin(PartialHue partialHue, PartialHue[] partialHues) {

		for(int i=0;i<partialHues.length;i++)

			if (partialHue==partialHues[i]) return true;

		return false;

	}//end isin
	
	public static String list(int[] a){
		
		String s="{";
		for (int i=0;i<a.length;i++){
			s=s+", "+a[i];
		}
		return s+"}";
	}

	   


}
