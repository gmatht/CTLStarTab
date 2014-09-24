import java.lang.*;
public class Test {
	volatile static boolean interrupted=false;

	public static boolean i() {
		return (interrupted);
	}
		

	public static void main(String[] args) {
                Thread t=Thread.currentThread();
		for (int i=0;i<100000000;i++) {
			//java.lang.Thread.interrupted();
			//t.isInterrupted();
			if (i()) return;
		}
	}
}
