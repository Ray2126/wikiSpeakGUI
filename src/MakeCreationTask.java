

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;

import javafx.concurrent.Task;

public class MakeCreationTask extends Task<Void>{

	private List<String> _sentences;
	private String _wordToSearch;
	
	public MakeCreationTask(List<String> sentences, String wordToSearch) {
		_sentences = sentences;
		_wordToSearch = wordToSearch;
	}

	@Override
	protected Void call() throws Exception {
		
		Path path = Paths.get("text.txt");
		Files.write(path, _sentences, StandardCharsets.UTF_8);
		
		String cmd = "cat text.txt | text2wave -o speech.wav";
		runProcess(cmd);
		
		String cmd2 = "soxi -D speech.wav";
		//Process process = runProcess(cmd2);
		//InputStream stdout = process.getInputStream();
		//BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
		//String vidLength = stdoutBuffered.readLine();
		String vidLength = "11.87";
		System.out.println("Made vspeech" + vidLength);
		
		String cmd3 = "ffmpeg -f lavfi -i color=c=blue:s=320x240 -t " + vidLength + " -vf \"drawtext=fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text="+ _wordToSearch + "\" video.mp4 &> /dev/null";
		runProcess(cmd3);
		System.out.println("Made vid");
		
		String cmd4 = "ffmpeg -i video.mp4 -i speech.wav -strict experimental ./Creations/mango.mp4 &> /dev/null";
		runProcess(cmd4);
		System.out.println("Done");
		
		
		return null;
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
