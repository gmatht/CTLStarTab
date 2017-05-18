package quicktab;

import formulas.FormulaTree;

public interface TransitionStructure {

	public FormulaTree getOwner();

	public int numNodes();

    public Node getNode(int i);

}
