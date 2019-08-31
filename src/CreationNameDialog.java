import javafx.scene.control.TextInputDialog;

public class CreationNameDialog extends TextInputDialog{

	private String _creationName;
	
	public CreationNameDialog() {
		setHeaderText("What would you like to name the creation?");
		setContentText("Enter the name of the creation: ");
		showAndWait();
		String creationName = getEditor().getText();
		System.out.println(creationName);
	}
	
	
}
