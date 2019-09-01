package wikiSpeak;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public class SearchTask extends Task<List<String>>{

	private String _wordToSearch;
	
	public SearchTask(String wordToSearch) {
		_wordToSearch = wordToSearch;
	}
	
	@Override
	protected List<String> call() throws Exception {
		String cmd = "wikit " + _wordToSearch;
		ProcessBuilder search = new ProcessBuilder("bash", "-c", cmd);
		Process process = search.start();
		process.waitFor();
		
		//Get output from wikit command
		InputStream stdout = process.getInputStream();
		BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
		String paragraph = stdoutBuffered.readLine();
		
		//If wikit didn't return anything
		if(paragraph.contains(":^(")) {
			Runnable r = new Runnable() {

				@Override
				public void run() {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("ERROR");
					alert.setHeaderText(null);
					alert.setContentText(_wordToSearch + " did not return an entry via wikit.");
					alert.showAndWait();
				}
				
			};
			Platform.runLater(r);
			return null;
		}
		
		//Separate stdout into single sentences
		paragraph = paragraph.replaceAll("\\.\\s?", "\\.\n");
		String line[] = paragraph.split("\\r?\\n");
		List<String> sentences = new ArrayList<String>();
		for(int i = 0; i<line.length;i++) {
			sentences.add(line[i].trim());
		}
		
		return sentences;
	}

}
