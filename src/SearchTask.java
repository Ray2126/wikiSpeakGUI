import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class SearchTask extends Task<List<String>>{

	private String _wordToSearch;
	
	public SearchTask(String wordToSearch) {
		_wordToSearch = wordToSearch;
	}
	
	@Override
	protected List<String> call() throws Exception {
		String cmd = "wikit " + _wordToSearch;
		ProcessBuilder search = new ProcessBuilder("bash", "-c", cmd);
		
		List<String> sentences = new ArrayList<String>();
		
		Process process = search.start();
		process.waitFor();
		
		//Get output from wikit command
		InputStream stdout = process.getInputStream();
		BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
		String paragraph = stdoutBuffered.readLine();
		
		//Separate stdout into single sentences
		paragraph = paragraph.replaceAll("\\.\\s?", "\\.\n");
		String line[] = paragraph.split("\\r?\\n");
		for(int i = 0; i<line.length;i++) {
			sentences.add(line[i].trim());
		}
		
		System.out.println("waiting");
		
		if(sentences.get(0).contains("not found")) {
			SearchTaskFailed failed = new SearchTaskFailed(_wordToSearch);
			Platform.runLater(failed);
			System.out.println("failed");
			return null;
		}
		
		return sentences;
	}

}
