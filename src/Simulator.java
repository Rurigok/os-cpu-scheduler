import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Simulator {
	
	private FileReader readerThread;
	private CPU cpuThread;
	private IO ioThread;
	
	/*
	 * Process queues and lists
	 */
	public LinkedList<Process> readyQueue = new LinkedList<>();
	public LinkedList<Process> ioQueue = new LinkedList<>();
	public LinkedList<Process> doneList = new LinkedList<>();
	
	/*
	 * Synchronization info
	 */
	public volatile int totalProcesses = 0;
	public volatile int finishedProcesses = 0;
	public final Lock statLock = new ReentrantLock();
	public final Semaphore doneFlag = new Semaphore(0);
	
	/*
	 * Process statistics
	 */
	
	public Simulator(String fileName, Algorithm algorithm, int quantum) {
		
		readerThread = new FileReader(this, fileName);
		cpuThread = new CPU(this, algorithm, quantum);
		ioThread = new IO(this);
		
	}
	
	public void startSimulation() {
		
		System.err.println("Beginning sim...");
		
		cpuThread.start();
		ioThread.start();
		readerThread.start();
		
		System.err.println("Threads started");
		
		try {
			doneFlag.acquire(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.err.println("All threads done");
		
		System.err.println("Simulation complete");
		
		// All threads have completed execution
		for (Process p : doneList) {
			System.out.println(p);
		}
		
		
		
		
	}
	
}
