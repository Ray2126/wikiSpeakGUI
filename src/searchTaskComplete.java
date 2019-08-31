import java.util.List;

import javafx.scene.control.Alert;

public class searchTaskComplete implements Runnable{

	private List<String> _sentences;
	private int _numSentences;
	
	public searchTaskComplete(List<String> sentences) {
		_sentences = sentences;
		_numSentences = sentences.size();
	}
	
	@Override
	public void run() {
		for(int i = 0; i<_sentences.size(); i++) {
			System.out.println(i + " - " + _sentences.get(i));
		}
		Alert numSentencesAlert = new Alert(Alert.AlertType.CONFIRMATION);
		
		
	}

}
