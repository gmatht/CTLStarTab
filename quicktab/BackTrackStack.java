package quicktab;

import java.util.ArrayList;

import formulas.Pair;

public class BackTrackStack {
	
	private ArrayList qtar=new ArrayList();
	private ArrayList rar=new ArrayList();
	private ArrayList baar=new ArrayList();

	public void add(TransitionStructure cqt, Rule rule,boolean[] ba) {
		//System.out.println("Before adding size is "+qtar.size());
		qtar.add(cqt);
		rar.add(rule);
		baar.add(ba);
		//System.out.println("After adding size is "+qtar.size());
		
	}

	public boolean[] peek3() {
		
		return (boolean[]) baar.get(baar.size()-1);
	}

	public void modify3(boolean[] uc) {
	  baar.set(baar.size()-1,uc);
		
	}

	public TransitionStructure peek1() {
		
		return (TransitionStructure) qtar.get(qtar.size()-1);
	}
	
	public Rule peek2(){
		return (Rule) rar.get(rar.size()-1);
	}
	
	public void pop(){
		//System.out.println("DEBUG popping now");
		qtar.remove(qtar.size()-1);
		rar.remove(rar.size()-1);
		baar.remove(baar.size()-1);
	}

	public boolean isEmpty() {
		
		return qtar.isEmpty();
	}

	public int size() {
		
		return rar.size();
	}
	
	public String toString(){
		String s="";
		for (int i=0;i<rar.size();i++)
			s=s+" "+((Rule)rar.get(i)).getName();
		return s;
	}

}
