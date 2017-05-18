package quicktab;

public class Rule {

	private String name;
	private int fmla;

	public Rule(String string, int i) {
		// TODO Auto-generated constructor stub
		name=string.trim();
		fmla=i;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public int getSFN() {
		// TODO Auto-generated method stub
		return fmla;
	}

}
