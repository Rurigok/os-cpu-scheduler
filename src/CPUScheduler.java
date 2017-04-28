import java.io.File;

public class CPUScheduler {
	
	public static void main(String[] args) {
		
		/*
		 * Possible command line flags:
		 *
		 * required: -alg [FIFO|SJF|PR|RR] (2)
		 * required: -input [file name] (2)
		 * optional: -quantum [int milliseconds] (2)
		 * 
		 * We could do some more robust command line argument parsing here
		 * but that's not the point of this assignment...
		 */
		
		if (args.length < 4 || args.length > 6) {
			usageExit("Invalid arguments");
		}
		
		String algorithmStr = null;
		Algorithm algorithm;
		String fileName = null;
		int quantum = -1;
		
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-alg":
				if (i + 1 == args.length) {
					usageExit("No algorithm specified. Options: [FIFO|SJF|PR|RR]");
				}
				algorithmStr = args[i + 1];
				break;
			case "-input":
				if (i + 1 == args.length) {
					usageExit("No input file name specified");
				}
				fileName = args[i + 1];
				break;
			case "-quantum":
				if (i + 1 == args.length) {
					usageExit("No quantum time specified");
				}
				try {
					quantum = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {
					usageExit("Quantum must be an integer in milliseconds");
				}
				break;
			default:
				usageExit("Invalid arguments");
			}
		}
		
		if (algorithmStr == null) {
			usageExit("Algorithm flag is required");
		}
		
		algorithm = Algorithm.valueOf(algorithmStr);
		
		if (fileName == null) {
			usageExit("Input flag is required");
		}
		
		/*
		 * Input file format:
		 * 
		 * proc <priority> <<cpu burst> <io burst>> ... (repeated) <cpu burst>
		 * sleep <milliseconds>
		 * stop
		 * 
		 */
		File inputFile = new File(fileName);
		
		/*
		 * Thread setup
		 */
		
	}
	
	public static void usageExit(String message) {
		System.err.println(message);
		System.err.println("Usage: java CPUScheduler -alg [FIFO|SJF|PR|RR] [-quantum [integer(ms)]] -input [file name]");
		System.exit(-1);
	}

}
