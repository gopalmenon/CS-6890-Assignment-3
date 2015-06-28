package menon.cs6890.assignment3;

import java.util.Map;

public class VectorSpaceRetriever {
	
	private VectorSpaceIndexer documentIndexer;
	
	VectorSpaceRetriever(String pathName) {
		documentIndexer = new VectorSpaceIndexer();
		documentIndexer.indexCollection(pathName.trim());
	}
	
	//Get document scores
	public Map<String, Double> searchCollection(String userQuery) {
		return this.documentIndexer.getDocumentScores(userQuery);

	}

}
