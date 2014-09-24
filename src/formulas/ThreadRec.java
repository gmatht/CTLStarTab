package formulas;

/*records which partial hues can be reached from somewhere
 * and what eventualities are witnessed along the way
 * 
 */
public class ThreadRec {
	
	PartialHue start=null;
	PartialHue[] destination= new PartialHue[0];
	boolean[][] sees=new boolean[0][];
	int[] allEvents;
	int[] allUntils;
	
	public ThreadRec(int[] allEvents,int[] allUntils){
		this.allEvents=allEvents;
		this.allUntils=allUntils;
	}

	//add in a one step thread
	//return true only if something was changed
	public boolean addinOneStep(PartialHue partialHue, PartialHue partialHue2) {

		if (start==null) start=partialHue;
		else if (start != partialHue){
			System.out.println("Crash: runtime error in class ThreadRec");
			System.exit(0);
		}
		int aen=allEvents.length;
		boolean[] seen=new boolean[aen];
		for(int i=0;i<aen;i++){
			if ((partialHue.has(allEvents[i])) || (partialHue2.has(allEvents[i]))){
				seen[i]=true;		
			}
		}
		return addInOneThread(partialHue2,seen);
	}
		
	//add in a one new thread
	//return true only if something was changed
		public boolean addInOneThread(PartialHue dest, boolean[] eventsWitnessed){
			
	
			//System.out.print("debug aiot1: dest="+dest+" ew=");
			//for(int k=0;k<eventsWitnessed.length;k++) System.out.print(eventsWitnessed[k]);
			//System.out.println();
			
		PartialHue partialHue2=dest;
		boolean[] seen=eventsWitnessed;
		int aen=allEvents.length;
		
		//is this thread included in one already recorded?
		//if yes then no need to include this thread: no change, return
		//if no: then add this thread and report a change
		
		boolean thisThreadIncluded=false;
		for(int j=0; j<destination.length;j++){
			if (destination[j]==partialHue2){
				boolean newThreadSeesSomethingNew=false;
				for(int i=0; i<aen;i++){
					if (( seen[i]) && !(sees[j][i])) newThreadSeesSomethingNew=true;
				}
				if (!(newThreadSeesSomethingNew)) thisThreadIncluded=true;
			}
		}
		if (thisThreadIncluded) return false;
		
		int oldCount=destination.length;
		PartialHue[] dnew=new PartialHue[oldCount+1];
		boolean[][] snew=new boolean[oldCount+1][];
		for(int g=0;g<oldCount;g++){
			dnew[g]=destination[g];
			snew[g]=new boolean[sees[g].length];
			for(int h=0; h<sees[g].length;h++){
				snew[g][h]=sees[g][h];
			}
		}
		dnew[oldCount]=partialHue2;
		snew[oldCount]=seen;
		destination=dnew;
		sees=snew;
		
		
		//does this thread include any that is already recorded?
		//if yes then remove those threads
		
		int j=0;
		while (j<destination.length-1){
			if (destination[j]==partialHue2){
				boolean oldThreadSeesSomethingElse=false;
				for(int i=0; i<aen;i++){
					if ((!seen[i]) && (sees[j][i])) oldThreadSeesSomethingElse=true;
				}
				if (!(oldThreadSeesSomethingElse)) {//we should remove the old thread
					oldCount=destination.length;
					dnew=new PartialHue[oldCount-1];
					snew=new boolean[oldCount-1][];
					for(int g=0;g<oldCount-1;g++){
						int gp=g;
						if (g>=j) gp=g+1;
						dnew[g]=destination[gp];
						snew[g]=new boolean[sees[gp].length];
						for(int h=0; h<sees[gp].length;h++){
							snew[g][h]=sees[gp][h];
						}
					}
					destination=dnew;
					sees=snew;
					j=j-1;
				}
			}
			j=j+1;
		}
		
		
		return true;
	}

	//add in a all the thread details from a successor node
	public boolean mergeIn(ThreadRec threadRec) {
		
		
		int aen=allEvents.length;
		boolean[] seen=new boolean[aen];
		for(int i=0;i<aen;i++){
			if ((start.has(allEvents[i]))){
				seen[i]=true;		
			}
		}
		
		//for each thread from threadRec
		//add on witnessed events at the start here
		//then add in that new thread to this
		boolean somethingChanged=false;
		for(int i=0; i<threadRec.destination.length; i++){
			PartialHue d=threadRec.destination[i];
			boolean[] e=threadRec.sees[i];
			for(int j=0; j<aen;j++)
				e[j]=e[j] || seen[j];
			somethingChanged=somethingChanged || addInOneThread(d,e);
		}
		
		return somethingChanged;
	}

	public boolean fulfilledLoop(PartialHue partialHue, PartialHue partialHue2) {
		
		if (start==null) start=partialHue;
		
		//collect all the events needing witnessing
		int aen=allUntils.length;
		boolean[] needed=new boolean[aen];
		for(int i=0;i<aen;i++){
			if ((start.has(allUntils[i]))){
				needed[i]=true;		
			}
		}
		
		//loop thru all threads recorded
		//if they end at ph2 then see if they see all the required events
		for(int j=0;j<destination.length;j++){
			if (destination[j]==partialHue2){
				boolean seesAllReqd=true;
				for(int i=0;i<aen;i++){
					if ((needed[i]) && !(sees[j][i])) seesAllReqd=false;
				}
				if (seesAllReqd) return true;
			}
		}
		
		return false;
	}

}
