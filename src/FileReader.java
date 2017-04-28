import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileReader extends Thread {
	
	File inputFile;

	/**
	 * Constructs a new FileReader to read input from the given file
	 */
	public FileReader(String fileName) {
		inputFile = new File(fileName);
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
				
					String[] parse = line.split(" ");
					
					switch (parse[0]) {
					case "proc":
						break;
					case "sleep":
						break;
					case "stop":
						break;
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
		
	}

}
