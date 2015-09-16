package formulas;

public class Pair {
	
	int x;
	int y;
	
	public Pair(int x, int y){
		this.x=x;
		this.y=y;
	}
	
	public int x(){
		return x;
	}
	
	public int y(){
		return y;
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof Pair) {
			Pair p = (Pair)o;
			return ( (x==p.x)&& (y==p.y) );
		}
		return false;
	}

	@Override
	public int hashCode() {
	    return (x + (y<<16));
	}
}
