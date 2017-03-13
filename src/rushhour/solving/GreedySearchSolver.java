package rushhour.solving;

import rushhour.core.*;

import java.util.PriorityQueue;
import java.util.Comparator;

public class GreedySearchSolver extends BoardGraph implements Solver {

	private EdgeComparator comparator;

	protected class EdgeComparator implements Comparator<SearchNode> {

		private Heuristic heuristic;

		protected EdgeComparator(Heuristic heuristic) {
			this.heuristic = heuristic;
		}

		private double value(SearchNode node) {
			return node.depth + this.heuristic.value(node.board);
		}

		public int compare(SearchNode node1, SearchNode node2) {
			double diff = this.value(node1) - this.value(node2);
			if(diff < 0) {
				return -1;
			} else if(diff > 0) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	public GreedySearchSolver(Heuristic heuristic) {
		super();
		this.comparator = new EdgeComparator(heuristic);
	}

	public SolveResult getSolution(Board board) {
		this.clear();
		this.addVertex(board);
		PriorityQueue<SearchNode> queue = new PriorityQueue<SearchNode>(100, this.comparator);
		queue.offer(new SearchNode(this.getVertex(board)));
		int statesVisited = 0;
		while(!queue.isEmpty()) {
			SearchNode current = queue.poll();
			statesVisited++;
			if(current.vertex.board.isSolved()) {
				// construct list from node tree
				return new SolveResult(current.getPath(), current.vertex.board, statesVisited);
			}
			for(Edge edge : current.vertex.expand()) {
				queue.offer(new SearchNode(edge.vertex, current, edge.move));
			}
		}
		return null;
	}
}
