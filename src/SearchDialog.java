

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.TextInputDialog;


public class SearchDialog extends TextInputDialog {
	
	private ExecutorService _team = Executors.newSingleThreadExecutor(); 
	private SearchTask _job;
	private List<String> _rawSentences = new ArrayList<String>();
	private SentencesDialog _sentencesDialog;
	
	
	public SearchDialog() {
		super();
		setTitle("WikiSearch");
		setHeaderText("What would you like to search?");
		setContentText("Enter the word you would like to search:");	
		Button okButton = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String wordToSearch = getEditor().getText();
				_job = new SearchTask(wordToSearch);
				try {
					_team.submit(_job);
					_job.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

						@Override
						public void handle(WorkerStateEvent event) {
							try {
								_rawSentences = _job.get();
								if(_rawSentences != null) {
									_sentencesDialog = new SentencesDialog(Alert.AlertType.CONFIRMATION, _rawSentences, wordToSearch);
									_sentencesDialog.showAndWait();
								}

									

							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}
						}
					
					});

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
	}


}
