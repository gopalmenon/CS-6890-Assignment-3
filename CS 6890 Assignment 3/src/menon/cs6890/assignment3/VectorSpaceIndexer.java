package menon.cs6890.assignment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VectorSpaceIndexer {
	
	private StopWords stopWords;
	private Map<String, WordVector> documentIndex;
	private Map<String, DocumentMetrics> CollectionWordCounts;
	private int documentsInCollection;
	
	private static final String WHITE_SPACE_REGEX = "\\s";
	private static final String[] punctuationMarks = {"!", "?", ".", ",", "'"};
	
	//Constructor
	VectorSpaceIndexer() {
		
		this.stopWords = new StopWords();
		this.documentIndex = new HashMap<String, WordVector>();
		this.CollectionWordCounts = new HashMap<String, DocumentMetrics>();
		
	}
	
	//Index the documents in the directory
	public void indexCollection(String directory) {
		
		Set<File> filesInCurrentDirectory = getFilesToBeIndexed(directory);
		if (filesInCurrentDirectory == null) {
			return;
		}
		
		this.documentsInCollection = filesInCurrentDirectory.size();
		
		//Create the index for the documents
		for (File document : filesInCurrentDirectory) {
			this.documentIndex.put(document.getName(), getDocumentWordVector(document));
		}
		
		//Compute Inverse Document Frequency for each word in the collection
		assignInverseDocumentFrequency();
		
		//Assign term frequency to each word in every document
		assignTermFrequency();		
	}
	
	//Return a list of files in the directory
	private Set<File> getFilesToBeIndexed(String pathName) {
		
		Set<File> filesInCurrentDirectory = new HashSet<File>();
		File directory = new File(pathName.trim());
		if (directory != null && directory.isDirectory()) {
			File[] listOfFiles = directory.listFiles(); 
			for (File document : listOfFiles) {
				if (document.isFile() && !StopWords.stopWordsFile.equals(document.getName()) && (document.getName().endsWith("txt") || document.getName().endsWith("TXT"))) {
					filesInCurrentDirectory.add(document);
				}
			}
			return filesInCurrentDirectory;
		} else {
			System.err.println("Directory " + pathName.trim() + " was not found.");
			return null;
		}
	}
	
	//Return a word vector for the document
	private WordVector getDocumentWordVector(File document) {
		
		WordVector wordVector = new WordVector();
		
		ArrayList<String> wordsInDocument = getWordsInDocument(document);
		for (String word : wordsInDocument) {
			wordVector.addWord(word);
		}
		return wordVector;
		
	}

	private ArrayList<String> getWordsInDocument(File document) {
		
		ArrayList<String> wordsInDocument = new ArrayList<String>();
		
		try {
			BufferedReader textFileReader = new BufferedReader(new FileReader(document));
			String textLine = textFileReader.readLine();
			ArrayList<String> stemmedWords = null;
			while(textLine != null) {
				if (textLine.trim().length() > 0) {
					stemmedWords = getStemmedWords(textLine.toLowerCase());
					if (stemmedWords != null && stemmedWords.size() > 0) {
						wordsInDocument.addAll(stemmedWords);
					}
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

		return wordsInDocument;
		
	}
	
	//From a line of text in a document, get a list of stemmed words after eliminating stop words
	private ArrayList<String> getStemmedWords(String textLine) {
		
		if (textLine == null || textLine.trim().length() == 0) {
			return null;
		}
		
		ArrayList<String> wordsInLine = new ArrayList<String>();
		//Inspect each word in the line
		String[] tokens = textLine.trim().split(WHITE_SPACE_REGEX);
		for (String word : tokens) {
			//Eliminate stop words
			if (!this.stopWords.isStopWord(word.trim())) {
				//Strip out ending punctuation marks
				word = stripEndingPunctuations(word.trim());
				if (word != null && word.trim().length() != 0) {
					//Add to document vector
					wordsInLine.add(word.trim());
					//Add to collection counts
					addToCollectionCounts(word.trim());
				}
			}
		}
		
		return wordsInLine;
		
	}
	
	//Strip punctuation marks at the end of the word
	private String stripEndingPunctuations(String word) {
		
		if (word == null || word.trim().length() == 0) {
			return null;
		}
		
		//Strip out punctuation characters starting at the end
		boolean punctuationFound;
		//Strip out ending punctuation marks
		while (word.length() > 0) {
			punctuationFound = false;
			for (String punctuation : punctuationMarks) {
				if (word.endsWith(punctuation)) {
					word = word.substring(0, word.length() - 1);
					punctuationFound = true;
					break;
				}
			}
			if (!punctuationFound) {
				break;
			}
		}

		//Strip out beginning punctuation marks
		while (word.length() > 0) {
			punctuationFound = false;
			for (String punctuation : punctuationMarks) {
				if (word.startsWith(punctuation)) {
					word = word.substring(1, word.length());
					punctuationFound = true;
					break;
				}
			}
			if (!punctuationFound) {
				break;
			}
		}

		return stem(word);
		
	}
	
	//Stem the word
	private String stem(String word) {
		
		if (word.trim().length() > 0) {
			Stemmer stemmer = new Stemmer();
			stemmer.add(word.trim().toCharArray(), word.trim().length());
			stemmer.stem();
			return stemmer.toString();
		}
		
		return null;
	}
	
	//Add the word to the collection word counts
	private void addToCollectionCounts(String word) {
		
		DocumentMetrics documentMetrics;
		if (this.CollectionWordCounts.containsKey(word)) {
			documentMetrics = this.CollectionWordCounts.get(word);
			documentMetrics.setWordCount(documentMetrics.getWordCount() + 1);
			this.CollectionWordCounts.put(word, documentMetrics);
		} else {
			documentMetrics = new DocumentMetrics(1, 0.0);
			this.CollectionWordCounts.put(word, documentMetrics);
		}
		
	}
	
	//Compute IDF for each word in the collection
	private void assignInverseDocumentFrequency() {
		
		Set<String> wordsInCollection = this.CollectionWordCounts.keySet();
		double log2Base10 = Math.log10(2.0);
		
		//Compute IDF for each word in collection
		int documentsContainingWord = 0;
		double inverseDocumentFrequency = 0.0;
		DocumentMetrics documentMetrics = null;
		for (String word : wordsInCollection) {
			documentsContainingWord = 0;
			Set<String> documentNames = this.documentIndex.keySet();
			for (String document : documentNames) {
				WordVector wordVector = this.documentIndex.get(document);
				if (wordVector.containsWord(word.trim())) {
					++documentsContainingWord;
				}
			}
			inverseDocumentFrequency = (Math.log10(this.documentsInCollection) - Math.log10(documentsContainingWord)) / log2Base10;
			documentMetrics = this.CollectionWordCounts.get(word.trim());
			documentMetrics.setWeight(inverseDocumentFrequency);
			this.CollectionWordCounts.put(word.trim(), documentMetrics);
		}
	}
	
	//Compute term frequency for each document word
	private void assignTermFrequency() {
		
		WordVector wordVector = null;
		Set<String> documentNames = this.documentIndex.keySet();
		for (String document : documentNames) {
			wordVector = this.documentIndex.get(document.trim());
			Set<String> wordsInDocument = wordVector.keySet();
			for (String word : wordsInDocument) {
				if (this.CollectionWordCounts.containsKey(word.trim())) {
					wordVector.setTermWeight(word.trim(), this.CollectionWordCounts.get(word.trim()).getWeight());
				}
			}
			this.documentIndex.put(document.trim(), wordVector);
		}
	}
	
	//Return cosine scores corresponding to user query
	public Map<String, Double> getDocumentScores(String userQuery) {
		
		Map<String, Double> cosineScores = new HashMap<String, Double>();
		DocumentMetrics documentMetrics = null;
		WordVector wordVector = null;
		double score = 0.0;
		Double currentScore = null;
		Map<String, WordVector> documentIndex = null;
		//Tokenize the user query
		String[] tokens = userQuery.trim().toLowerCase().split(WHITE_SPACE_REGEX);
		for (String word : tokens) {
			//Eliminate stop words
			if (!this.stopWords.isStopWord(word.trim())) {
				//Strip out ending punctuation marks
				word = stripEndingPunctuations(word.trim());
				if (word != null && word.trim().length() != 0) {
					//For each token in the user query 
					if (this.CollectionWordCounts.containsKey(word.trim())) {
						documentIndex = this.documentIndex;
						Set<String> documents = documentIndex.keySet();
						//Calculate and store similarity scores of word in user query to score map
						for (String document : documents) {
							score = 0.0;
							wordVector = documentIndex.get(document.trim());
							if (wordVector != null) {
								if (wordVector.containsWord(word.trim())) {
									documentMetrics = wordVector.get(word.trim());
									score = documentMetrics.getWeight();
									if (cosineScores.containsKey(document.trim())) {
										currentScore = cosineScores.get(document.trim());
										currentScore = Double.valueOf(currentScore.doubleValue() + score);
									} else {
										currentScore = Double.valueOf(score);
									}
									cosineScores.put(document.trim(), currentScore);
								}
							}
						}
					}
				}
			}
		}
		return cosineScores;
	}
	
}
