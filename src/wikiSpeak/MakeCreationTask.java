package wikiSpeak;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public class MakeCreationTask extends Task<Void>{

	private List<String> _sentences;
	private String _wordToSearch;
	private String _creationName;
	
	public MakeCreationTask(List<String> sentences, String wordToSearch, String creationName) {
		_sentences = sentences;
		_wordToSearch = wordToSearch;
		_creationName = creationName;
	}

	@Override
	protected Void call() throws Exception {
		
		Path path = Paths.get("text.txt");
		Files.write(path, _sentences, StandardCharsets.UTF_8);
		
		String cmd = "cat text.txt | text2wave -o speech.wav";
		Process process = runProcess(cmd);
		if(process.exitValue() == 0) {
			String cmd2 = "soxi -D speech.wav";
			process = runProcess(cmd2);
			if(process.exitValue() == 0) {
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String vidLength = stdoutBuffered.readLine();
				String cmd3 = "ffmpeg -f lavfi -i color=c=blue:s=320x240 -t " + vidLength + " -vf \"drawtext=fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text="+ _wordToSearch + "\" video.mp4 &> /dev/null";
				process = runProcess(cmd3);
				if(process.exitValue() == 0) {
					String cmd4 = "ffmpeg -i video.mp4 -i speech.wav -strict experimental ./Creations/" + _creationName + ".mp4 &> /dev/null";
					runProcess(cmd4);
					if(process.exitValue() == 0) {
						Runnable createdSuccess = new Runnable() {

							@Override
							public void run() {
								Alert alert = new Alert(Alert.AlertType.INFORMATION);
								alert.setTitle("Success!");
								alert.setHeaderText(null);
								alert.setContentText(_creationName + " had been made successfully and is ready to play!");
								alert.showAndWait();
							}
							
						};
						Platform.runLater(createdSuccess); 
					}
					else {
						processError();
					}
				}
				else {
					processError();
				}
			}
			else {
				processError();
			}
		}
		else {
			processError();
		}

		//Remove temporary files after creation is done
		List<File> tempFiles = new ArrayList<File>();
		tempFiles.add(new File("text.txt"));
		tempFiles.add(new File("speech.wav"));
		tempFiles.add(new File("video.mp4"));
		for(int i = 0; i < tempFiles.size(); i++) {
			tempFiles.get(i).delete();
		}
		
		return null;
	}	
	
	private void processError() {
		Runnable createdFailed = new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Failure!");
				alert.setHeaderText(null);
				alert.setContentText(_creationName + " could not be created!");
				alert.showAndWait();
			}
			
		};
		Platform.runLater(createdFailed); 
	}

	private Process runProcess(String cmd) {
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
		Process process;
		try {
			process = pb.start();
			process.waitFor();
			return process;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return null;
	}
}
