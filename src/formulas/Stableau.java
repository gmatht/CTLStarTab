package formulas;

public class Stableau {
	
	private Node[] node;
	private TemporalGraph owner;
	

	public Stableau(TemporalGraph t,OrderedColour oc){
		owner=t;
		node= new Node[1];
		//node[0]=new Node(oc);
	}
	
	
	public String toString(){
		return "This is a tableau with "+node.length+" nodes.";
	}
	

}
