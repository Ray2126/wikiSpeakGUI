
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private Button _refreshButton;
	private ListView<String> _creationListView;
	private SearchDialog _searchDialog;
	private File _creationsFolder;
	
	@Override
	public void start(Stage primaryStage){
		primaryStage.setTitle("WikiSpeak");
		
		BorderPane pane = new BorderPane();
		
		_createButton = new Button("Create");
		_deleteButton = new Button("Delete");
		_playButton = new Button("Play");
		_refreshButton = new Button("Refresh");
		
		_searchDialog = new SearchDialog();
		
		//Create file for creations
		_creationsFolder = new File("./Creations");
		_creationsFolder.mkdirs();
		
		//Get all files in the creations folder
		ObservableList<String> creationFileNames = refreshListView();
		_creationListView = new ListView<String>(creationFileNames);
		
		_createButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				_searchDialog.showAndWait();
				_searchDialog.setOnCloseRequest(new EventHandler<DialogEvent>() {

					@Override
					public void handle(DialogEvent arg0) {
						
					}
				});
			}
			
		});
		
		
		
		HBox botPane = new HBox(10, _createButton, _deleteButton, _playButton, _refreshButton);
		pane.setCenter(_creationListView);
		pane.setBottom(botPane);
		
		
		Scene scene = new Scene(pane, 500,300);
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
	
	private ObservableList<String> refreshListView() {
		File[] creations = _creationsFolder.listFiles();
		ObservableList<String> creationFileNames = FXCollections.observableArrayList();
		for(int i = 0; i < creations.length; i++) {
			creationFileNames.add(creations[i].getName());
		}
		return creationFileNames;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
