package formulas;

public final class Pair {
	
	int x;
	int y;
	
	public Pair(int x, int y){
		this.x=x;
		this.y=y;
	}

	// Convert an integer into a pair
	static public Pair ofInt(int max_x, int i){
	    int xx,yy;
	    yy=(i/max_x);
	    xx=(i%max_x);
	    assert(xx<max_x);
	    Pair ret = new Pair(xx,yy);
	    assert(ret.toInt(max_x)==i);
	    return ret;
	}

	public int toInt(int max_x) {
	    assert(x<max_x);
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
