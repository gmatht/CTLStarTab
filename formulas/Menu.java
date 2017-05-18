package formulas;

public class Menu {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  menu();

	}
	
	   
    public static void menu(){
    
    while (true){
        displayMenu();
    
        char choice=Keyboard.getInput().charAt(0);
        switch (choice) {
            case 'e': one(); break;
            case 'f': inpf(); break;
            case 'q': System.exit(0);
            default:
                System.out.println("choice not recognized");
        }
    }
}

public static void displayMenu(){
    System.out.println("USER MENU: choose a character" );
    System.out.println("e: do one of the built in examples");
    System.out.println("f: enter own formula");
    System.out.println("q: quit");
    
}

public static void one(){
    System.out.println("which built in example 1-14?");
    String s=Keyboard.getInput();
    int num=Integer.parseInt(s);
    
    if ((num<1) || (28<num)){
        System.out.println("Choice not appropriate.");
        return;
    }
    String egf;
    if (num>14)
        egf=Examples.getExample(num-14);
    else
        egf="-("+Examples.getExample(num)+")";
        
    
    new BctlTab(egf,new ReportToFile("C:\\Documents and Settings\\mark\\My Documents\\Work\\Research\\Programs\\Logic\\fout.txt"));
    
}
    public static void inpf(){
    System.out.println("Enter formula");
    String s=Keyboard.getInput();
 
    new BctlTab(s,new ReportToFile("C:\\Documents and Settings\\mark\\My Documents\\Work\\Research\\Programs\\Logic\\fout.txt"));
    
}

}
