package menon.cs6890.assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

public class MainClass {
	
	private static final String CURRENT_DIRECTORY = ".";
	private static final String QUIT_REQUEST = "Quit";
	
	public static void main(String[] args) {
		
		VectorSpaceRetriever vectorSpaceRetriever = new VectorSpaceRetriever(CURRENT_DIRECTORY);
		String userQuery;
		Double score = null;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		Map<String, Double> searchResults = null;
		Set<String> matchingDocuments = null;
		//Get user response
		while (true) {
			System.out.println("\n\nEnter search string: ");
			try {
				userQuery = bufferedReader.readLine();
				if (QUIT_REQUEST.equals(userQuery.trim())) {
					break;
				}
				
				searchResults = vectorSpaceRetriever.searchCollection(userQuery.trim());
				matchingDocuments = searchResults.keySet();
				if (matchingDocuments == null || matchingDocuments.size() == 0) {
					System.out.println("\nNo search results found for: " + userQuery.trim());
				} else {
					for (String documentName : matchingDocuments) {
						score = searchResults.get(documentName);
						System.out.format("\n%s - %f", documentName, score.doubleValue());
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
