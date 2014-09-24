package formulas;


/**
 * @author mark
 * only the order of the first hue matters
 * rest can be numerical order
 */
public class OrderedColour {

	private static TemporalGraph t;
	int[] contents;
	
	public OrderedColour(TemporalGraph t, int[] contents){
		this.t=t;
		this.contents=contents;
	}
	
	public String toString(){
	String s="Following list of hues: ";
	for(int i=0;i<contents.length;i++)
		s=s+contents[i]+" ";
	return s;
	}

	public int size(){
	return contents.length;
	}

	public int getHue(int dirn){
	return contents[dirn];
	}

	public boolean match(OrderedColour other){
		if (contents[0] !=other.contents[0]) return false;
		boolean sofar=true;
		for(int i=0;i<contents.length;i++){
			boolean in=false;
			for (int j=0;j<other.contents.length;j++)
				if (other.contents[j]==contents[i]) in=true;
			if (!in) sofar=false;
		}
		for(int i=0;i<other.contents.length;i++){
			boolean in=false;
			for (int j=0;j<contents.length;j++)
				if (contents[j]==other.contents[i]) in=true;
			if (!in) sofar=false;
		}
		return sofar;
	
	}

	public int getFirstHue() {
		
		return contents[0];
	}
	
	public int[] getAllHueNumbers(){
		return contents;
	}

	public boolean rx(OrderedColour nc) {
		for (int i=0;i<nc.contents.length;i++){
			boolean covered=false;
			for(int j=0;j<contents.length;j++){
				if (t.succ(contents[j], nc.contents[i]))
					covered=true;
			}
			if (!covered) return false;
		}
		
		return true;
	}
}
