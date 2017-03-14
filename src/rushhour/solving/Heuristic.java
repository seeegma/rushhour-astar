package rushhour.solving;

import rushhour.core.Board;

import java.util.Comparator;

/**
 * Represents a linear combination of features and weights
 */
public class Heuristic {

	private Feature[] features;
	private double[] weights;

	public Heuristic(Feature[] features, double[] weights) {
		if(features.length != weights.length) {
			System.err.println("lengths of feature vector and weight vector do not match!");
			System.exit(1);
		}
		this.features = features;
		this.weights = weights;
		this.normalizeWeights();
	}

	public double value(Board board) {
		double result = 0.0;
		for(int i=0; i<this.features.length; i++) {
			result += this.features[i].value(board) * this.weights[i];
		}
		return result;
	}

	public int numFeatures() {
		return this.features.length;
	}

	public double getWeight(int i) {
		return this.weights[i];
	}

	public double[] getWeights() {
		return this.weights;
	}

	private void normalizeWeights() {
		double total = 0.0;
		for(int i=0; i<this.weights.length; i++) {
			total += this.weights[i];
		}
		for(int i=0; i<this.weights.length; i++) {
			this.weights[i] /= total;
		}
	}

}
