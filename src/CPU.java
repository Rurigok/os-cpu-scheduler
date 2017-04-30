
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
					//System.out.println("choose process " + process.processID + " to run");

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

						//System.out.println("process " + process.processID + " cpu bursted for " + process.burst());

					}

					// Prepare process for next io burst
					process.nextBurst();

					if (process.done) {
						sim.finishedProcesses++;
						synchronized (sim.doneList) {
							//System.out.println("process " + process.processID + " done!");
							sim.doneList.add(process);
						}
					} else {
						synchronized (sim.ioQueue) {
							sim.ioQueue.add(process);
							//System.out.println("sent process " + process.processID + " to ioqueue");
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
					
					process.turnaroundTime += process.burst();
					process.cpuTime += process.burst();
					
					// The time that this process used should be added to the
					// waiting time of any other processes in the readyQueue
					synchronized (sim.readyQueue) {

						for (Process p : sim.readyQueue) {
							p.waitingTime += process.burst();
							p.turnaroundTime += process.burst();
						}

						//System.out.println("process " + process.processID + " cpu bursted for " + process.burst());

					}

					process.nextBurst();

					if (process.done) {
						// process.turnaroundTime = System.currentTimeMillis() -
						// process.startTimestamp;
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
					 * Choose highest priority process
					 */
					synchronized (sim.readyQueue) {

						process = sim.readyQueue.peekFirst();

						for (Process p : sim.readyQueue) {
							if (p.priority < process.priority) {
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
					
					process.turnaroundTime += process.burst();
					process.cpuTime += process.burst();
					
					// The time that this process used should be added to the
					// waiting time of any other processes in the readyQueue
					synchronized (sim.readyQueue) {

						for (Process p : sim.readyQueue) {
							p.waitingTime += process.burst();
							p.turnaroundTime += process.burst();
						}

						//System.out.println("process " + process.processID + " cpu bursted for " + process.burst());

					}

					process.nextBurst();

					if (process.done) {
						// process.turnaroundTime = System.currentTimeMillis() -
						// process.startTimestamp;
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
				case RR:
					/**
					 * First-come-first-serve, however each burst is limited to
					 * min(quantum, p.burst()). If there is still burst left, we
					 * keep the process here and add it back to the end of the
					 * ready queue
					 */
					
					synchronized (sim.readyQueue) {
						process = sim.readyQueue.removeFirst();
					}
					
					int burst = Math.min(quantum, process.burst());

					try {
						Thread.sleep(burst);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					process.turnaroundTime += burst;
					process.cpuTime += burst;
					
					// The time that this process used should be added to the
					// waiting time of any other processes in the readyQueue
					synchronized (sim.readyQueue) {

						for (Process p : sim.readyQueue) {
							p.waitingTime += burst;
							p.turnaroundTime += burst;
						}

						//System.out.println("process " + process.processID + " cpu bursted for " + process.burst());

					}

					process.bursts[process.burstIndex] -= burst;
					
					if (process.burst() < 0) {
						System.err.println("Internal error: below 0 burst time");
						System.exit(-1);
					} else if (process.burst() == 0) {			
						// CPU burst is done, send process to ioQueue
						
						process.nextBurst();
						
						if (process.done) {
							// process.turnaroundTime = System.currentTimeMillis() -
							// process.startTimestamp;
							sim.finishedProcesses++;
							synchronized (sim.doneList) {
								sim.doneList.add(process);
							}
						} else {
							synchronized (sim.ioQueue) {
								sim.ioQueue.add(process);
							}
						}
						
					} else {
						// CPU burst isn't done, put this process at the end of the queue
						
						synchronized (sim.readyQueue) {
							sim.readyQueue.add(process);
						}
						
					}		
					
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
