
public class IO extends Thread {

	Simulator sim;
	
	/**
	 */
	public IO(Simulator sim) {
		this.sim = sim;
	}
	
	@Override
	public void run() {
		
		Process process;
		
		while (sim.ioQueue.isEmpty()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (;;) {
			
			if (!sim.ioQueue.isEmpty()) {
				
				synchronized (sim.ioQueue) {
				
					process = sim.ioQueue.removeFirst();
				
				}
					
				try {
					Thread.sleep(process.burst());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				process.turnaroundTime += process.burst();
				process.ioTime += process.burst();
				
				// The time that this process used should be added to the
				// waiting time of any other processes in the ioQueue
				synchronized (sim.ioQueue) {
					
					for (Process p : sim.ioQueue) {
						p.waitingTime += process.burst();
						p.turnaroundTime += process.burst();
					}
					
				}
				
				process.nextBurst();
				
				synchronized (sim.readyQueue) {
					sim.readyQueue.add(process);
				}
								
			}
			
			if (sim.finishedProcesses == sim.totalProcesses) {
				break;
			}
			
		}
		
		sim.doneFlag.release();
		
	}
	
}
