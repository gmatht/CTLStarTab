package quicktab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import formulas.Examples;

public class Report {
	
	public static final long mm=40000; //max time allowed mS
	private static int meg=23; //max example number
	
	public static String latexOF="qt.tex";
	public static String csvOF="qt.csv";
	
	
	public static int version=2;
	
	public static void main(String[]args){
		
		int[][] a=new int[meg+1][];
		int[][] length=new int[meg+1][];
		int[][] steps=new int[meg+1][];
		long[][] timing=new long[meg+1][];
		String[][] outcome=new String[meg+1][];
		
		//initialize
		for(int i=1;i<meg+1;i++){
			a[i]=new int[2];
			length[i]=new int[2];
			steps[i]=new int[2];
			timing[i]=new long[2];
			outcome[i]=new String[2];
		}
		
		//get data
		for(int i=1;i<meg+1;i++) for (int q=-1;q<2;q=q+2){
			System.out.println("-------------EXAMPLE "+(q*i)+"-----------------");
			a[i][(q+1)/2]=QuickTab.decide(Examples.getExample(q*i), mm, version);
			length[i][(q+1)/2]=QuickTab.fmlaLength;
			steps[i][(q+1)/2]=QuickTab.numSteps;
			timing[i][(q+1)/2]=QuickTab.timeTaken;
			
			String r="";
			switch (a[i][(q+1)/2]){
			case 0: r="parse error";break;
			case 1: r="SAT"; break;
			case 2: r="UNSAT";break;
			case 3: r="probably UNSAT"; break;
			case 4: r="out of time";break;			
			}
			outcome[i][(q+1)/2]=r;
			
			System.out.println("             END of EXAMPLE                 ");
			System.out.println();
		}
		
		//output to screen
		for(int i=1;i<meg+1;i++) for (int q=-1;q<2;q=q+2){

			System.out.println(""+(q*i)+": "
					+Examples.getExample(q*i)+" "+Examples.unSat(q*i)+" "
					+length[i][(q+1)/2]+": "
					+outcome[i][(q+1)/2]+" "+steps[i][(q+1)/2]+" "+timing[i][(q+1)/2]);  
			
		}
		
		//output text file
		PrintWriter pw=null;
	        try {
	            pw=new PrintWriter(new FileOutputStream(csvOF));
	            System.out.println("Output file opened OK.");
	            pw.println("Results today");
	            pw.println("Version of decision procedure="+version);
	            
	            pw.println("egno, eg, sat?, length, outcome, steps, time");
	            
	    		for(int i=1;i<meg+1;i++) for (int q=-1;q<2;q=q+2){

	    			pw.println(""+(q*i)+", "
	    					+Examples.getExample(q*i)+", "+Examples.unSat(q*i)+", "
	    					+length[i][(q+1)/2]+", "
	    					+outcome[i][(q+1)/2]+", "+steps[i][(q+1)/2]+", "+timing[i][(q+1)/2]);  
	    			
	    		}
	            
	            
	            pw.close();
	            
	        } catch(IOException e){
	            System.exit(1);
	        }
		
		
	}

}
