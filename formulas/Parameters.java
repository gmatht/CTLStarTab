package formulas;



public class Parameters {

	

	

//restricting the search thru tablx

	//if there is a restriction then can't conclude fmla not sat

	//-1 means no restriction

  public static int depthRestriction=-1;

  public static int widthRestriction=-1;

  

  

  //Different types of Repetition Checks can be on or off

  public static boolean RepChkSHRon=true;

  public static boolean RepChkICRon=true;

  public static boolean RepChkSHIRon=true;

  public static boolean RepChkLFBIRon=true; //Leftmost Boring Interval

  public static boolean RepChkGBIRon=true;

  

  

  public static boolean BFSon=true;  //breadth first search (ow dfs)



}


