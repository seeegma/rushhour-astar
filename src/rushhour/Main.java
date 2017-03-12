package rushhour;

import rushhour.Util;
import rushhour.core.*;
import rushhour.io.*;
import rushhour.solving.*;
import rushhour.generation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;
import java.nio.file.Path;
import java.util.Set;
import java.util.HashSet;

public class Main {
	private static final String usage =
		"Usage: java rushhour.Main [OPERATION] [ARGUMENTS]\n" + 
		"Supported operations:\n" +
		"\tprint <puzzle_file>\n" +
		"\tsolve <puzzle_file>\n" +
		"\tevaluate [ --csv | --fields ] <puzzle_file>\n" +
		"\tanalyze [ --csv | --fields ] <puzzle_file> <log_file>\n" +
		ConstraintSatisfier.usage;

	public static void main(String[] args) {
		if(args.length > 1) {
			String operation = args[0];
			String puzzleFile = null;
			if(operation.equals("print")) {
				puzzleFile = args[1];
				Board b = BoardIO.read(puzzleFile);
				System.out.println(b.toString());
			} else if(operation.equals("solve")) {
				Solver solver = null;
				if(args.length == 2) {
					puzzleFile = args[1];
					solver = new EquivalenceClassSolver();
				} else if(args.length == 3) {
					if(args[1].equals("--equiv")) {
						solver = new EquivalenceClassSolver();
					} else if(args[1].equals("--bfs")) {
						solver = new BreadthFirstSearchSolver();
					} else if(args[1].equals("--ids")) {
						solver = new IterativeDeepeningSolver();
					} else {
						System.err.println("unrecognized solver name");
						usage();
					}
					puzzleFile = args[2];
				}
				Board board = BoardIO.read(puzzleFile);
				List<Move> solution = solver.getSolution(board);
				for(Move move : solution) {
					System.out.println(move);
					board.move(move);
				}
				System.err.println(board.isSolved());
				BoardIO.write("out.txt", board);
			} else if(operation.equals("generate")) {
				ConstraintSatisfier csf = new ConstraintSatisfier();
				if(csf.readArgs(args)) {
					csf.satisfy();
				} else {
					usage();
				}
			} else if(operation.equals("check-unique")) {
				if(args.length == 3) {
					List<Path> oldPaths = Util.getFilePaths(args[1]);
					List<Path> newPaths = Util.getFilePaths(args[2]);
					Map<Long,Path> oldHashes = new HashMap<>();
					for(Path path : oldPaths) {
						oldHashes.put(BoardIO.read(path.toAbsolutePath().toString()).getEquivalenceClass().hash(), path);
					}
					boolean allUnique = true;
					for(Path path : newPaths) {
						Long newHash = BoardIO.read(path.toAbsolutePath().toString()).getEquivalenceClass().hash();
						if(oldHashes.containsKey(newHash)) {
							allUnique = false;
							System.out.println(path.toString() + " is in the same equivalence class as " + oldHashes.get(newHash).toString());
						}
					}
					if(allUnique) {
						System.out.println("all unique");
					}
				} else {
					usage();
				}
			} else if(operation.equals("info")) {
				puzzleFile = args[1];
				Board board = BoardIO.read(puzzleFile);
				EquivalenceClass graph = board.getEquivalenceClass();
				System.err.println("graph size: " + graph.size());
				System.err.println("graph depth: " + graph.maxDepth());
				System.err.println("board depth: " + graph.getDepthOfBoard(board));
				System.err.println("graph solutions: " + graph.solutions().size());
			} else if(operation.equals("test")) {
				puzzleFile = args[1];
				Board board = BoardIO.read(puzzleFile);
				System.err.println("finding solutions...");
				SolvedBoardGraph graph = SolvedBoardGraph.create(board);
				if(graph == null) {
					graph = SolvedBoardGraph.create(board.getEquivalenceClass().solutions().iterator().next());
				}
				int toDepth = Integer.parseInt(args[2]);
				System.err.println("propogating to depth " + toDepth + "...");
				graph.propogateDepths(toDepth);
				System.err.println("board depth: " + graph.getDepthOfBoard(board));
				System.err.println("graph depth: " + graph.maxDepth());
				System.err.println("graph size: " + graph.size());
				System.err.println("graph solutions: " + graph.solutions().size());
				System.err.println("farthest depth: " + graph.getDepthOfBoard(graph.getFarthest()));
			} else {
				usage();
			}
		} else {
			usage();
		}
	}

	private static void usage() {
		System.err.println(usage);
		System.exit(1);
	}
}
