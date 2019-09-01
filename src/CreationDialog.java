import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class CreationDialog extends Alert{

	private Label _sentencesLabel;
	private Label _creationNameLabel;
	private TextArea _sentenceTextArea;
	private ComboBox<String> _numSelector;
	private TextField _creationNameTextInput;
	private MakeCreationTask _job;
	private ExecutorService _team = Executors.newSingleThreadExecutor(); 
	
	
	public CreationDialog(AlertType alertType, List<String> sentences, String wordToSearch) {
		super(alertType);
		setTitle("Creation Details");
		setHeaderText("Enter the details of the creation.");
		
		_sentencesLabel = new Label("Choose the number of sentences: ");
		_creationNameLabel = new Label("Enter the name of the creation: ");
		_sentenceTextArea = new TextArea();
		_numSelector = new ComboBox<String>();
		_creationNameTextInput = new TextField("default_name");
		
		BorderPane content = new BorderPane();
		FlowPane selectPane = new FlowPane();
		FlowPane creationNamePane = new FlowPane();
		FlowPane outerPane = new FlowPane(Orientation.VERTICAL);
		
		//Add sentences to the textArea
		ObservableList<String> sentenceNumbersObservable = FXCollections.observableArrayList();
		for(int i = 1; i < sentences.size()+1; i++) {
			_sentenceTextArea.appendText("[" + i + "]  " + sentences.get(i-1) + "\n");
			sentenceNumbersObservable.add(i + "");
		}
		
		
		_numSelector.setItems(sentenceNumbersObservable);	
		
		selectPane.getChildren().addAll(_sentencesLabel, _numSelector);
		creationNamePane.getChildren().addAll(_creationNameLabel, _creationNameTextInput);
		
		outerPane.getChildren().addAll(selectPane, creationNamePane);
		
		content.setTop(_sentenceTextArea);
		content.setBottom(outerPane);
		getDialogPane().setContent(content);
		
//		Button okButton = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
//		okButton.setOnAction(new EventHandler<ActionEvent>() {
//
//			@Override
//			public void handle(ActionEvent arg0) {
//				
//				String creationName = _creationNameTextInput.getText();
//				
//				//Get the selected number of sentences
//				int selectedNumOfSentences = Integer.parseInt(_numSelector.getValue());
//				List<String> newSentences = new ArrayList<String>();
//				for(int i =0; i<selectedNumOfSentences; i++) {
//					newSentences.add(sentences.get(i));
//				}
//				
//				_job = new MakeCreationTask(newSentences, wordToSearch, creationName);
//				_team.submit(_job);
//				
//				_job.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//
//					@Override
//					public void handle(WorkerStateEvent arg0) {
//						System.out.println("Creation made");
//					}
//					
//				});
//				
//				
//			}
//			
//		});
	}
	
	public TextField getTextField() {
		return _creationNameTextInput;
	}

	public ComboBox<String> getComboBox() {
		return _numSelector;
	}
	
}
