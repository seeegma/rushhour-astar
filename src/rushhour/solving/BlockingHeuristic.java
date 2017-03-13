package rushhour.solving;

import rushhour.core.*;

/**
 * Simplistic heuristic for A*. Mostly for proof of concept. Returns the number
 * of cars blocking the VIP's path to the exit.
 */
public class BlockingHeuristic extends Heuristic {
	public double heuristicValue(Board board) {
		if (board.isSolved()) { return 0; }

		Car vip = board.getCars().get(0);
		int exitHeight = vip.y;
		int numBlocking = 0;
		for (int x = vip.x + vip.length; x < board.getWidth(); x++) {
			if (board.getGrid().get(x, exitHeight) > 0) {
				numBlocking++;
			}
		}

		return numBlocking;
	}
}
