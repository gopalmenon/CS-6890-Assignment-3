package menon.cs6890.assignment3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StopWords {
	
	public static final String stopWordsFile = "stopWords.txt";
	private Set<String> stopWordList;
	
	//Constructor
	StopWords() {
		
		this.stopWordList = new HashSet<String>();
		
		//Read all words from stop words file and put into the list of stop words
		try {
			BufferedReader textFileReader = new BufferedReader(new FileReader(stopWordsFile));
			String textLine = textFileReader.readLine();
			while(textLine != null) {
				if (textLine.trim().length() > 0) {
					stopWordList.add(textLine.toLowerCase());
				}
				textLine = textFileReader.readLine();
			}
			textFileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isStopWord(String word) {
		
		if (word == null || word.trim().length() == 0) {
			return false;
		}
		
		return this.stopWordList.contains(word.trim());
	}
}
