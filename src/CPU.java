
public class CPU extends Thread {

	Simulator sim;
	Algorithm algorithm;
	int quantum;
	
	/**
	 * 
	 * @param algorithm
	 * @param quantum
	 */
	public CPU(Simulator sim, Algorithm algorithm, int quantum) {
		this.sim = sim;
		this.algorithm = algorithm;
		this.quantum = quantum;
	}
	
	@Override
	public void run() {
		
		Process process;
		
		while (sim.readyQueue.isEmpty()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (;;) {
			
			if (!sim.readyQueue.isEmpty()) {
				
				switch (algorithm) {
				case FIFO:
					/**
					 * Pick the first process
					 */
					synchronized (sim.readyQueue) {
						process = sim.readyQueue.removeFirst();
					}
					
					try {
						Thread.sleep(process.burst());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					process.turnaroundTime += process.burst();
					process.cpuTime += process.burst();
					
					// The time that this process used should be added to the
					// waiting time of any other processes in the readyQueue
					synchronized (sim.readyQueue) {
						
						for (Process p : sim.readyQueue) {
							p.waitingTime += process.burst();
							p.turnaroundTime += process.burst();
						}
						
					}
					
					// Prepare process for next io burst
					process.nextBurst();
					
					if (process.done) {
						//process.turnaroundTime = System.currentTimeMillis() - process.startTimestamp;
						sim.finishedProcesses++;
						synchronized (sim.doneList) {
							sim.doneList.add(process);
						}
					} else {
						synchronized (sim.ioQueue) {
							sim.ioQueue.add(process);
						}
					}
					
					
					break;
				case SJF:
					/**
					 * Pick the shortest burst time first
					 */
					
					synchronized (sim.readyQueue) {
					
						process = sim.readyQueue.peekFirst();
						
						for (Process p : sim.readyQueue) {
							if (p.burst() < process.burst()) {
								process = p;
							}
						}
						
						sim.readyQueue.remove(process);
					
					}
					
					try {
						Thread.sleep(process.burst());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					process.nextBurst();
					
					if (process.done) {
						//process.turnaroundTime = System.currentTimeMillis() - process.startTimestamp;
						sim.finishedProcesses++;
						synchronized (sim.doneList) {
							sim.doneList.add(process);
						}
					} else {
						synchronized (sim.ioQueue) {
							sim.ioQueue.add(process);
						}
					}
									
					break;
				case PR:
					/**
					 * 
					 */
					break;
				case RR:
					/**
					 * 
					 */
					break;
				}
				
			}
			
			if (sim.finishedProcesses == sim.totalProcesses) {
				break;
			}
			
		}
		
		sim.doneFlag.release();
		
	}
	
}
