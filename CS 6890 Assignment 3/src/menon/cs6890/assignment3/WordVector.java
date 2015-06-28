package menon.cs6890.assignment3;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WordVector {

	private Map<String, DocumentMetrics> documentWordMetrics;
	
	//Constructor
	WordVector() {
		documentWordMetrics = new HashMap<String, DocumentMetrics>();
	}
	
	//Add a word
	public void addWord(String word) {
		
		if (word == null || word.trim().length() == 0) {
			return;
		}
		
		//If the word already exists, increment the count, else create an entry with count of 1
		DocumentMetrics documentWordCount = null;
		if (this.documentWordMetrics.containsKey(word.trim())) {
			documentWordCount = this.documentWordMetrics.get(word.trim());
			documentWordCount.setWordCount(documentWordCount.getWordCount() + 1);
		} else {
			documentWordCount = new DocumentMetrics(1, 0.0);
		}
		this.documentWordMetrics.put(word.trim(), documentWordCount);
	}
	
	//Get the count of a word
	public int getWordCount(String word) {
		
		if (word == null || word.trim().length() == 0) {
			return 0;
		}
		
		if (this.documentWordMetrics.containsKey(word.trim())) {
			return this.documentWordMetrics.get(word.trim()).getWordCount();
		} else {
			return 0;
		}
		
	}
	
	//Return boolean value for whether the word is present or not
	public boolean containsWord(String word) {
		
		if (word == null || word.trim().length() == 0) {
			return false;
		}
		
		return this.documentWordMetrics.containsKey(word.trim());
	}
	
	//Return a key set
	public Set<String> keySet() {
		return this.documentWordMetrics.keySet();
	}
	
	//Return a map entry
	public DocumentMetrics get(String word) {
		
		if (this.documentWordMetrics.containsKey(word.trim())) {
			return this.documentWordMetrics.get(word.trim());
		} else {
			return null;
		}
	}
	
	//Update the term weight
	void setTermWeight(String word, double inverseDocumentFrequency) {
		DocumentMetrics documentMetrics = this.documentWordMetrics.get(word.trim());
		documentMetrics.setWeight(documentMetrics.getWordCount() * inverseDocumentFrequency);
	}


}


