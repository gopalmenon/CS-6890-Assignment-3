package menon.cs6890.assignment3;

public class DocumentMetrics {

	private int wordCount;
	private double weight;
	
	DocumentMetrics(int wordCount, double weight) {
		this.wordCount = wordCount;
		this.weight = weight;
	}
	
	public int getWordCount() {
		return wordCount;
	}
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
