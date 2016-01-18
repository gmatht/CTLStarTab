package formulas;

//these are hues which have successors which are also perfect hues
//and have rx paths of successors which eventually witness all eventualities

public class PerfectHues {
	
	TemporalGraph t;
	boolean[] isPerfect;
	
	public PerfectHues(TemporalGraph t){
		this.t=t;
		Subformulas sf=t.getSubformulas();
		
		int nh=t.numHues();
		isPerfect=new boolean[nh];
		for(int i=0;i<nh;i++) isPerfect[i]=true;
		
		boolean change=true;
		while (change){
			
			change=false;
			
			//check have perfect successor
			for(int i=0;i<nh;i++){
				if (isPerfect[i]){
					boolean foundps=false;
					for(int j=0;j<nh;j++){
						if ((t.rx(i,j)) && (isPerfect[j])) foundps=true;
					}
					if (!foundps){
						isPerfect[i]=false;
						change=true;
						//System.out.println("DEBUG PerfectHues (1) losing "+i);
					}
				}
				
			}//end for i
			
			int[] events=t.getAllEvents();
			boolean[][] sees=new boolean[nh][events.length]; //sees[hn][en] means hue hn has a path thru perfects to event en
			for(int i=0; i<nh;i++) for(int k=0;k<events.length;k++) sees[i][k]=false;
			
			//check eventualities fulfilled in perfect hues
			boolean evchange=true;
			while (evchange){
				evchange=false;
				for(int i=0; i<nh;i++){
					if (isPerfect[i]){
						for(int k=0; k<events.length;k++){
							if (!sees[i][k]){
								if (t.contains(i,events[k])){
									sees[i][k]=true;
									evchange=true;
								}
							}
						}
						for(int j=0; j<nh;j++){
							if (isPerfect[j]) {
								if (t.rx(i,j)) {
									for(int k=0; k<events.length;k++){
										if (sees[j][k]){
											if (!sees[i][k]){
												sees[i][k]=true;
												evchange=true;
											}
										}
									}
								}
							}
						}
					}
				}
				
			}//end while evchange
			
			
			int[] untils=t.getAllUntils();
			//get rid of perfects who can't see own events
			for(int i=0;i<nh;i++){
				if (isPerfect[i])
				for(int k=0;k<events.length;k++){
					if (t.contains(i,untils[k])){
						if (!sees[i][k]){
							isPerfect[i]=false;
							change=true;
							//t.tell("Losing hue "+i);
						}
					}
				}
			}
			
		}//end while change
		
	}//end constructor
	
	public boolean isPerfect(int n){
		return isPerfect[n];
	}
	


}
