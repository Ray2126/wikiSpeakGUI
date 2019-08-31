
import javafx.scene.control.Button;

import java.io.File;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application{

	private Button _createButton;
	private Button _deleteButton;
	private Button _playButton;
	private ListView<String> _creationList;
	private searchDialog _searchDialog;
	
	@Override
	public void start(Stage primaryStage){
		primaryStage.setTitle("WikiSpeak");
		
		BorderPane pane = new BorderPane();
		
		_createButton = new Button("Create");
		_deleteButton = new Button("Delete");
		_playButton = new Button("Play");
		
		_searchDialog = new searchDialog();
		
		//Create file for creations
		File creationFolder = new File("./Creations");
		creationFolder.mkdirs();
		
		//Get all files in the creations folder
		File[] creations = creationFolder.listFiles();
		ObservableList<String> creationFileNames = FXCollections.observableArrayList();
		for(int i = 0; i < creations.length; i++) {
			creationFileNames.add(creations[i].getName());
		}
		
		_creationList = new ListView<String>(creationFileNames);
		
		_createButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				_searchDialog.showAndWait();
				
			}
			
		});
		
		
		
		HBox botPane = new HBox(10, _createButton, _deleteButton, _playButton);
		pane.setCenter(_creationList);
		pane.setBottom(botPane);
		
		
		Scene scene = new Scene(pane, 500,300);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
