// TODO: Force Colours to be final before 

/**
 * 
 */
package formulas;

import java.util.*;
import java.lang.*;

/**
 * @author John
 *
 */
public class Timeout {
	
	volatile static boolean interrupted = false;
	
	/**
	 * A class that quickly checks if interrupted
	 * has been set to true. Not thread-safe.
	 * 
	 * @throws InterruptedException
	 */
	

	static boolean wasInterrupted(Exception e) {
		return (interrupted || e.getMessage() == "InterruptedException"); //TODO: this is a bug workaround.
	}
	
	static boolean wasInterrupted() {
		return interrupted;
	}
	
	static void delayedAbort(long milliseconds) {
		Timer t=new Timer(true);
		t.schedule(new TimeoutAbortTask(), milliseconds);	
	}
	
	static void yield() {
//		static void yield() throws InterruptedException {
		//if (Thread.interrupted()) throw new InterruptedException();

        if (Thread.interrupted()) {
            System.out.println("INTERRUPTED!");
            throw new RuntimeException("InterruptedException");
        }
		if (interrupted) {

            System.out.println("INTERRUPTED!!");
            //if (Thread.interrupted()) {
            Thread.interrupted(); 
            //{
            	//interrupted=false;
            	//throw new InterruptedException();
                throw new RuntimeException("InterruptedException");
            //}
		}
	}
	
	static void timeout_run(Runnable r) {
		interrupted = false;
		Timer t=new Timer();
		int milliseconds=5000;
		t.schedule(new TimeoutTask(), milliseconds);
		r.run();
		t.cancel();
		Thread.interrupted();
		//System.out.println("FINISHED.");
	}
	
	public static void main(String[] argv) {
		System.out.println("START");
		timeout_run(new TestRunnable());

		System.out.println("END");
	}
}

class TestRunnable implements Runnable {

	@Override
	public void run() {
		try { 
		   while(true) Timeout.yield();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//System.out.println("FINISHED. TR	");
	}
	
	
}

class TimeoutTask extends TimerTask {

	volatile Thread thread;
	
	TimeoutTask() {
		thread=Thread.currentThread();
	}
	
	@Override
	public void run() {
		System.out.println("Attempting to interrupt!");
		Timeout.interrupted=true;
		thread.interrupt();
	}
}

class TimeoutAbortTask extends TimerTask {
	
	@Override
	public void run() {
		System.out.println("Terminated! (Abort Timeout)");
		System.out.flush();
		System.exit(1);
	}
}