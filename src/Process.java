
public class Process {
	
	public int processID;
	
	/**
	 * Priority of this process, where 1 is the lowest priority and 10 is the
	 * highest priority.
	 */
	public volatile int priority;
	
	/**
	 * Alternating CPU and IO burst numbers, always with an odd length and
	 * ending with a final CPU burst.
	 */
	private volatile int[] bursts;
	
	/**
	 * Internal index representing which burst should be processed next.
	 */
	private volatile int burstIndex = 0;
	
	/**
	 * True if this process has completed all bursts.
	 */
	public volatile boolean done = false;
	
	public final long startTimestamp = System.currentTimeMillis();
	
	public volatile long turnaroundTime = 0;
	public volatile long cpuTime = 0;
	public volatile long ioTime = 0;
	public volatile long waitingTime = 0;
	
	/**
	 * Constructs a new Process from a "proc ..." command representing a
	 * virtual process for simulation by the CPU and IO threads.<br><br>
	 * 
	 * A proc command has the following format:<br>
	 * 
	 * proc [priority] [[cpu burst] [io burst]] ... (repeated) [cpu burst]
	 * 
	 * @param creationString A "proc ..." command to build this process
	 */
	public Process(int processID, String creationString) {
		
		this.processID = processID;
		
		String[] split = creationString.split("\\s+");
		
		int finalBurst = Integer.parseInt(split[split.length - 1]);
		priority = Integer.parseInt(split[1]);
		
		bursts = new int[split.length - 2];
		
		int i;
		for (i = 2; i < split.length - 1; i++) {
			bursts[i - 2] = Integer.parseInt(split[i]);
		}
		
		bursts[i - 2] = finalBurst;
		
	}
	
	public int burst() {
		return this.bursts[burstIndex];
	}
	
	public void nextBurst() {
		this.burstIndex++;
		if (burstIndex >= bursts.length) {
			this.done = true;
		}
	}
	
	@Override
	public String toString() {
		
		return String.format("[id: %d, cpu: %d, io: %d, waiting: %d, turnaround: %d]", 
				this.processID,
				this.cpuTime,
				this.ioTime,
				this.waitingTime,
				this.turnaroundTime);
		
	}

}
