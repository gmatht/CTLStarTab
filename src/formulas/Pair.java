package formulas;

public final class Pair {
	
	int x;
	int y;
	
	public Pair(int x, int y){
		this.x=x;
		this.y=y;
	}

	// Convert an integer into a pair
	public Pair(int max_x, int max_y, int i){
	    this.y=(i/max_x);
	    this.x=(i%max_x);
	    assert(x<max_x);
	    assert(y<max_y);
	    assert(toInt(max_x,max_y)==i);
	}

	public int toInt(int max_x, int max_y) {
	    assert(x<max_x);
	    assert(y<max_y);
	    return(x+(y*max_x));
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
