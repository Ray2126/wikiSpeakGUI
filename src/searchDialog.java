
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;


public class searchDialog extends TextInputDialog {
	
	private ExecutorService _team = Executors.newFixedThreadPool(3); 
	
	public searchDialog() {
		super();
		this.setTitle("WikiSearch");
		setHeaderText("What would you like to search?");
		setContentText("Enter the word you would like to search:");	
		Button okButton = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				String wordToSearch = getEditor().getText();
				System.out.println(wordToSearch);
				searchTask searchTask = new searchTask(wordToSearch);
				try {
					_team.submit(searchTask);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
			
		});
	
	}
	
	
	
}
