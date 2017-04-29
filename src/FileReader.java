import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

public class FileReader extends Thread {
	
	File inputFile;
	Simulator sim;

	/**
	 * Constructs a new FileReader to read input from the given file
	 */
	public FileReader(Simulator sim, String fileName) {
		this.sim = sim;
		this.inputFile = new File(fileName);
	}
	
	@Override
	public void run() {
		
		/*
		 * Input file format:
		 * 
		 * proc <priority> <<cpu burst> <io burst>> ... (repeated) <cpu burst>
		 * sleep <milliseconds> 
		 * stop
		 * 
		 */
		try {
			Files.lines(inputFile.toPath()).forEachOrdered(line -> {
				
				line = line.trim();
				
				if (!line.isEmpty()) {
				
					//System.out.println(line);
					
					String[] parse = line.split(" ");
					
					switch (parse[0]) {
					case "proc":
						sim.readyQueue.add(new Process(sim.totalProcesses, line));
						sim.totalProcesses++;
						break;
					case "sleep":
						int time = Integer.parseInt(parse[1]);
						try {
							Thread.sleep(time);
						} catch (InterruptedException e) {
							System.err.println("Interrupted file reader thread while sleeping!");
							e.printStackTrace();
						}
						break;
					case "stop":
						sim.doneFlag.release();
						return; // stop thread by returning from run
					default:
						System.err.println("Unknown command in input file");
						System.exit(-1);
					}
				
				}
				
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sim.doneFlag.release();
		
	}

}
