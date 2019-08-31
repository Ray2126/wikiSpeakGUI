

import java.util.List;
import javafx.concurrent.Task;

public class MakeCreationTask extends Task<Void>{

	private List<String> _sentences;
	
	public MakeCreationTask(List<String> sentences) {
		_sentences = sentences;
	}

	@Override
	protected Void call() throws Exception {
		
		
		
		return null;
	}
	
}
