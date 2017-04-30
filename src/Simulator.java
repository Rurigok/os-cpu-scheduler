import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Simulator {

	private FileReader readerThread;
	private CPU cpuThread;
	private IO ioThread;
	private String fileName;
	private Algorithm algorithm;

	/*
	 * Process queues and lists
	 */
	public volatile LinkedList<Process> readyQueue = new LinkedList<>();
	public volatile LinkedList<Process> ioQueue = new LinkedList<>();
	public volatile LinkedList<Process> doneList = new LinkedList<>();

	/*
	 * Synchronization info
	 */
	public volatile int totalProcesses = 0;
	public volatile int finishedProcesses = 0;
	// public final Lock statLock = new ReentrantLock();
	// public final Lock readyLock = new ReentrantLock();
	// public final Lock ioLock = new ReentrantLock();
	public final Lock lock = new ReentrantLock(true);
	public final Semaphore doneFlag = new Semaphore(0);

	/*
	 * Process statistics
	 */

	public Simulator(String fileName, Algorithm algorithm, int quantum) {
		this.fileName = fileName;
		this.algorithm = algorithm;
		readerThread = new FileReader(this, fileName);
		cpuThread = new CPU(this, algorithm, quantum);
		ioThread = new IO(this);

	}

	public void startSimulation() {

		//System.err.println("Beginning sim...");

		cpuThread.start();
		ioThread.start();
		readerThread.start();

		//System.err.println("Threads started");

		try {
			doneFlag.acquire(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//System.err.println("All threads done");

		//System.err.println("Simulation complete");

		long totalCPUTime = 0;
		long totalWaitingTime = 0;
		long totalIOTime = 0;
		long totalTime = 0;

		// All threads have completed execution
		for (Process p : doneList) {
			totalCPUTime += p.cpuTime;
			totalWaitingTime += p.waitingTime;
			totalIOTime += p.ioTime;
			totalTime += p.turnaroundTime;
			//System.out.println(p);
		}

		double averageTurnaround = (double) totalTime / doneList.size();
		double throughputPerSecond = 1000 / averageTurnaround;
		double avgWait = (double) totalWaitingTime / doneList.size();

		System.out.printf("Input File Name     : %s\n", fileName);
		System.out.printf("CPU Scheduling Alg  : %s\n", algorithm);
		System.out.printf("CPU utilization     : %.2f%%\n", ((double) totalCPUTime / totalTime) * 100);
		System.out.printf("Throughput          : %.2f processes per second\n", throughputPerSecond);
		System.out.printf("Turnaround time     : %.2f ms (average) %d ms (total)\n", averageTurnaround, totalTime);
		System.out.printf("Waiting time        : %.2f ms (average) %d ms (total)\n", avgWait, totalWaitingTime);

	}

}
