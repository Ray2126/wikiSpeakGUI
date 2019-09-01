
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Labeled;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{

	private Button _createButton;
	private Button _deleteButton;
	private Button _playButton;
	private ListView<String> _creationListView;
	private SearchDialog _searchDialog;
	private File _creationsFolder;
	private ExecutorService _team = Executors.newSingleThreadExecutor(); 
	private CreationDialog _creationDetailsDialog;
	private BorderPane _mainPane;
	
	@Override
	public void start(Stage primaryStage){
		primaryStage.setTitle("WikiSpeak");
		
		_mainPane = new BorderPane();
		
		_createButton = new Button("Create");
		_deleteButton = new Button("Delete");
		_playButton = new Button("Play");
		
		_searchDialog = new SearchDialog();
		
		//Create file for creations
		_creationsFolder = new File("./Creations");
		_creationsFolder.mkdirs();
		
		//Get all files in the creations folder
		this.refreshListView();
		
		
		_createButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				Button okSearchButton = (Button) _searchDialog.getDialogPane().lookupButton(ButtonType.OK);
				okSearchButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						System.out.println("okSearchButton pressed");
						String wordToSearch = _searchDialog.getEditor().getText();
						SearchTask searchJob = new SearchTask(wordToSearch);
						_team.submit(searchJob);
						searchJob.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
							
							@Override
							public void handle(WorkerStateEvent event) {
								try {
									List<String> rawSentences = searchJob.get();
										if(rawSentences != null) {
											_creationDetailsDialog = new CreationDialog(AlertType.CONFIRMATION, rawSentences, wordToSearch);
											
											
											Button okCreationButton = (Button) _creationDetailsDialog.getDialogPane().lookupButton(ButtonType.OK);
											okCreationButton.setOnAction(new EventHandler<ActionEvent>() {
		
												@Override
												public void handle(ActionEvent arg0) {
													
													String creationName = _creationDetailsDialog.getTextField().getText();
													
													//Get the selected number of sentences
													int selectedNumOfSentences = Integer.parseInt(_creationDetailsDialog.getComboBox().getValue());
													List<String> newSentences = new ArrayList<String>();
													for(int i =0; i<selectedNumOfSentences; i++) {
														newSentences.add(rawSentences.get(i));
													}
													
													MakeCreationTask createJob = new MakeCreationTask(newSentences, wordToSearch, creationName);
													_team.submit(createJob);
													
													createJob.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		
														@Override
														public void handle(WorkerStateEvent arg0) {
															refreshListView();
														}
														
													});
													
													
												}
												
											});
											_creationDetailsDialog.showAndWait();
										}
									} catch (InterruptedException | ExecutionException e) {
										e.printStackTrace();
									}
									
							}
							
						});
					}
					
				});
				_searchDialog.showAndWait();
			}
			
			
		});
		
		_deleteButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String selectedItem = _creationListView.getSelectionModel().getSelectedItem();
				Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
				((Button) confirmAlert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
				confirmAlert.setTitle("Confirmation");
				confirmAlert.setHeaderText(null);
				confirmAlert.setContentText("You are about to delete " + selectedItem + ". Are you sure?");
				Optional<ButtonType> result = confirmAlert.showAndWait();
				if(result.get() == ButtonType.OK) {
					File file = new File(_creationsFolder.toString() + "/" + selectedItem);
					file.delete();
				}
				refreshListView();
				
			}
			
		});
		
		_playButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String selectedItem = _creationListView.getSelectionModel().getSelectedItem();
				String cmd = "ffplay -autoexit ./Creations/"+ selectedItem +" &> /dev/null";
				System.out.println(cmd);
				ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
				try {
					pb.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		
		
		HBox botPane = new HBox(10, _createButton, _deleteButton, _playButton);
		_mainPane.setBottom(botPane);
		
		
		Scene scene = new Scene(_mainPane, 500,300);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent e) {
				Platform.exit();
				System.exit(0);
			}
			
		});
	}
	
	private void refreshListView() {
		File[] creations = _creationsFolder.listFiles();
		ObservableList<String> creationFileNames = FXCollections.observableArrayList();
		for(int i = 0; i < creations.length; i++) {
			creationFileNames.add(creations[i].getName());
		}
		_creationListView = new ListView<String>(creationFileNames);
		_mainPane.setCenter(_creationListView);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
