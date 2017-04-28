public class CPU extends Thread {
	
	Algorithm algorithm;
	
	int quantum;
	
	/**
	 * 
	 * @param algorithm
	 */
	public CPU(Algorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	/**
	 * 
	 * @param algorithm
	 * @param quantum
	 */
	public CPU(Algorithm algorithm, int quantum) {
		this.algorithm = algorithm;
		this.quantum = quantum;
	}
	
	@Override
	public void run() {
	}
	
}
