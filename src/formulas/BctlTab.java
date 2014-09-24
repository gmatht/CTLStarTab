/*
 * BctlTab.java
 *
 * Created on 20 June 2005, 10:54
 */

package formulas;

/**
 *
 * @author  mark
 */
public class BctlTab {
    
    Report report;

    public volatile int status=3; // unfinished.
    
    
    
    /** Creates a new instance of BctlTab */
    public BctlTab(String fs, Report r) {
        report=r;
        try {
            
            report.seol(0,"Test satisfiability in BCTL* of "+fs);
            Logic logic=new AUXLogic();
            FormulaTree f=logic.parse(fs);
            report.seol(1,"parsed to "+f.toString());
            
            f=logic.disabbreviate(f);
            report.seol(1,"Disabbreviates to = "+f);
            
            report.seol(1,"which has length = "+f.length());
            
            TemporalGraph t=new TemporalGraph(f,report);
            
            int maxEqClassSize=t.getMaxEquivClassSize();
            if (maxEqClassSize>=60) {
                report.seol(1,"Looks like there are too many colours so not continuing.");
                report.seol(2,"Max equiv class size is "+maxEqClassSize);
                return;
            }
            
            report.heading(2,"working through colours.");
            
            Colour c=new Colour(t);
            c.findFirstCandidate();
            int ctr=0;
            while (!c.allDone()){
            	Timeout.yield();
                if (c.isAColour()){
                    ctr++;
                    /*report.sol(3,"colour["+(ctr)+"]="+c.toString()+" is ");
                    report.eol(" a colour.");  HACK*/
                }
                c=c.nextCandidate();
            }
            report.seol(2,"Worked through all "+ctr+" colours. Now collect them.");
            
            
            Colour[] colour=new Colour[ctr];
            c=new Colour(t);
            c.findFirstCandidate();
            ctr=0;
            while (!c.allDone()){
            	Timeout.yield();
                if (c.isAColour()){
                    colour[ctr++]=c;
                }
                c=c.nextCandidate();
            }
            report.seol(3,"Got 'em.");
            
            report.seol(3,"The big precs reln is as follows:");
            int[][] csucc=new int[colour.length][];
            for(int i=0;i<colour.length; i++){
                report.sol(4,"colour["+(i+1)+"]="+colour[i].toString()+" precs colours ");
                int cp=0;
                for (int j=0;j<colour.length;j++)
                    if ((colour[i]).precs(colour[j])){
                        report.mol(" "+(j+1));
                        cp++;
                    }
                report.eol("");
                csucc[i]=new int[cp];
                cp=0;
                for (int j=0;j<colour.length;j++)
                    if ((colour[i]).precs(colour[j])){
                        csucc[i][cp]=j;
                        cp++;
                    }
            }
            report.seol(3,"end of precs");
            
          
            if (t.process(colour,csucc)) {
		status=1; //satisfiable
	    } else {
                status=0;
	    }
            
        } catch(ParseException e){
            System.out.println(e.getMessage());
            status=2; //exception
        }
    }
    
    
}
    
