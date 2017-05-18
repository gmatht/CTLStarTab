package quicktab;

import formulas.Logic;
import formulas.PartialHue;
import formulas.Subformulas;
import formulas.ThreadRec;

public class LG {
	
	private TransitionStructure base;
	Subformulas sf;
	int[] sfi; //sf nums in inc order of cmplxty
	private boolean noNonLeafNodes=true;
	
	private PartialHue[][] ph;
	
	public LG(TransitionStructure qt){
		
		base=qt;
		
		sf=new Subformulas(base.getOwner());
		ph=new PartialHue[base.numNodes()][];
    	sfi=sf.sortinc();
    	int c=sfi.length;

    	for(int i=0;i<base.numNodes();i++ )if (base.getNode(i)!=null){

    		ph[i]= new PartialHue[1];

    		ph[i][0]=new PartialHue(c,i); //empty one

    	}

    	for(int i=0;i<base.numNodes();i++ ) if ((base.getNode(i)!=null) && (!base.getNode(i).isLeaf())){

    		noNonLeafNodes=false;
    		for(int j=0;j<base.getNode(i).numsucc();j++){
    			//sop("DEBUG: i,j, succ(j)"+i+", "+j+", "+base.getNode(i).succ(j));
    			ph[i][0].makeSucc(ph[base.getNode(i).succ(j)][0]);
    		}

    	}
    	
		//sop("Initial phues:"+this);
		//sop("");
			
	}
	
	public boolean check(){
		
		if (noNonLeafNodes) return true;
		
		int c=sfi.length;
    	
    	for(int i=0;i<c;i++){
    		//sop("update by "+sf.getFormula(sfi[i]));
    		update(i); //update by the ith sorted fmla
    		//sop(""+this);
    		//sop("");
    	}
		
		return matches();
	}

	private boolean matches() {
		//check every label hue is a subset of an actual partial hue at same node
		//only need check nonleaves
		boolean match=true;
		for(int i=0;i<base.numNodes();i++) 
			if ((base.getNode(i)!=null) && (!base.getNode(i).isLeaf())){
			for(int j=0;j<base.getNode(i).numHues();j++){
				boolean issubsetofone=false;
				for(int k=0;k<ph[i].length;k++){
					boolean allin=true;
					for(int f=0;f<sf.count();f++){
						if ((base.getNode(i).getLabel(j).member(f)) 
								&& (!ph[i][k].has(f))) allin=false;
					}
					if (allin) issubsetofone=true;
				}
				if (!issubsetofone) match=false;
			}
		}
		
		/**
		//also check every actual partial hue is superset of a label hue
		for(int i=0;i<base.numNodes();i++) 
			if ((base.getNode(i)!=null) && (!base.getNode(i).isLeaf())){
				for(int k=0;k<ph[i].length;k++){
					boolean issupsetofone=false;
					for(int j=0;j<base.getNode(i).numHues();j++){
						boolean allin=true;
						for(int f=0;f<sf.count();f++){
							if ((base.getNode(i).getLabel(j).member(f)) 
									&& (!ph[i][k].has(f))) allin=false;
						}
						if (allin) issupsetofone=true;
					}
					if (!issupsetofone) match=false;
				}
			}
			**/
		
		return match;
	}

	private void update(int rd) {
		char tc=sf.topChar(sfi[rd]);


    		if ((('a'<=tc) && (tc<='z'))){

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				if (!base.getNode(i).isLeaf()){

    				//put sfi[rd] into all phs if atom is in any hue label at node i

    				if (base.getNode(i).getLabel(0).member(sfi[rd])){
    						//(t.contains(base.getNode(i).oc().getFirstHue(),sfi[rd])){

    					for(int k=0;k<ph[i].length;k++) 

    						ph[i][k].add(sfi[rd]);			

    				}

    				}

    			}

    		} else if (tc=='1'){

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				if (!base.getNode(i).isLeaf()){

    				//put sfi[rd] into all phs 

					for(int k=0;k<ph[i].length;k++) 

						ph[i][k].add(sfi[rd]);	

    				}

    			}

    		} else if (tc=='-'){

    			int pf=sf.left(sfi[rd]);

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				if (!base.getNode(i).isLeaf()){

    				//put sfi[rd] into all phs which do not have pf

					for(int k=0;k<ph[i].length;k++) {

						if (!ph[i][k].has(pf)) ph[i][k].add(sfi[rd]);

					}

    				}

    			}	

    		} else if (tc=='&'){

    			int lf=sf.left(sfi[rd]);

    			int rf=sf.right(sfi[rd]);

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				if (!base.getNode(i).isLeaf()){

    				//put sfi[rd] into all phs which have both lf and rf

					for(int k=0;k<ph[i].length;k++) {

						if ((ph[i][k].has(lf)) && (ph[i][k].has(rf))) ph[i][k].add(sfi[rd]);

					}

    				}

    			}	

    		} else if (tc=='X'){

    			int lf=sf.left(sfi[rd]);
    			

    			//make an array [][] of boolean to record poss and another for neg

    			boolean[][] addpos=new boolean[base.numNodes()][];

    			boolean[][] addneg=new boolean[base.numNodes()][];
    			

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				if (!base.getNode(i).isLeaf()){

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

							if (base.getNode(succnode).isLeaf()){

								//get Xp from the corresponding actual hue here

								//find the hue at base.getNode(i) which created need for base.getNode(succnode)

								int d=-1;
								for (int j=0;j<base.getNode(i).numsucc();j++)
									if (base.getNode(i).succ(j)==succnode) d=j;

								//see if Xp ie sfi[rd] is in that hue
								//t.contains(hn,sfi[rd])
								if (base.getNode(i).getLabel(d).member(sfi[rd])) 
									addpos[i][k]=true; else addneg[i][k]=true;

								

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
    			int[] kn=new int[base.numNodes()];
    			int[][] splitTo=new int[base.numNodes()][];
    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				kn[i]=ph[i].length;
    				splitTo[i]=new int[kn[i]];
    				splitTo[i][0]=-1;
    				
    				if (!base.getNode(i).isLeaf()){

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
    			
    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				if (!base.getNode(i).isLeaf()){

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

	    							if (!base.getNode(succnode).isLeaf()){

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

	      								//find the hue at base.getNode(i) which created need for base.getNode(succnode)

	      								int d=-1;

	      								for (int j=0;j<base.getNode(i).numsucc();j++)

	      									if (base.getNode(i).succ(j)==succnode) d=j;
	      								

	      								//see if Xp ie sfi[rd] is in that hue

	      								if (base.getNode(i).getLabel(d).member(sfi[rd])) {
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

    			boolean[][] addpos=new boolean[base.numNodes()][];

    			boolean[][] addneg=new boolean[base.numNodes()][];

    			//PartialHue[][][] er=new PartialHue[base.numNodes()][][];
    			
    			ThreadRec[][] tr=new ThreadRec[base.numNodes()][];

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				addpos[i]=new boolean[ph[i].length];

    				addneg[i]=new boolean[ph[i].length];

    				//er[i]=new PartialHue[ph[i].length][];
    				
    				tr[i]=new ThreadRec[ph[i].length];

    				for (int j=0;j<ph[i].length;j++){

    					addpos[i][j]=false;

    					addneg[i][j]=false;

    					//er[i][j]=new PartialHue[0];
    					tr[i][j]=new ThreadRec(getAllEvents(),getAllUntils());

    				}

    			}

				

    			//repeatedly loop thru all phs looking at their successors and

    			//noting need for adding new + or new - 

    			//until no more changes

    			boolean change=true;

    			while (change){

    				//report.seol(2,"into change loop again");

    				change=false;

	    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){
	    				
	    				if (!base.getNode(i).isLeaf()){

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
	    									
	    									if (base.getNode(onn).isLeaf()){
	    										
	    										//find the hue at base.getNode(i) which created need for base.getNode(succnode)

			      								int d=-1;

			      								for (int s=0;s<base.getNode(i).numsucc();s++)

			      									if (base.getNode(i).succ(s)==onn) d=s;
			      								//see if aUb ie sfi[rd] is in that hue

			      								if (base.getNode(i).getLabel(d).member(sfi[rd])) {
			      								
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
    									
    									if (base.getNode(onn).isLeaf()){
    										//find the hue at base.getNode(i) which created need for base.getNode(succnode)

		      								int d=-1;

		      								for (int s=0;s<base.getNode(i).numsucc();s++)

		      									if (base.getNode(i).succ(s)==onn) d=s;

		      								

		      								//see if aUb ie sfi[rd] is not in that hue

		      								if (!base.getNode(i).getLabel(d).member(sfi[rd])) {
		      								
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
	    									if (!base.getNode(onn).isLeaf()){
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
	    					    			 for(int ii=0;ii<base.numNodes();ii++){
	    					    			

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

    			int[][] splitTo =new int[base.numNodes()][];
    			int[] pl=new int[base.numNodes()]; //how many phs in i before splitting

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				

    				if (!base.getNode(i).isLeaf()){

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

    			

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				

    				if (!base.getNode(i).isLeaf()){

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

	    							if (!base.getNode(succnode).isLeaf()){

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
	    								//find the hue at base.getNode(i) which created need for base.getNode(succnode)

	      								int d=-1;

	      								for (int s=0;s<base.getNode(i).numsucc();s++)

	      									if (base.getNode(i).succ(s)==succnode) d=s;

	      								

	      								//see if aUb ie sfi[rd] is  in that hue

	      								if (base.getNode(i).getLabel(d).member(sfi[rd])) {
	      								
	      									
	      									
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

	    							if (!base.getNode(succnode).isLeaf()){

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

    							if (!base.getNode(succnode).isLeaf()){

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

    			for(int i=0;i<base.numNodes();i++) if (base.getNode(i)!=null){

    				if (!base.getNode(i).isLeaf()){

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
	}
	
	
	
	   //all the bs from aUbs
    public int[] getAllEvents(){
    	
    	Logic l=base.getOwner().getLogic();
    	
    	int ctr=0;
    	for(int i=0; i<sf.count(); i++){
    	
    	  if ((l).isEventuality(sf.getFormula(i))){
    		  ctr++;
    	  }
    		  
    	}
    	
    	int[] e=new int[ctr];
    	ctr=0;
    	for(int i=0; i<sf.count(); i++){
        	
      	  if ((l).isEventuality(sf.getFormula(i))){
      		  e[ctr++]=sf.right(i);
      	  }
      		  
      	}
    	return e;
    }
    
    public int[] getAllUntils(){
    	
    	int ctr=0;
    	for(int i=0; i<sf.count(); i++){
    	
    	  if ((base.getOwner().getLogic()).isEventuality(sf.getFormula(i))){
    		  ctr++;
    	  }
    		  
    	}
    	
    	int[] e=new int[ctr];
    	ctr=0;
    	for(int i=0; i<sf.count(); i++){
        	
      	  if ((base.getOwner().getLogic()).isEventuality(sf.getFormula(i))){
      		  e[ctr++]=i;
      	  }
      		  
      	}
    	return e;
    }
	
	private void sop(String s){
		System.out.println(s);
	}
	
	public String toString(){
		String a="";
		
		for(int i=0;i<ph.length;i++) if (ph[i]!=null)
			for (int j=0;j<ph[i].length;j++)
				sop(""+i+", "+j+": "+(ph[i][j]).toString(sf));
		
		return a;
	}

}
