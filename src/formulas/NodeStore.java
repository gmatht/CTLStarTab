package formulas;

public class NodeStore {
	
	private int number;
	//private boolean[] label;
	OrderedColour oc;
	private int[] succ=new int[0]; 
	private int parent; //strict parent ie not via loops, -1 for root, -2 if node abandoned
	private int dirn; //direction from parent to here, -1 if no parent
	
	public NodeStore(int number, OrderedColour oc, int[] succ, int parent, int dirn) {

		this.number=number;
		//this.label = label;
		this.succ = succ;
		this.parent = parent;
		this.dirn = dirn;
		this.oc=oc;
	}
	
	
	

}
