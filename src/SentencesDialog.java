import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class SentencesDialog extends Alert{

	private TextArea _sentenceTextArea;
	private ComboBox<String> _numSelector;
	private int _selectedNum;
	private MakeCreationTask _job;
	private ExecutorService _team = Executors.newSingleThreadExecutor(); 
	
	public SentencesDialog(AlertType alertType, List<String> sentences, String wordToSearch) {
		super(alertType);
		setTitle("Number of sentences");
		setHeaderText("How many sentences would you like in the final creation?");
		
		_sentenceTextArea = new TextArea();
		ObservableList<String> sentenceNumbersObservable = FXCollections.observableArrayList();
		
		for(int i = 1; i < sentences.size()+1; i++) {
			_sentenceTextArea.appendText("[" + i + "]  " + sentences.get(i-1) + "\n");
			sentenceNumbersObservable.add(i + "");
		}
		
		_numSelector = new ComboBox<String>();
		_numSelector.setItems(sentenceNumbersObservable);
		
		BorderPane content = new BorderPane();
		FlowPane selectPane = new FlowPane();
		
		Label label = new Label("Choose the number of sentences: ");
		
		selectPane.getChildren().addAll(label, _numSelector);
		
		content.setTop(_sentenceTextArea);
		content.setBottom(selectPane);
		
		getDialogPane().setContent(content);
		
		Button okButton = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				_selectedNum = Integer.parseInt(_numSelector.getValue());
				List<String> newSentences = new ArrayList<String>();
				for(int i =0; i<_selectedNum; i++) {
					newSentences.add(sentences.get(i));
				}
				_job = new MakeCreationTask(newSentences, wordToSearch);
				_team.submit(_job);
				
			}
			
		});
	}

	
}
