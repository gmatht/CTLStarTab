package formulas;

public class RestrictedOrderedColourGenerator {

	int firstHue; //fixed as the first one
	
	TemporalGraph t;
	int[] allEquivHues;
	int[] edefects;
	boolean[] in; //which of the equivalent hues are currently in
	int[][] defwit;
	int wifh; //where is the first hue in the list of all equiv hues
	boolean start;
	int widthRestriction=-1;
	
	public RestrictedOrderedColourGenerator(int firstHue,
			TemporalGraph t, int widthRestriction) {
		this(firstHue,t);
	    this.widthRestriction=widthRestriction;
	    
	}
	
	public RestrictedOrderedColourGenerator(int firstHue,
			TemporalGraph t) {

		//t.tell("debug: into ROCG constructor");
		
		this.firstHue = firstHue;
		
		this.t = t;
		
		//get details of hue
		Subformulas sf=t.getSubformulas();
		Logic logic=t.getOwner().getLogic();
		
		//extract the +/- A and atomic fmlas from hue
		int count=0;
		for(int i=0;i<sf.count();i++){
			if (t.contains(firstHue,i)){
				if (sf.isZeroary(i)){
					if (((AUXLogic)logic).isAtom(sf.topChar(i))) count++;
				}
				if (sf.isUnary(i)){	
					if (sf.topChar(i)=='A') count++;
					if (sf.topChar(i)=='-') {
						int nf=sf.left(i);
						if (sf.isZeroary(nf)){
							if (((AUXLogic)logic).isAtom(sf.topChar(nf))) count++;
						}
						if (sf.topChar(nf)=='A') count++;
					}
				}
			}
		}
		int[] ff=new int[count];
		count=0;
		for(int i=0;i<sf.count();i++){
			if (t.contains(firstHue,i)){
				if (sf.isZeroary(i)){
					if (((AUXLogic)logic).isAtom(sf.topChar(i))) 
						ff[count++]=i;
				}
				if (sf.isUnary(i)){	
					if (sf.topChar(i)=='A') ff[count++]=i;
					if (sf.topChar(i)=='-') {
						int nf=sf.left(i);
						if (sf.isZeroary(nf)){
							if (((AUXLogic)logic).isAtom(sf.topChar(nf))) 
								ff[count++]=i;
						}
						if (sf.topChar(nf)=='A') ff[count++]=i;
					}
				}
			}
		}
		
		//get a hue generator from these fmlas
		RestrictedHueGenerator h=new RestrictedHueGenerator(t,ff);
		
		//collect all the equivalent hues
		int ceq=0;
		while (h.hasNext()) ceq++;
		allEquivHues=new int[ceq];
		h=new RestrictedHueGenerator(t,ff);
		ceq=0;
		while (h.hasNext()) {
			allEquivHues[ceq++]=h.hue();
			if (h.hue()==firstHue) wifh=ceq-1;
		}
		
		//collect all the insides of E fmlas in firstHue
		int cee=0;
		for(int i=0;i<sf.count();i++){
			if (t.contains(firstHue,i)){
				
				if (sf.isUnary(i)){	
					if (sf.topChar(i)=='-') {
						int nf=sf.left(i);
						
						if (sf.isUnary(nf)){
							if (sf.topChar(nf)=='A') cee++;
						}
					}
				}
			}
		}
		
		edefects =new int[cee];
		cee=0;
		for(int i=0;i<sf.count();i++){
			if (t.contains(firstHue,i)){
				if (sf.isUnary(i)){	
					if (sf.topChar(i)=='-') {
						int nf=sf.left(i);
						if (sf.isUnary(nf)){
							if (sf.topChar(nf)=='A') 
								edefects[cee++]=sf.negn(sf.left(nf));
						}
					}
				}
			}
		}

		
		in=new boolean[ceq];
		for(int i=0;i<allEquivHues.length;i++){
			in[i]=false;
		}
		start=true;
		
		//now each defect needs its own list of which hues witness it
		defwit=new int[edefects.length][];
		for (int i=0;i<edefects.length;i++){
			int ctr=0;
			for(int ehn=0;ehn<allEquivHues.length;ehn++){
				if (t.contains(allEquivHues[ehn],edefects[i])) ctr++;		
			}
			defwit[i]=new int[ctr];
			ctr=0;
			for(int ehn=0;ehn<allEquivHues.length;ehn++){
				if (t.contains(allEquivHues[ehn],edefects[i])) defwit[i][ctr++]=allEquivHues[ehn];		
			}
		}
		
		
		//t.tell("debug: out of ROCG constructor"+ceq);
		
	}//end constructor
	
	public RestrictedOrderedColourGenerator(int firstHue,
			TemporalGraph t, int[] prevHues) {

		//t.tell("debug: into ROCG constructor");
		
		this.firstHue = firstHue;
		
		this.t = t;
		
		//get details of hue
		Subformulas sf=t.getSubformulas();
		Logic logic=t.getOwner().getLogic();
		
		//extract the +/- A and atomic fmlas from hue
		int count=0;
		for(int i=0;i<sf.count();i++){
			if (t.contains(firstHue,i)){
				if (sf.isZeroary(i)){
					if (((AUXLogic)logic).isAtom(sf.topChar(i))) count++;
				}
				if (sf.isUnary(i)){	
					if (sf.topChar(i)=='A') count++;
					if (sf.topChar(i)=='-') {
						int nf=sf.left(i);
						if (sf.isZeroary(nf)){
							if (((AUXLogic)logic).isAtom(sf.topChar(nf))) count++;
						}
						if (sf.topChar(nf)=='A') count++;
					}
				}
			}
		}
		int[] ff=new int[count];
		count=0;
		for(int i=0;i<sf.count();i++){
			if (t.contains(firstHue,i)){
				if (sf.isZeroary(i)){
					if (((AUXLogic)logic).isAtom(sf.topChar(i))) 
						ff[count++]=i;
				}
				if (sf.isUnary(i)){	
					if (sf.topChar(i)=='A') ff[count++]=i;
					if (sf.topChar(i)=='-') {
						int nf=sf.left(i);
						if (sf.isZeroary(nf)){
							if (((AUXLogic)logic).isAtom(sf.topChar(nf))) 
								ff[count++]=i;
						}
						if (sf.topChar(nf)=='A') ff[count++]=i;
					}
				}
			}
		}
		
		//get a hue generator from these fmlas
		RestrictedHueGenerator h=new RestrictedHueGenerator(t,ff);
		
		//collect all the equivalent hues
		int ceq=0;
		while (h.hasNext()){
			if (t.traceBack(h.hue(),prevHues)!=-1){
				ceq++;
			}
		}
		allEquivHues=new int[ceq];
		h=new RestrictedHueGenerator(t,ff);
		ceq=0;
		while (h.hasNext()) {
			if (t.traceBack(h.hue(),prevHues)!=-1){
				allEquivHues[ceq++]=h.hue();
				if (h.hue()==firstHue) wifh=ceq-1;
			}
		}
		
		//collect all the insides of E fmlas in firstHue
		int cee=0;
		for(int i=0;i<sf.count();i++){
			if (t.contains(firstHue,i)){
				
				if (sf.isUnary(i)){	
					if (sf.topChar(i)=='-') {
						int nf=sf.left(i);
						
						if (sf.isUnary(nf)){
							if (sf.topChar(nf)=='A') cee++;
						}
					}
				}
			}
		}
		
		edefects =new int[cee];
		cee=0;
		for(int i=0;i<sf.count();i++){
			if (t.contains(firstHue,i)){
				if (sf.isUnary(i)){	
					if (sf.topChar(i)=='-') {
						int nf=sf.left(i);
						if (sf.isUnary(nf)){
							if (sf.topChar(nf)=='A') 
								edefects[cee++]=sf.negn(sf.left(nf));
						}
					}
				}
			}
		}

		
		in=new boolean[ceq];
		for(int i=0;i<allEquivHues.length;i++){
			in[i]=false;
		}
		start=true;
		
		//now each defect needs its own list of which hues witness it
		defwit=new int[edefects.length][];
		for (int i=0;i<edefects.length;i++){
			int ctr=0;
			for(int ehn=0;ehn<allEquivHues.length;ehn++){
				if (t.contains(allEquivHues[ehn],edefects[i])) ctr++;		
			}
			defwit[i]=new int[ctr];
			ctr=0;
			for(int ehn=0;ehn<allEquivHues.length;ehn++){
				if (t.contains(allEquivHues[ehn],edefects[i])) defwit[i][ctr++]=allEquivHues[ehn];		
			}
		}
		
		
		//t.tell("debug: out of ROCG constructor"+ceq);
		
	}//end constructor
	
	public RestrictedOrderedColourGenerator(int firstHue,
			TemporalGraph t, int[] prevHues, int widthRestriction){
		this(firstHue,t,prevHues);
		this.widthRestriction=widthRestriction;
	}
	
	
	public boolean hasNext(){
		
		if (start) {
			start=false;
		} else
			if (widthRestriction>-1) {
				if (!(incarr(widthRestriction))) return false;
			}
			else if (!incarr()) return false;
			
		if (!in[wifh]) in[wifh]=true;
		for(int i=0;i<edefects.length;i++){
			boolean found=false;
			//t.tell("i="+i+" d.l="+defwit[i].length);
			if (defwit[i].length==0) return false;
			int last=-1;
			for(int j=0;j<defwit[i].length;j++){
				
				for(int k=0;k<allEquivHues.length;k++){
					if ((allEquivHues[k]==defwit[i][j])){
						last=k;
						if ((in[k])) found=true;
					}
				}
			}
			if (!found) in[last]=true;
		}
		return true;

	}
	
	private boolean incarr(){
		int pl=in.length-1;

		while ((pl>=0) && (in[pl])) {
			in[pl]=false;
			pl--;
		}
		if (pl<0) return false;
		in[pl]=true;
		return true;
	}
	
	private boolean incarr(int widthRestriction){
		int pl=0;
		int ctr=0;
		while ((pl<in.length) && (ctr<widthRestriction)) {
			if (in[pl]) ctr++;
			pl++;
		}
		if (pl<in.length){
			for(int i=pl+1;i<in.length;i++) in[i]=false;
		} else {pl--;}

		while ((pl>=0) && (in[pl])) {
			in[pl]=false;
			pl--;
		}
		if (pl<0) return false;
		in[pl]=true;
		return true;
	}
	
	
	public OrderedColour oc(){
		
		int ct=0;
		for(int i=0;i<in.length;i++)
			if (in[i]) ct++;
		int[] contents=new int[ct];
		contents[0]=firstHue;
		ct=0;
		for(int i=0;i<in.length;i++){
			if ((in[i]) && (i != wifh))
				contents[++ct]=allEquivHues[i];
		}
		return new OrderedColour(t,contents);
		
	}
	
	private String listin(){
		String s="";
		for (int i=0;i<in.length;i++)
			if (in[i]) s=s+"1"; else s=s+"0";
		return s;
	}

	//let the user set oc to a colour which is a successor of oc
	public void userSetColour(OrderedColour poc) {
		// TODO Auto-generated method stub
		userSetColour();
	}
	
	public void userSetColour() {
		// TODO Auto-generated method stub
		System.out.println("You can choose to include each of the following hues:");
		

		for(int h=0;h<allEquivHues.length;h++){
			System.out.println("#"+allEquivHues[h]+": "+t.getHue(allEquivHues[h]));
			System.out.println("Enter y or n:");
			char a=Keyboard.getInput().charAt(0);
			if (a=='y') in[h]=true; else in[h]=false;
			
		}
		decarr();
		return;
		
	}
	
	private boolean decarr(){
		int pl=0;

		while ((pl<in.length) && (!in[pl])) {
			in[pl]=true;
			pl++;
		}
		if (pl>=in.length) return false;
		in[pl]=false;
		return true;
	}
}//end class

/*OLD VERSION: too slow
 * public boolean hasNext(){
		
		//t.tell("debug: into ROCG hasNext");
		boolean found=false;
		while ((!found) && incarr()){	
			//t.tell("debug:  hasNext loop"+listin());
			if (in[wifh]){
				boolean[] witnessed=new boolean[edefects.length];
				for(int i=0;i<edefects.length;i++){
					witnessed[i]=false;
				}
				for(int j=0;j<allEquivHues.length;j++){
						if (in[j]){
							for(int i=0;i<edefects.length;i++){
								if (t.contains(allEquivHues[j],edefects[i])){
									witnessed[i]=true;
								}
							}
						}
				}
				found=true;
				for(int i=0;i<edefects.length;i++){
					if (!witnessed[i]) found=false;
				}
			}
		}	
		return found;
	}
	*/
