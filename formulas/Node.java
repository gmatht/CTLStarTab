package formulas;

public class Node {

	private RestrictedHueGenerator hg;
	private RestrictedOrderedColourGenerator cg;
	private int[] succ=new int[0]; //can be negated if up a loop
	private int parent; //strict parent ie not via loops, -1 for root, -2 if node abandoned
	private int dirn; //direction from parent to here, -1 if no parent
	private int[] wlt;
	
	public Node(int p,int d){
		parent=p;
		dirn=d;
	}
	
	public void setParent(int i,int d){
		parent=i;
		dirn=d;
	}
	
	public int numsucc(){
		return succ.length;
	}
	
	public int succ(int i){
		return succ[i];
	}

	public void paint(RestrictedHueGenerator hg, RestrictedOrderedColourGenerator cg){
		this.hg = hg;
		this.cg = cg;

	}
	
	public void setRHG(RestrictedHueGenerator hg){
		this.hg = hg;
		this.cg = null;

	}
	
	public void branch(int n){
		succ=new int[n];
	}
	
	public void givesucc(int d,int n){
		succ[d]=n;
	}
	
	public OrderedColour oc(){
		if (cg==null) return null;
		return cg.oc();
	}
	
	public String toString(){
		String s="";
		if (cg==null){
			s="LEAF";
		} else {
	    s="Colour: "+cg.oc();
		s=s+"\n There are "+succ.length+" successors: ";
		for(int i=0;i<succ.length;i++) s=s+" "+succ[i];
		}
		return s;
	}
	
	public int getParent(){
		return parent;
	
	}

	public boolean isLeaf() {
		
		return (succ.length==0);
	}
	
	public int getDirn(){
		return dirn;
	}
	
	public RestrictedHueGenerator getRHG(){
		return hg;
	}
	
	public RestrictedOrderedColourGenerator getROCG(){
		return cg;
	}



	public void setROCG(RestrictedOrderedColourGenerator cg2) {
		// TODO Auto-generated method stub
		cg=cg2;
	}
	
	public int[] whatLoopsTo(){
		return wlt;
	}
	
	public void resetWLT(){
		wlt=new int[0];
	}
	
	public void addWLT(int n){
		int[] tmp=new int[wlt.length+1];
		for(int i=0; i< wlt.length; i++) tmp[i]=wlt[i];
		tmp[wlt.length]=n;
		wlt=tmp;
	}
}
