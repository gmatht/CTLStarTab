



	package formulas;



	/**

	 *

	 * @author  mark

	 */
	


	public class StarTab {

	    

	    Report report;

	    TemporalGraph t;

	    	public int widthRestriction=Parameters.widthRestriction; //set this to -1 for no restriction
	    	public int depthRestriction=Parameters.depthRestriction; //set this to -1 for no restriction
	    

	    private Node[] node;
	    private Treeversal2 pic;
	    

	    /** Creates a new instance of StarTab */

   
	    public StarTab(String fs, Report r) {

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

            	pic=new Treeversal2(this);

	            go();

	            

	            	

	            report.seol(1,"That is all that is implemented so not continuing.");

	            

                report.close();

	            

	        } catch(ParseException e){

	            System.out.println(e.getMessage());

	        }

	    }

	    

	    

	    public void go(){
	    	
	    	boolean usf=true;
	    	int step=1;
	    	int status=-1;
	    	// >=0 means backtrack to that node
	    	// -1 move on building
	    	// -2 no leaf 
	    	// -3 run out of backtracking
	    	// -4 user requested halt
	    	
	    	if ((widthRestriction>-1) || (depthRestriction>-1))
	    	report.seol(1,"DANGER: there may be programmer imposed width "+widthRestriction+" and depth "+depthRestriction+" restrictions on the search");

	    	while (status > -2){ 
	    		
	    		if (status==-1) tidyUp(0);              //reclaim indices of unused nodes
	    		report.seol(1,"New tableau: "+this);
	    		//pic.update();
	    		
	    		if (status==-1) 
	    			report.seol(1,"What I am planning next is to extend the tableau by replacing a leaf.");
	    		else {
	    			status=tidyUp(status);
	    			report.seol(1,"What I am planning next is backtracking to reconsider node "+status);
	    		}
	    		
	    		if ((step%100==0) && (usf)){
	    			pic.update();
	    			System.out.println("Step #"+(step++)+": keep going? (new line=next 100 steps/n=stop/g=go on forever");

	    			String s=Keyboard.getInput();
	    			if (s.equals("n")) {
		    			status=-4; 
		    		} else if (s.equals("g")){
		    			usf=false;
		    		}
	    		} else {
	    			System.out.println("Step #"+(step++));
	    		}
	    		
	    		if (status>-1){
	    			status=backtrack(status);
	    		} else if (status==-1){    			
	    			status=doOneExten(); 
	    		}
	    	}//end while

	    	
	    	if (status==-2){
	    		report.seol(1,"Finished: fmla IS satisfiable");
	    	} else if (status==-3){
	    		report.seol(1,"Finished: fmla unsatisfiable");
	    	} else if (status==-4){
	    		report.seol(1,"Finished: User requested halt");
	    	}
	    	


	    	report.close();


    		

	    }// end go

	    

	    private int backtrack(int nn) {
	    	
	    	node[nn].branch(0);
	    	int a=repaint(nn);
	    	if (a==-3) return -1;
	    	if (a==-2) {  //extn done but not looped up
	    		
	    		if (tooWide(nn)) {
	    			report.seol(2,"Overwidth Node Detected: will have to redo node "+nn);
	    			return nn;
	    		}
	    		
	    		if (tooDeep(nn)) {
	    			report.seol(2,"Tableau too deep: will have to redo node "+nn);
	    			return nn;
	    		}
		    		
		    		if (repetitiveAboveNew(nn)) {
		    			report.seol(2,"Repetition Detected: will have to redo node "+nn);
		    			return nn;
		    		}
		    		return -1; //extn allowed, success
		    	}

	    	if (a==-1) return -3;
	    	return (a);
	    	
		}



	    //-3 means could not even colour in the root
	    //-2 means no leaf found
	    //-1 means one extension done Ok
	    // >=0 means now have to backtrack to that node and recolour it
	    public int doOneExten(){

	    	//pick a leaf node (won't have colours yet)

	    	int leaf=findNextLeaf();
	    	if (leaf==-1) return -2; 
	    	int ex=repaint(leaf);
	    	if (ex==-2){  //extn done but not looped up
	    		
	    		if (tooWide(leaf)) {
	    			report.seol(2,"Overwidth Node Detected: will have to redo node "+leaf);
	    			return leaf;
	    		}
	    		
	    		if (tooDeep(leaf)) {
	    			report.seol(2,"Tableau too deep: will have to redo node "+leaf);
	    			return leaf;
	    		}
	    		
	    		if (repetitiveAboveNew(leaf)) {
	    			report.seol(2,"Repetition Detected: will have to redo node "+leaf);
	    			return leaf;
	    		}
	    		return -1; //extn allowed, success
	    	}
	    	if (ex==-3) return -1;  //the leaf was turned into a good loop
	    	if (ex==-1) return -3;	//total failure
	    	return ex;				//parent to backtrack to next time
	    }
	    
	    //tries to find the first/next colour for this node
	    //returns -3 if successful and looped up this leaf node
	    //returns -2 if successful but this leaf now has leaf kids
	    // parent>=0 if can't do
	    // -1 if can't do root
	    public int repaint(int leaf){
	    	
			int parent=node[leaf].getParent();
			int dirn=node[leaf].getDirn();

	    	//pick hue and colour
	    	
	    	RestrictedHueGenerator rhg=node[leaf].getRHG();
	    	RestrictedOrderedColourGenerator rocg=node[leaf].getROCG();
	    	
	    	boolean done=false;	
	    	while (!done){
	    	
	    		if (rhg==null){ //give it one
	    			if (leaf==0){
	    	    		report.seol(2,"Choosing initial hue at root ");

	    				int[] ji=new int[1];
	    				ji[0]=t.getSubformulas().getIndexOfOwner();
	    				rhg=new RestrictedHueGenerator(t,ji);
			    	
	    			} else {

	    	    		report.seol(2,"Choosing initial hue below "+parent+" in direction "+dirn);
	    	    		int hte=node[parent].oc().getHue(dirn);
	    	    		int[] forms=t.reqdNext(hte);
	    	    			//list of fmlas that must be carried over
	    	    		rhg=new RestrictedHueGenerator(t,forms); 
	    			}
	    			node[leaf].setRHG(rhg); //sets colour null too  			
	    		}//end rhg is null
	    		
	    		if (rocg==null){	
	    			//now get first/next hue from rhg
	    			if (!(rhg.hasNext())){
	    				report.seol(1,"No (more) suitable hues: have to backtrack to parent");
	    				node[leaf].setROCG(null);
	    				return parent;
	    			}
					int fih=rhg.hue();
					report.seol(1,"A suitable hue is number "+fih);   	
					if (leaf==0) rocg=new RestrictedOrderedColourGenerator(fih,t,widthRestriction);
					else 
						rocg=new RestrictedOrderedColourGenerator(fih,t,node[parent].oc().getAllHueNumbers(),widthRestriction); //and any other hues in colour must succ from parent 
					node[leaf].setROCG(rocg);
	    		}//end rocg null
		    		
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


	    		//try a loop up instead of this node

	    		int depth=0;

	    		int cn=leaf;

	    		while (cn>0) {

	    			cn=node[cn].getParent();

	    			depth++;

	    		}

	    		boolean loopOk=false;

	    		int d=depth+1;

	    		while ((!loopOk) && (1<d)){

	    			d--;

	    			//try to loop up d steps

	    			int tn=leaf;

	    			for(int i=0;i<d;i++) tn=node[tn].getParent();

	    			if (node[tn].oc().match(rocg.oc())){

	    				

	    				node[parent].givesucc(dirn,tn);

	    				loopOk=tryLoop(parent,dirn,tn);

	    				if (!loopOk) //put back leaf

	    					node[parent].givesucc(dirn,leaf);

	    			}

	    		}

                if (loopOk) return -3;


	    			//if no loop works then paint the node         

	    			//give it new successors

	    			int numh=rocg.oc().size();

	    			Node[] tmp=new Node[numh+node.length];

	    			for(int i=0;i<node.length;i++) tmp[i]=node[i];

	    			tmp[leaf].branch(numh);

            	

	    			for(int i=0;i<numh;i++){

	    				tmp[node.length+i]=new Node(leaf,i);

	    				tmp[leaf].givesucc(i,node.length+i);

	    			}

	    			node=tmp;

	    	return -2;	

	    }

	    

	    private boolean lookForLeaf(boolean sofar,int n){

	    	if (sofar) return true;

	    	int ns=node[n].numsucc();

	    	if (ns==0) return true;

	    	boolean tmp=false;

	    	for (int i=0;i<ns;i++){

	    		int si=node[n].succ(i);

	    		if (si>0)  //successor is not up a loop

	    			tmp=lookForLeaf(tmp,si);

	    	}

	    	return tmp;

	    }

	    //-1 means no leaves found
	    private int findNextLeaf(){
	    	if (Parameters.BFSon) return findTopLeaf();
	    	else return findBottomLeaf();
	    }
	    
	    
	    //-1 means no leaves found
	    private int findTopLeaf(){

             int[] shallowness=new int[node.length];

             int[] bestBelow=new int[node.length];

             for(int i=0;i<node.length;i++)

            	 if (node[i].numsucc()==0){

            		 shallowness[i]=0;

            		 bestBelow[i]=i;

            	 } else {

            		 shallowness[i]=node.length+1;

            		 bestBelow[i]=i;

            	 }

             boolean change=true;

             while (change){

            	 change=false;

                 for(int i=0;i<node.length;i++)

                	 if (node[i].numsucc()>0){

                		 for(int j=0;j<node[i].numsucc();j++){

                			 int k=node[i].succ(j);

                			 if (shallowness[k]<shallowness[i]-1){

                				 bestBelow[i]=bestBelow[k];

                				 shallowness[i]=shallowness[k]+1;

                				 change=true;

                			 }

                		 }



                	 }

             

             }

	    	if (shallowness[0]<=node.length)

	    		return bestBelow[0];

	    	else return -1; //no leaves found

	    }
	    
	    //-1 means no leaves found
	    private int findBottomLeaf(){

             int[] depth=new int[node.length];
             
             for(int i=0; i<node.length; i++)
            	 depth[i]=-1;
             
             depth[0]=0;
             
             spreadDepth(0,depth);

             int best=-1;

             for(int i=0;i<node.length;i++)

            	 if (node[i].numsucc()==0){
            		 
            		 if (best==-1) best=i;

            		 else if (depth[i]>depth[best]) best=i;

            	 } 



	    	if (best>-1)

	    		return best;

	    	else return -1; //no leaves found

	    }
	    
	    private void spreadDepth(int n,int[] depth){
       	 if (node[n].numsucc()>0){

    		 for(int j=0;j<node[n].numsucc();j++){
    			 int k=node[n].succ(j);
    			 if (depth[k] ==-1) {
    				 depth[k]=depth[n]+1;
    				 spreadDepth(k,depth);
    			 }
    		 } 	
    		 }
	    }

	    

	    public String toString(){

	    	String s="There are "+node.length+" nodes\n";

	    	for(int i=0;i<node.length;i++)

	    		s=s+"Node "+i+"\n"+node[i]+"\n";

	    	return s;

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

	    	//	report.seol(2,"PH round for number "+rd+" ie "+sfi[rd]+" "+t.getSubformulas().getFormula(sfi[rd]));

  			//debug
	    	/*	
  			for(int i=0; i< node.length; i++)
  				for (int j=0;j<ph[i].length;j++){
  					report.seol(2, "debug: ph["+i+"]["+j+"]="+ph[i][j]);
  					int ns=ph[i][j].numsucc();
  					report.seol(2, "debug: its "+ns+" successors are: ");
  					for(int k=0;k<ns;k++){
  						report.seol(2, "debug: "+k+"th is "+ph[i][j].getSucc(k));
  					}
  				}  */

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
	    	//report.seol(2,"debug11: evenst are: "+list(events));
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
	
	private int tidyUp(int old){
		if (node.length==0) return 0;
		int[] nume = new int[node.length];
		int[] back= new int[node.length];
		for (int i=0; i<node.length; i++) nume[i]=-1;
		
		boolean change=true;
		nume[0]=0;
		back[0]=0;
		int used=1;
		while (change){
			change=false;
			for(int i=0;i<node.length;i++){
				if (nume[i]>-1){
					for (int j=0;j<node[i].numsucc();j++){
						int k=node[i].succ(j);
						if (nume[k]==-1){
							back[used]=k;
							nume[k]=used++;
							change=true;
						}
					}
				}
			}
		}//end while change
		
		Node[] tmp=new Node[used];
		tmp[0]=node[0];
	    for(int i=0;i<used;i++){
	    	for(int j=0;j<node[back[i]].numsucc();j++){
	    		int oj=node[back[i]].succ(j);
	    		tmp[i].givesucc(j,nume[oj]);
	    		if (nume[oj]>i) {
	    			
	    			tmp[nume[oj]]=node[oj];
	    			tmp[nume[oj]].setParent(i,j);
	    		}
	    		
	    	}
	    }
	    
	    node=tmp;
        return nume[old];
		
	}
	
	public static String list(int[] a){
		
		String s="{";
		for (int i=0;i<a.length;i++){
			s=s+", "+a[i];
		}
		return s+"}";
	}

	    

	private boolean repetitiveAboveNew(int leaf){
		if (leaf==0) return false;
		
		int d=0;
		int at=leaf;
		while (at !=0){
			d++;
			at=node[at].getParent();
		}
		int[] br=new int[d+1];
		at=leaf;

		report.seol(2,"Colours along branch up from leaf are:");
		for(int i=0;i<d+1;i++){
			br[d-i]=at;
			report.seol(2," "+at+" "+node[at].oc());
			at=node[at].getParent();
		}
		
		
		if (Parameters.RepChkSHRon && immSingRepCheck(br)) {
			report.seol(2,"Rep Check: immediate single hue repeat! Not allowed.");
			return true;
		}
		if (Parameters.RepChkICRon && immMultiRepCheck(br)) {
			report.seol(2,"Rep Check: immediate colour repeat with id traceback! Not allowed.");
			return true;
		}
		if (Parameters.RepChkSHIRon && singRepCheck(br)) {
			report.seol(2,"Rep Check: single hue interval repeat! Not allowed.");
			return true;
		}
		if (Parameters.RepChkLFBIRon && leftmostBoring(br)) {
			report.seol(2,"Rep Check: leftmost boring interval repeat! Not allowed.");
			return true;
		}
		//borRepCheck
		if (Parameters.RepChkGBIRon && borRepCheck(br)) {
			report.seol(2,"Rep Check: general boring interval repeat! Not allowed*.");
			return true;
		}
		
		
		report.seol(2,"Rep Check finished.");
		return false;
		
	}//edn rep
	
	//same one hue colour repeated immediately
	private boolean immSingRepCheck(int[] br){
		int brl=br.length;
		if (brl<2) return false;
		OrderedColour endcol=node[br[brl-1]].oc();
		if (endcol.size()>1) return false;
		if (endcol.match(node[br[brl-2]].oc())) return true;
		return false;
	}
	
	//same colour repeated imm, hue traceback id
	private boolean immMultiRepCheck(int[] br){
		int brl=br.length;
		OrderedColour endcol=node[br[brl-1]].oc();
		if (!endcol.match(node[br[brl-2]].oc())) return false;
		int[] phues=node[br[brl-2]].oc().getAllHueNumbers();
		int numhue=endcol.size();
		boolean[] id= new boolean[numhue];
		for(int i=0;i<numhue;i++){
			int hue= endcol.getHue(i);
			int htb=t.traceBack(hue,phues);
			if (htb != hue) return false;
		}
		return true;
		
	}
	
	//same one hue colour repeated with nothing new inbetween
	private boolean singRepCheck(int[] br){
		int brl=br.length;
		if (brl<2) return false;
		OrderedColour endcol=node[br[brl-1]].oc();
		if (endcol.size()>1) return false;
		int indend= brl-1;
		int at=indend;
		int mid=-1;
		int top=-1;
		boolean done=false;
		boolean rep=false;
		while (!done){
			at--;
			if (at<1) done=true;
			else if ((mid<0) && (node[br[at]].oc().size()>1)) done=true; //don't allow branching in the bottom part
			else if (endcol.match(node[br[at]].oc())) {
				if (mid<0) mid=at;
				else {
					top=at;
					rep=true;
				}
			}
		}
		if (!rep) return false;
		boolean allCovered=true;
		for(int i=mid+1;i<indend;i++){
			boolean covered=false;
			for (int j=top;j<mid;j++){
				if ((node[br[i]].oc()).match(node[br[j]].oc())) covered=true;
			}
			if (!covered) allCovered=false;
		}
		if (!allCovered) return false;
		report.seol(2,"boring interval discovered wrt one hue colour");
		return true;
	}
	
	//same colour repeated on leftmost branch with nothing new inbetween
	private boolean leftmostBoring(int[] br){
		int brl=br.length;
		if (brl<2) return false;
		OrderedColour endcol=node[br[brl-1]].oc();
		int indend= brl-1;
		int at=indend;
		int mid=-1;
		int top=-1;
		boolean done=false;
		boolean rep=false;
		while (!done){
			if (node[br[at]].getDirn()!=0) {
				done=true; //not a leftmost branch
			}
			at--;
			if (at<1) done=true;
			else  if (endcol.match(node[br[at]].oc())) {
				if (mid<0) mid=at;
				else {
					top=at;
					rep=true;
				}
			}
		}
		if (!rep) return false;
		boolean allCovered=true;
		for(int i=mid+1;i<indend;i++){
			boolean covered=false;
			for (int j=top;j<mid;j++){
				if ((node[br[i]].oc()).match(node[br[j]].oc())) covered=true;
			}
			if (!covered) allCovered=false;
		}
		if (!allCovered) return false;
		report.seol(2,"boring leftmost interval discovered");
		return true;
	}
	
	//same  colour repeated 3 times  with nothing new inbetween last 2 repts
	//note that I have not yet proved that it is correct to prevent this
	private boolean borRepCheck(int[] br){
		int brl=br.length;
		if (brl<2) return false;
		OrderedColour endcol=node[br[brl-1]].oc();
		
		int indend= brl-1;
		int at=indend;
		int mid=-1;
		int top=-1;
		boolean done=false;
		boolean rep=false;
		while (!done){
			at--;
			if (at<1) done=true;
			else if (endcol.match(node[br[at]].oc())) {
				if (mid<0) mid=at;
				else {
					top=at;
					rep=true;
				}
			}
		}
		if (!rep) return false;
		
		boolean allCovered=true;
		for(int i=mid+1;i<indend;i++){
			boolean covered=false;
			for (int j=top;j<mid;j++){
				if ((node[br[i]].oc()).match(node[br[j]].oc())) covered=true;
			}
			if (!covered) allCovered=false;
		}
		if (!allCovered) return false;
		report.seol(2,"general boring interval discovered");
		return true;
	}
	
	
	private boolean tooWide(int n){
		if (widthRestriction==-1) return false;
		int wide=node[n].oc().size();
		return (wide>widthRestriction);
	}
	
	public int depth(int n){
	
		int d=0;
		int at=n;
		while (at !=0){
			d++;
			at=node[at].getParent();
		}
	return d;
	}
	
	private boolean tooDeep(int n){
		if (depthRestriction==-1) return false;
		return (depth(n)>depthRestriction);
	}
	
	public Node getNode(int i){
		return node[i];
	}





	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return node.length;
	}



	public ClosureSubset2 getHue(int hue) {
		// TODO Auto-generated method stub
		return t.getHue(hue);
	}





	public FormulaTree getPhi() {
		// TODO Auto-generated method stub
		return t.getOwner();
	}





	public int getTraceback(int shn, int[] allHueNumbers) {
		// TODO Auto-generated method stub
		return t.traceBack(shn,allHueNumbers);
	}
	
	}//end class



