import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class searchTaskFailed implements Runnable{
	
	private String _wordToSearch;
	
	public searchTaskFailed(String wordToSearch) {
		_wordToSearch = wordToSearch;
	}

	@Override
	public void run() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText(_wordToSearch + " not found!");
		alert.setContentText("The word you are trying to search could not be found with wikit!");
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

}
