/*
 * BTLTableau.java
 *
 * Created on 26 May 2005, 18:38
 */

package formulas;

/**
 *
 * @author  mark
 */
public class BTLTableau {
    
    
    FormulaTree f;
    Subformulas sf;
    ClosureSubset2[] mtcs;
    int[][] succ; //the RX relation
    int[] initial; //contains f
    int[][] equiv;  //the RA reln
    
    int[] eqClass; //index of first member of each equiv class
    
    
    
    /** Creates a new instance of Tableau */
    public BTLTableau(FormulaTree f) {
        this.f=f;
        Logic logic=f.getLogic();
         
        sf=new Subformulas(f);
        System.out.println("It has "+sf.count()+" subformulas:");
        for(int i=0;i<sf.count();i++)
                System.out.println("sf["+i+"]= "+sf.getFormula(i).toString());
                
        PosSubF p= new PosSubF(sf);
        
         ClosureSubset2 c=new ClosureSubset2(f,sf,p);
         System.out.println("The MTC closure subsets are:");
         int count=0;
         while (!c.isLast()){
                if (c.isMTC()) System.out.println(""+(count++)+": "+c); 
                c.makeNext();
         }
         if (c.isMTC()) System.out.println(""+(count++)+": "+c); 
         System.out.println("That is all of the MTC closure subsets.");
         
         mtcs=new ClosureSubset2[count];
         c=new ClosureSubset2(f,sf,p);
         count=0;
         while (!c.isLast()){
                if (c.isMTC()) mtcs[count++]=c; 
                c=c.next();
         }
         if (c.isMTC()) mtcs[count++]=c; 
         
         System.out.println("The steps to relation contains the following:");
         succ=new int[count][];
         for (int i=0; i<mtcs.length;i++){
             System.out.print(""+i+" steps to ");
             int ct=0;
             for(int j=0;j<mtcs.length;j++)
                 if (logic.precs(mtcs[i],mtcs[j])){
                    System.out.print(" "+j);
                    ct++;
                 }
             System.out.println();
             succ[i]=new int[ct];
             ct=0;
             for(int j=0;j<mtcs.length;j++)
                 if (logic.precs(mtcs[i],mtcs[j])){
                    succ[i][ct]=j;
                    ct++;
                 }
             
         }
         System.out.println("That is all of the step to.");
         
         //print out initials mtcs
         int ci=0;
         for(int i=0;i<mtcs.length;i++)
             if (mtcs[i].isInitial()){
                 System.out.println("mtcs["+i+"] is initial.");
                 ci++;
             }
         initial=new int[ci];
         ci=0;
         for(int i=0;i<mtcs.length;i++)
             if (mtcs[i].isInitial()){
                 initial[ci++]=i;
             }
         
         
         System.out.println("The RA equiv relation contains the following:");
         equiv=new int[count][];
         for (int i=0; i<mtcs.length;i++){
             System.out.print(""+i+" is equiv to ");
             int ct=0;
             for(int j=0;j<mtcs.length;j++)
                 if (((AUXLogic)logic).equiv(mtcs[i],mtcs[j])){
                    System.out.print(" "+j);
                    ct++;
                 }
             System.out.println();
             equiv[i]=new int[ct];
             ct=0;
             for(int j=0;j<mtcs.length;j++)
                 if (((AUXLogic)logic).equiv(mtcs[i],mtcs[j])){
                    equiv[i][ct]=j;
                    ct++;
                 }
             
         }
         System.out.println("That is all of equiv.");
         
         System.out.println("Working out what the equiv classes are.");
         int ect=0;
         for (int i=0; i<mtcs.length;i++)
             if (equiv[i][0]==i) ect++;
         eqClass=new int[ect];
         ect=0;
         for (int i=0; i<mtcs.length;i++)
             if (equiv[i][0]==i) eqClass[ect++]=i;
         System.out.println("The "+ect+" equiv classes are.");
         for(int i=0;i<ect;i++){
             int m=eqClass[i];
             for(int j=0;j<equiv[m].length;j++)
                 System.out.print(" "+equiv[m][j]);
             System.out.println();
         }//end i
         
         System.out.println("Working out what E defects appear in the equiv classes.");
         for(int i=0; i<eqClass.length;i++){
             System.out.println("The class of "+eqClass[i]);
             int m=eqClass[i];
             for(int j=0;j<sf.count();j++)
                 if ((mtcs[m].member(j)) && (sf.topChar(j)=='-') && (sf.topChar(sf.left(j))=='A')){
                     System.out.print(sf.getFormula(j)+" ");
                     int cure=sf.negn(sf.left(sf.left(j)));
                     System.out.print(" cured by "+sf.getFormula(cure)+" in ");
                     for(int k=0;k<equiv[m].length;k++)
                         if (mtcs[equiv[m][k]].member(cure))
                             System.out.print(" "+equiv[m][k]);
                     System.out.println();
                 
             }//end for j
         }//end for i
         
         
    }//end constructor
    
    
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
            
            BTLTableau t=new BTLTableau(f);
            t.process();
            
        } catch(ParseException e){
            System.out.println(e.getMessage());
        }
    }//end main
    
    public void process(){
        Logic logic=f.getLogic();
        
        boolean[] stin=new boolean[mtcs.length];
        for (int i=0;i<mtcs.length;i++) stin[i]=true;
        
        //determine eventualities
        int ce=0;
        for(int i=0;i<sf.count();i++)
            if (logic.isEventuality(sf.getFormula(i)))
                ce++;
        int[] ev=new int[ce];
        System.out.println("There are "+ce+" eventualities. They are:");
        ce=0;
        for(int i=0;i<sf.count();i++)
            if (logic.isEventuality(sf.getFormula(i))){
                ev[ce++]=i;
                System.out.println(""+(ce-1)+": "+sf.getFormula(i));
            }
        System.out.println("That is all.");
        boolean[][] evcured=new boolean[mtcs.length][ev.length];
            
        //gradually prune the graph tableau
        boolean change=true;
        boolean badend=false;
        
        while ( change && !badend ){
            change=false;
            
            //check for no initials
            boolean found=false;
            for(int i=0;i<initial.length;i++)
                if (stin[initial[i]]) found =true;
            if (!found){
                System.out.println("No initial states left!");
                badend=true;
            }
            
            if (!badend){
                //check for no successors
                for(int i=0;i<mtcs.length;i++){
                    if (stin[i]){
                     boolean fd=false;
                        for(int j=0;j<succ[i].length;j++)
                            if (stin[succ[i][j]]) fd=true;
                     if (!fd){
                            System.out.println(""+i+" out as no successor left.");
                            stin[i]=false;
                            change=true;
                        }
                    }
                }
                
                //check for E defects 
                //to be implemented
                for(int i=0;i<mtcs.length;i++) if (stin[i]) {
                    int e=0;
                    boolean outed=false;
                    while (!outed && (e<sf.count())){
                        if ((mtcs[i].member(e)) && (sf.topChar(e)=='-') && (sf.topChar(sf.left(e))=='A')){
                            int j=sf.negn(sf.left(sf.left(e)));
                            //now try to find cure which is j in some equiv
                            boolean cured=false;
                            for(int k=0;k<equiv[i].length;k++)
                                if ((stin[equiv[i][k]]) && mtcs[equiv[i][k]].member(j)) cured=true;
                            if (!cured){
                                System.out.println(""+i+" out as no cure for "+sf.getFormula(e)+" left.");
                                stin[i]=false;
                                change=true;
                                outed=true;
                            }
                           
                        }//end if
                        e++;
                        }//end while
                }//end of checking for uncured E defects
            
                //check for eventualities
                //set up whhat needs curing
                for(int i=0; i<mtcs.length; i++) if (stin[i])
                for(int e=0; e<ce;e++) 
                    evcured[i][e]=!logic.unfullfilled(mtcs[i],ev[e]);
                //do loop of spreading cures
                boolean stable=false;
             while (!stable){
                    stable=true;
                    for(int i=0;i<mtcs.length;i++) if (stin[i])
                        for(int j=0;j<succ[i].length;j++){
                            int isucc=succ[i][j];
                            if (stin[isucc]){
                                for(int e=0;e<ce;e++){
                                    if (evcured[isucc][e] && !evcured[i][e]){
                                        evcured[i][e]=true;
                                        stable=false;
                                    }
                                }
                            }
                        }//end for-j
                
                }//end while
                //remove states with uncured events
                for(int i=0;i<mtcs.length;i++) if (stin[i]) {
                    for(int e=0; e<ce; e++)
                     if (!evcured[i][e]){
                            System.out.println(""+i+" out as no cure for eventuality "+e);
                            stin[i]=false;
                            change=true;
                        }
                }
            }//end if !badend
            
            System.out.println("End of processing round.");
            
        }//end while
       
        if (badend)
            System.out.println("FINISHED: unsatisfiable");
        else
            System.out.println("FINISHED: satisfiable");
        
    }
    
}
