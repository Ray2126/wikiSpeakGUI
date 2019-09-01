package wikiSpeak;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{

	private Button _createButton;
	private Button _deleteButton;
	private Button _playButton;
	private ListView<String> _creationListView;
	private ProgressIndicator _progress;
	private BorderPane _mainPane;
	private HBox _botPane;
	private File _creationsFolder;
	private ExecutorService _team = Executors.newSingleThreadExecutor();
	
	@Override
	public void start(Stage primaryStage){
		primaryStage.setTitle("WikiSpeak");
		
		_mainPane = new BorderPane();
		
		_createButton = new Button("Create");
		_deleteButton = new Button("Delete");
		_playButton = new Button("Play");
		
		_progress = new ProgressIndicator();
		_progress.setMaxSize(26,26);
		
		_creationsFolder = new File("./Creations");
		_creationsFolder.mkdirs();

		this.refreshListView();
		
		_createButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				
				//Dialog to get word to search
				TextInputDialog searchDialog = new TextInputDialog();
				searchDialog.setTitle("WikiSearch");
				searchDialog.setHeaderText("What would you like to search?");
				searchDialog.setContentText("Enter the word you would like to search:");
				
				//When OK button in the dialog is clicked
				Button okSearchButton = (Button) searchDialog.getDialogPane().lookupButton(ButtonType.OK);
				okSearchButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						String wordToSearch = searchDialog.getEditor().getText();
						SearchTask searchJob = new SearchTask(wordToSearch);
						_team.submit(searchJob);
						_botPane.getChildren().add(_progress);
						searchJob.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
							
							@Override
							public void handle(WorkerStateEvent event) {
								try {
									_botPane.getChildren().remove(_progress);
									List<String> rawSentences = searchJob.get();
										if(rawSentences != null) {
											CreationDialog creationDetailsDialog = new CreationDialog(AlertType.CONFIRMATION, rawSentences);
											
											//When OK button in the dialog is clicked
											Button okCreationButton = (Button) creationDetailsDialog.getDialogPane().lookupButton(ButtonType.OK);
											okCreationButton.setOnAction(new EventHandler<ActionEvent>() {
		
												@Override
												public void handle(ActionEvent e) {
													String creationName = creationDetailsDialog.getTextField().getText();
													//Check if entered name is invalid (must only be letters, digits, hyphen or underscore)
													if((creationName.matches("^[a-zA-Z0-9_-]*$")) | !(creationName.trim().equals("")) | (creationName.equals(null))) {
														
														//Get the specified number of sentences
														int selectedNumOfSentences = Integer.parseInt(creationDetailsDialog.getComboBox().getValue());
														List<String> newSentences = new ArrayList<String>();
														for(int i =0; i<selectedNumOfSentences; i++) {
															newSentences.add(rawSentences.get(i));
														}
														
														MakeCreationTask createJob = new MakeCreationTask(newSentences, wordToSearch, creationName);
														
														//Check if file already exists
														if(new File("./Creations", creationName + ".mp4").exists()) {
															Runnable fileAlreadyExists = new Runnable() {

																@Override
																public void run() {
																	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
																	alert.setTitle("File already exists");
																	alert.setHeaderText(null);
																	alert.setContentText("The creation name you have entered already exists. Would you like to overwrite?");
																	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
																	Optional<ButtonType> result = alert.showAndWait();
																	
																	//If they press OK, delete the old file and create a new one
																	if(result.get() == ButtonType.OK) {
																		File file = new File(_creationsFolder.toString() + "/" + creationName + ".mp4");
																		file.delete();
																		runCreateCreationTask(createJob);
																	}
																}
																
															};
															Platform.runLater(fileAlreadyExists);
														}
														else {
															runCreateCreationTask(createJob);
														}
													}	
													else {
														Runnable invalidName = new Runnable() {

															@Override
															public void run() {
																Alert alert = new Alert(Alert.AlertType.ERROR);
																alert.setTitle("Invalid Name");
																alert.setHeaderText(null);
																alert.setContentText("The creation name you have entered is invalid. "
																		+ "Please only use letters, numbers, underscores or hyphens.");
																alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
																alert.showAndWait();
															}
															
														};
														Platform.runLater(invalidName);
													}
												}
											});
											creationDetailsDialog.showAndWait();
										}
									} catch (InterruptedException | ExecutionException exception) {
										exception.printStackTrace();
									}
							}
						});
					}
				});
				searchDialog.showAndWait();
			}
			
			
		});
		
		_deleteButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String selectedItem = _creationListView.getSelectionModel().getSelectedItem();
				
				if(selectedItem != null) {
					//Create a dialog for user to confirm their delete selection
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
			}
			
		});
		
		_playButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String selectedItem = _creationListView.getSelectionModel().getSelectedItem();
				String cmd = "ffplay -autoexit ./Creations/"+ selectedItem +" &> /dev/null";
				ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
				try {
					pb.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		
		//Make the progress indicator anchor to right side
		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);
		
		_botPane = new HBox(10, _createButton, _deleteButton, _playButton, region);
		_mainPane.setBottom(_botPane);
		
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
	
	private void runCreateCreationTask(MakeCreationTask createJob) {
		_team.submit(createJob);
		_botPane.getChildren().add(_progress);
		createJob.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				Runnable r = new Runnable() {

					@Override
					public void run() {
						_botPane.getChildren().remove(_progress);
					}
					
				};
				Platform.runLater(r);
				refreshListView();
			}
			
		});
	}
	
	private void refreshListView() {
		File[] creations = _creationsFolder.listFiles();
		ObservableList<String> creationFileNames = FXCollections.observableArrayList();
		for(int i = 0; i < creations.length; i++) {
			creationFileNames.add(creations[i].getName());
		}
		Collections.sort(creationFileNames);
		_creationListView = new ListView<String>(creationFileNames);
		_mainPane.setCenter(_creationListView);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
