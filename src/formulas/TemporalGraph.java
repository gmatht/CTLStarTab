/*
 * TemporalGraph.java
 *
 * Created on 29 May 2005, 17:14
 */

package formulas;

/**
 *
 * @author  mark
 */
public class TemporalGraph {
    
    FormulaTree f;
    Subformulas sf;
    ClosureSubset2[] mtcs;
    int[][] succ; //the RX relation, the list of successors 
    int[] initial; //contains f
    int[][] equiv;  //the RA reln
    
    int[] eqClass; //index of first member of each equiv class
    PerfectHues ph;
    Report report;
    
    public TemporalGraph(FormulaTree f){
        this(f,new ReportToScreen());
    }
    
    /** Creates a new instance of TemporalGraph */
    public TemporalGraph(FormulaTree f, Report r) {
    	this.f=f;
        report=r;
        Logic logic=f.getLogic();
         
        sf=new Subformulas(f);
        report.seol(1,"It has "+sf.count()+" subformulas.");
        for(int i=0;i<sf.count();i++)
            report.seol(3,"sf["+i+"]= "+sf.getFormula(i).toString());
                
        PosSubF p= new PosSubF(sf);
        
         ClosureSubset2 c=new ClosureSubset2(f,sf,p);
         report.seol(3,"The hues are:");
         int count=0;
         while (!c.isLast()){
		Timeout.yield();
                if (c.isMTC()) {
                    report.seol(3,""+(count)+": "+c); 
                    count++;
                }
                c.makeNext();
         }
         if (c.isMTC()) {
             report.seol(3,""+(count)+": "+c); 
             count++;
         }
         report.seol(3,"That is all of the hues.");
         report.seol(2,"There are "+count+" hues.");
         
         mtcs=new ClosureSubset2[count];
         c=new ClosureSubset2(f,sf,p);
         count=0;
         while (!c.isLast()){
		Timeout.yield();
                if (c.isMTC()) mtcs[count++]=c; 
                c=c.next();
         }
         if (c.isMTC()) mtcs[count++]=c; 
         
         report.seol(1,"Computing rX");
         report.seol(3,"The steps to relation contains the following:");
         succ=new int[count][];
         for (int i=0; i<mtcs.length;i++){
             report.sol(4,""+i+" steps to ");
             int ct=0;
             for(int j=0;j<mtcs.length;j++)
                 if (logic.precs(mtcs[i],mtcs[j])){
                    //report.mol(" "+j); HACK, make this configurable!!
                    ct++;
                 }
             report.eol("");
             succ[i]=new int[ct];
             ct=0;
             for(int j=0;j<mtcs.length;j++)
                 if (logic.precs(mtcs[i],mtcs[j])){
                    succ[i][ct]=j;
                    ct++;
                 }
             
         }
         report.seol(3,"That is all of the step to.");
         
         
         //print out initials mtcs
         int ci=0;
         for(int i=0;i<mtcs.length;i++)
             if (mtcs[i].isInitial()){
                 report.seol(3,"mtcs["+i+"] is initial.");
                 ci++;
             }
         initial=new int[ci];
         ci=0;
         for(int i=0;i<mtcs.length;i++)
             if (mtcs[i].isInitial()){
                 initial[ci++]=i;
             }
         
         
         report.seol(3,"The RA equiv relation contains the following:");
         equiv=new int[count][];
         for (int i=0; i<mtcs.length;i++){
             report.sol(4,""+i+" is equiv to ");
             int ct=0;
             for(int j=0;j<mtcs.length;j++)
                 if (((AUXLogic)logic).equiv(mtcs[i],mtcs[j])){
                    report.mol(" "+j);
                    ct++;
                 }
             report.eol("");
             equiv[i]=new int[ct];
             ct=0;
             for(int j=0;j<mtcs.length;j++)
                 if (((AUXLogic)logic).equiv(mtcs[i],mtcs[j])){
                    equiv[i][ct]=j;
                    ct++;
                 }
             
         }
         report.seol(3,"That is all of equiv.");
         
         report.seol(2,"Working out what the equiv classes are.");
         int ect=0;
         for (int i=0; i<mtcs.length;i++)
             if (equiv[i][0]==i) ect++;
         eqClass=new int[ect];
         ect=0;
         for (int i=0; i<mtcs.length;i++)
             if (equiv[i][0]==i) eqClass[ect++]=i;
         report.seol(3,"The "+ect+" equiv classes are.");
         for(int i=0;i<ect;i++){
             report.sol(4,"");
             int m=eqClass[i];
             for(int j=0;j<equiv[m].length;j++)
                 report.mol(" "+equiv[m][j]);
             report.eol("");
         }//end i
         
         report.seol(2,"Working out what E defects appear in the equiv classes.");
         for(int i=0; i<eqClass.length;i++){
             report.seol(4,"The class of "+eqClass[i]);
             int m=eqClass[i];
             for(int j=0;j<sf.count();j++)
                 if ((mtcs[m].member(j)) && (sf.topChar(j)=='-') && (sf.topChar(sf.left(j))=='A')){
                     report.sol(5,sf.getFormula(j)+" ");
                     int cure=sf.negn(sf.left(sf.left(j)));
                     report.mol(" cured by "+sf.getFormula(cure)+" in ");
                     for(int k=0;k<equiv[m].length;k++)
                         if (mtcs[equiv[m][k]].member(cure))
                             report.mol(" "+equiv[m][k]);
                     report.eol("");
                 
             }//end for j
         }//end for i
         

         ph=new PerfectHues(this);
         
         
    }//end constructor
    
    public FormulaTree getOwner(){
        return f;
    }
    
    public int numEquivClasses(){
        return eqClass.length;
    }
    
    public int sizeOfEqClass(int i){
        return equiv[eqClass[i]].length;
    }
    
    public int[] contentsOfEquivClass(int i){
        return equiv[eqClass[i]];
    }
    
    public boolean contains(int m,int sbf){
        return mtcs[m].member(sbf);
    }
    
    public Subformulas getSubformulas(){
        return sf;
    }
    
    public boolean succ(int i, int j){
        int k=0;
        while ((k<succ[i].length) && !(succ[i][k]==j)) k++;
        return (k<succ[i].length);
    }
    
    
    public static void main(String[] args){
        try {
            
            Logic logic=new AUXLogic();
            System.out.println("Enter formula:");
            String s=Keyboard.getInput();
            FormulaTree f=logic.parse(s);
            System.out.println(f.toString());
            
            f=logic.disabbreviate(f);
            System.out.println("Disabbreviates to = "+f);
            
            System.out.println("which has length = "+f.length());
            
            TemporalGraph t=new TemporalGraph(f);
            
            System.out.println("working through colours.");
            
            Colour c=new Colour(t);
            c.findFirstCandidate();
            int ctr=0;
            while (!c.allDone()){
                if (c.isAColour()){
                    ctr++;
                    //System.out.print("colour["+(ctr)+"]="+c.toString()+" is ");
                    //System.out.println(" a colour.");
                }
                c=c.nextCandidate();
            }
            System.out.println("worked through all "+ctr+" colours. Now collect them.");
            
            
            Colour[] colour=new Colour[ctr];
            c=new Colour(t);
            c.findFirstCandidate();
            ctr=0;
            while (!c.allDone()){
                if (c.isAColour()){
                    colour[ctr++]=c;
                }
                c=c.nextCandidate();
            }
            System.out.println("Got 'em.");
            
            System.out.println("The big precs reln is as follows:");
            int[][] csucc=new int[colour.length][];
            for(int i=0;i<colour.length; i++){
                System.out.print("colour["+i+"]="+colour[i].toString()+" precs colours ");
                int cp=0;
                for (int j=0;j<colour.length;j++)
                    if ((colour[i]).precs(colour[j])){
                        System.out.print(" "+j);
                        cp++;
                    }
                System.out.println();
                csucc[i]=new int[cp];
                cp=0;
                for (int j=0;j<colour.length;j++)
                    if ((colour[i]).precs(colour[j])){
                        csucc[i][cp]=j;
                        cp++;
                    }
            }
            System.out.println("end of precs");
            
          
            t.process(colour,csucc);
            
        } catch(ParseException e){
            System.out.println(e.getMessage());
        }
    }//end main
    
/**
 * Returns true if formula is satisfiable
 */
    
    public boolean process(Colour[] colour,int[][] csucc){
        boolean[] stin=new boolean[colour.length];
        for(int i=0; i<stin.length; i++) stin[i]=true;
        
        //gradually prune the graph tableau
        boolean change=true;
        boolean badend=false;
        
        int roundno=0;
        report.heading(1,"Starting processing of tableau.");
        while ( change && !badend ){
        	Timeout.yield();

            change=false;
            roundno++;
            
            //check for no initial colours
            boolean found=false;
            for(int i=0;i<initial.length;i++){
                for(int j=0;j<colour.length;j++)
                if ((colour[j].contains(initial[i])) && (stin[j])) {
                    report.seol(3,"Initial colour "+(j)+" left.");
                    found =true;
                }
            }
            if (!found){
                report.seol(2,"No initial states left!");
                badend=true;
            }
            
            if (!badend){
                
                //each hue of each colour must have an extant successor
                report.seol(2,"Checking for extant successors.");
                for(int i=0;i<colour.length;i++) if (stin[i]) {
                    int[] cont=colour[i].contents();
                    for(int j=0;j<cont.length;j++){
                        int hue=cont[j];
                        boolean exsuccfd=false;
                        for(int k=0;k<csucc[i].length;k++) if (stin[csucc[i][k]]){
                            int[] c2=colour[csucc[i][k]].contents();
                            for(int p=0;p<c2.length;p++)
                                if (succ(hue,c2[p])) exsuccfd=true;
                        }//end k  
                        if (!exsuccfd){
                            report.seol(3,"Colour "+(i)+" out as no successor for hue "+hue+" left.");
                            stin[i]=false;
                            change=true;
                        }
                    }//end j
                }//end i
                
                
                //now check that eventualities are fulfillable
                report.heading(2,"Starting this round of eventuality checking.");
                int cte=0;
                for(int i=0;i<colour.length; i++) if (stin[i]){
                    int[] cont=colour[i].contents();
                    for(int j=0;j<cont.length;j++){
                        int hue=cont[j];
                        for(int e=0;e<sf.count();e++)
                            if ((f.getLogic()).isEventuality(sf.getFormula(e)))
                                if (mtcs[hue].member(e)){
                                    cte++;
                                    report.seol(3,""+cte+") Colour "+(i)+", hue "+hue+" contains "+sf.getFormula(e));
                                    int cure=sf.right(e);
                                    if (mtcs[hue].member(cure)){
                                        report.seol(4,"Cured by "+sf.getFormula(cure)+" here.");
                                    }
                                        else report.seol(4,"Not cured here.");
                                    
                                }
                    }
                    
                }
                report.seol(2,"There are "+cte+" eventuality instances to check.");
                //end set up eventualities
                
                Eventuality[] ev=new Eventuality[cte];
                report.heading(3,"Now collecting this information.");
                cte=0;
                for(int i=0;i<colour.length; i++) if (stin[i]){
                    int[] cont=colour[i].contents();
                    for(int j=0;j<cont.length;j++){
                        int hue=cont[j];
                        for(int e=0;e<sf.count();e++)
                            if ((f.getLogic()).isEventuality(sf.getFormula(e)))
                                if (mtcs[hue].member(e)){
                                    
                                    ev[cte]=new Eventuality(i,hue,e);
                                    
                                    int cure=sf.right(e);
                                    if (mtcs[hue].member(cure)){
                                        ev[cte].cured=true;
                                    }
                                    
                                    cte++;
                                }//end of for e
                    }//end for j
                }//end for i
                report.seol(3,"Finished collection.");
                
                //now look for successors
                for (int e=0;e<cte; e++){
                    int numsucc=0;
                    for (int e2=0;e2<cte; e2++){
                        if (colour[ev[e].colour].precs(colour[ev[e2].colour]))
                            if ((f.getLogic()).precs(mtcs[ev[e].hue],mtcs[ev[e2].hue]))
                                if (ev[e].sbf==ev[e2].sbf){
                                    numsucc++;
                                }
                    }
                    ev[e].succ=new int[numsucc];
                    numsucc=0;
                    for (int e2=0;e2<cte; e2++){
                        if (colour[ev[e].colour].precs(colour[ev[e2].colour]))
                            if ((f.getLogic()).precs(mtcs[ev[e].hue],mtcs[ev[e2].hue]))
                                if (ev[e].sbf==ev[e2].sbf){
                                    ev[e].succ[numsucc]=e2;
                                    numsucc++;
                                }
                    }
                        
                        
                }//end for e
                
                //now check em
                report.seol(2,"Starting loops of spreading cures backwards.");
                int loopno=0;
                boolean stable=false;
                while (!stable){
                	Timeout.yield();

                    report.seol(4,"Starting loop "+loopno);
                    stable=true;
                    
                    for (int e=0; e<cte; e++){
                        if (!ev[e].cured){
                            for(int y=0; y<ev[e].succ.length; y++){
                                if (ev[ev[e].succ[y]].cured){
                                    int e2=ev[e].succ[y];
                                    stable=false;
                                    ev[e].cured=true;
                                    report.seol(4,"Colour "+(ev[e].colour)+", hue "+(ev[e].hue)+" containing "+sf.getFormula(ev[e].sbf)
                                    +" cured by step to "+
                                    "colour "+(ev[e2].colour)+", hue "+(ev[e2].hue));
                                }
                            }
                        }
                    }//end for e
                    
                    //just for debugging
                    for (int e=0; e<cte; e++)
                        if (!ev[e].cured){
                                    report.seol(5,"Colour "+(ev[e].colour+1)+", hue "+(ev[e].hue)+" containing "+sf.getFormula(ev[e].sbf)
                                    +" still uncured.");
                            }
                    
                    loopno++;
                }//end of stable loop
                report.seol(3,"Ended loops of spreading cures backwards.");
                
                //remove colours with uncured eventualities
                for (int e=0; e<cte; e++){
                        if (!ev[e].cured){
                            int cl=ev[e].colour;
                            report.seol(3,"Colour "+(cl)+" out as hue "+ev[e].hue+" has unfulfillable "+sf.getFormula(ev[e].sbf)+".");
                            stin[cl]=false;
                            change=true;
                        }
                    }//end for e
                
            }//end badend
            report.seol(2,"End of round "+roundno);
            
        }//end while
        report.seol(1,"End of process. There were "+roundno+" rounds.");
        if (badend)  report.seol(1,"The formula is unsatisfiable.");
        else {
            report.seol(1,"The formula is satisfiable.");
            tellUsAModel(colour,stin);
        }
        report.close();
	return (!badend);
    }//end process
    

    public int getMaxEquivClassSize(){
        int m=0;
        for(int i=0; i<eqClass.length; i++)
            if (m<equiv[eqClass[i]].length)
                m=equiv[eqClass[i]].length;
        return m;
    }
    
    
    private void tellUsAModel(Colour[] colour, boolean[] stin){
        report.seol(1,"Now let us describe one model as an example.");
        report.seol(1,"The colours in the model are:");
        for (int i=0; i<colour.length; i++)
            if (stin[i]) report.seol(2,"Colour["+i+"]");
        for(int i=0;i<initial.length;i++){
                for(int j=0;j<colour.length;j++)
                if ((colour[j].contains(initial[i])) && (stin[j])) {
                    report.seol(2,"Initial colour "+(j)+" left.");
                }
            }
    }
    
    public int numHues(){
    	return mtcs.length;
    }
    
    public ClosureSubset2 getHue(int n){
    	return mtcs[n];
    }
    
    public boolean anyInitialHues(){
    	boolean answ=false;
    	for(int i=0;i<mtcs.length;i++)
    		if (mtcs[i].isInitial()) answ=true;
    	return answ;
    }
    
    public int firstInitialHue(){
    	int answ=-1;
    	for(int i=0;i<mtcs.length;i++){
			if ((mtcs[i].isInitial() && (answ<0))) answ=i;
    	}
    	return answ;
    
    }
    
    //tells us the subforms which must be in any successor hue of h
    public int[] reqdNext(int h){
    	
    	//report.seol(2,"Debug: trying to find reqNext of hue number "+h);
    	
    	int sfc=sf.count();
    	boolean[] ndd=new boolean[sfc];
    	for(int i=0;i<sfc;i++) ndd[i]=false;
    	for(int i=0;i<sfc;i++){
    		
    		
    		// pUq and -q in this => pUq in other
            if (sf.topChar(i) =='U'){
                if ((contains(h,i)) && (contains(h,sf.negn(sf.right(i)))))
                   ndd[i]=true;
            }
            
            // -(pUq) and p in this => -(pUq) in other
            if ((sf.topChar(i)=='-') && (sf.topChar(sf.left(i))=='U')){
                if ((contains(h,i)) && (contains(h,sf.left(sf.left(i))))) ndd[i]=true;
            }
            
            //X\alpha in this -> \alpha in other
            if (sf.topChar(i)=='X'){
                if ((contains(h,i))) ndd[sf.left(i)]=true;
            }
            
            //-X\alpha in this -> -\alpha in other
            if ((sf.topChar(i)=='-') && (sf.topChar(sf.left(i))=='X')){
                if ((contains(h,i))) ndd[sf.negn(sf.left(sf.left(i)))]=true;
            }
    	}//end for
    	
    	int ct=0;
    	for(int i=0;i<sfc;i++) if(ndd[i]) ct++;
    	int[] answ=new int[ct];
    	ct=0;
    	for(int i=0;i<sfc;i++) if(ndd[i]) answ[ct++]=i;
    	
    	//report.seol(2,"Debug: following forms ");
    	//for(int i=0;i<ct;i++) report.seol(2," "+answ[i]);
    	
    	return answ;
    	
    }//end reqdNext
    
    public void tell(String s){
    	report.seol(2,s);
    }
    
    //what eventualties are raised by hue number hn
    public int[] getEvents(int hn){
    	int ctr=0;
    	for(int i=0; i<sf.count(); i++){
    	
    	  if ((f.getLogic()).isEventuality(sf.getFormula(i))){
    		  if (contains(hn,i)) ctr++;
    	  }
    		  
    	}
    	
    	int[] e=new int[ctr];
    	ctr=0;
    	for(int i=0; i<sf.count(); i++){
        	
      	  if ((f.getLogic()).isEventuality(sf.getFormula(i))){
      		  if (contains(hn,i)) e[ctr++]=sf.right(i);
      	  }
      		  
      	}
    	return e;
    }
    
    public int traceBack(int hue, int[] phues){
    	for(int i=0;i<phues.length;i++){
    		if (rx(phues[i],hue)) return phues[i];
    	}
    	return -1;
    }
    
    public boolean rx(int hn,int hnb){
    	int l=succ[hn].length;
    	for (int i=0;i<l;i++)
    		if (succ[hn][i]==hnb) return true;
    	return false;
    }
    
    public boolean isPerfect(int i){
    	return ph.isPerfect(i);
    }
    
    public int[] getAllEvents(){
    	
    	int ctr=0;
    	for(int i=0; i<sf.count(); i++){
    	
    	  if ((f.getLogic()).isEventuality(sf.getFormula(i))){
    		  ctr++;
    	  }
    		  
    	}
    	
    	int[] e=new int[ctr];
    	ctr=0;
    	for(int i=0; i<sf.count(); i++){
        	
      	  if ((f.getLogic()).isEventuality(sf.getFormula(i))){
      		  e[ctr++]=sf.right(i);
      	  }
      		  
      	}
    	return e;
    }
    
    public int[] getAllUntils(){
    	
    	int ctr=0;
    	for(int i=0; i<sf.count(); i++){
    	
    	  if ((f.getLogic()).isEventuality(sf.getFormula(i))){
    		  ctr++;
    	  }
    		  
    	}
    	
    	int[] e=new int[ctr];
    	ctr=0;
    	for(int i=0; i<sf.count(); i++){
        	
      	  if ((f.getLogic()).isEventuality(sf.getFormula(i))){
      		  e[ctr++]=i;
      	  }
      		  
      	}
    	return e;
    }
    
    public int[] getAtForms(){
    	int ctr=0;
    	for (int i=0;i<sf.count(); i++)
    		if (((AUXLogic)f.getLogic()).isAtom(sf.getFormula(i).topChar())) ctr++;
    	int[] ans=new int[ctr];
    	ctr=0;
    	for (int i=0;i<sf.count(); i++)
    		if (((AUXLogic)f.getLogic()).isAtom(sf.getFormula(i).topChar())) ans[ctr++]=i;
    	return ans;
    }

    
}//end class
