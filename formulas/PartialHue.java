package formulas;

public class PartialHue {
	
	boolean[] in;
	PartialHue[] succ;
	int ownerNodeNumber;
	
	

	public PartialHue(int numsf, int onn) {
		in=new boolean[numsf];
		for(int i=0; i<numsf; i++) in[i]=false;
		succ=new PartialHue[0];
		ownerNodeNumber=onn;
	}



	public void makeSucc(PartialHue partialHue) {
		//check not there already
		boolean there=false;
		for(int i=0;i<succ.length;i++) if (succ[i]==partialHue) there=true;
		if (!there){
		
			PartialHue[] temp=new PartialHue[succ.length+1];
			for(int i=0;i<succ.length;i++) temp[i]=succ[i];
			temp[succ.length]=partialHue;
			succ=temp;
		}
		
	}



	public void add(int i) {
		in[i]=true;
		
	}



	public boolean has(int sf) {
		
		return in[sf];
	}
	
	public int numsucc(){
		return succ.length;
		
	}



	public PartialHue getSucc(int l) {
        return succ[l];
	}



	public int getOwnNum() {
		
		return ownerNodeNumber;
	}

	public int getIndex(PartialHue[] g){
		for(int j=0;j<g.length;j++)
			if (g[j]==this) return j;
		return -1;
	
	}


	public void scrapSucc(PartialHue xph) {
		PartialHue[] temp=new PartialHue[succ.length-1];
		int nctr=0;
		for(int i=0;i<succ.length;i++){
			if (succ[i] != xph)
				temp[nctr++]=succ[i];
		}
		succ=temp;
	}



	public PartialHue split() {
       PartialHue answ=new PartialHue(in.length,ownerNodeNumber);
       for(int i=0;i<in.length;i++) answ.in[i]=in[i];
       
       return answ;
       
	}

    public String toString(){
        String s="{";
        boolean start=true;
        for(int i=0; i<in.length; i++){
            if (in[i]) {
                if (!start) s=s+",  ";
                start=false;
                s=s+i;
            }
        }
        s=s+"} of node #"+ownerNodeNumber;
        return s;
    }
    
    public String toString(Subformulas sf){
        String s="{";
        boolean start=true;
        for(int i=0; i<in.length; i++){
            if (in[i]) {
                if (!start) s=s+",  ";
                start=false;
                s=s+sf.getFormula(i);
            }
        }
        s=s+"} of node #"+ownerNodeNumber;
        return s;
    }

}
