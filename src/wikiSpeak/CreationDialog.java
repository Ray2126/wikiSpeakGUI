package wikiSpeak;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class CreationDialog extends Alert{

	private Label _sentencesLabel;
	private TextArea _sentenceTextArea;
	private Label _creationNameLabel;
	private TextField _creationNameTextInput;
	private ComboBox<String> _numSelector;

	public CreationDialog(AlertType alertType, List<String> sentences) {
		super(alertType);
		setTitle("Creation Details");
		setHeaderText("Enter the details of the creation.");
		
		_sentencesLabel = new Label("Choose the number of sentences: ");
		_sentenceTextArea = new TextArea();
		_creationNameLabel = new Label("Enter the name of the creation: ");
		_creationNameTextInput = new TextField("default_name");
		_numSelector = new ComboBox<String>();
		
		BorderPane content = new BorderPane();
		FlowPane selectPane = new FlowPane();
		FlowPane creationNamePane = new FlowPane();
		
		//Add sentences to the textArea and add numbers to ComboBox
		ObservableList<String> sentenceNumbersObservable = FXCollections.observableArrayList();
		for(int i = 1; i < sentences.size()+1; i++) {
			_sentenceTextArea.appendText("[" + i + "]  " + sentences.get(i-1) + "\n");
			sentenceNumbersObservable.add(i + "");
		}
		_numSelector.setItems(sentenceNumbersObservable);	
		
		selectPane.getChildren().addAll(_sentencesLabel, _numSelector);
		creationNamePane.getChildren().addAll(_creationNameLabel, _creationNameTextInput);
		
		content.setTop(_sentenceTextArea);
		content.setCenter(selectPane);
		content.setBottom(creationNamePane);
		
		getDialogPane().setContent(content);
	}
	
	public TextField getTextField() {
		return _creationNameTextInput;
	}

	public ComboBox<String> getComboBox() {
		return _numSelector;
	}
	
}
