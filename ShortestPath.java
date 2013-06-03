/*
	Andrew Goebel - atggg3@mail.umsl.edu
	Dr. Janikow - Software Engineering - P1b
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShortestPath {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args == null || args.length < 1) {
			System.out.println("Usage: run datafile [problemfile]");
			System.exit(-1);
		}

		String dataFileStr = "";
		String problemFileStr = "";
		File dataFile = null;
		File problemFile = null;
		dataFileStr = args[0];
		if (args.length > 1) {
			problemFileStr = args[1];
		}
		dataFile = new File(dataFileStr);
		if (dataFile == null || !dataFile.exists()) {
			System.out
					.println("datafile does not exist. Please enter valid datafile.");
			System.exit(-1);
		}
		if (problemFileStr != null && problemFileStr.trim().length() > 0) {
			problemFile = new File(problemFileStr);
			if (problemFile == null || !problemFile.exists()) {
				System.out
						.println("problemfile does not exist. Please enter valid problemfile.");
				System.exit(-1);
			}
		}
		Graph graph = new Graph();
		graph.constructGraph(dataFile);
		for (Node node : graph.getNodeList()) {
			Collections.sort(node.getAdjacencies());
		}
		Collections.sort(graph.getEdgeList());
		if (problemFile != null) {
			// solve the problems one by one from the problemfile.
			if (!problemFile.exists()) {
				throw new Exception("File " + problemFile.getAbsolutePath()
						+ " does not exist");
			}
			BufferedReader input = new BufferedReader(new FileReader(
					problemFile));
			String line = null;
			int counter = 0;
			while ((line = input.readLine()) != null) {
				counter++;
				processInput(line, counter, graph, problemFile);
			}
			input.close();
		} else {
			// Ask user to input the problem
			boolean continueLoop = true;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			while (continueLoop) {
				try {
					System.out.println("Enter the problem (quit to exit):");
					String line = in.readLine();
					if (line != null && !line.trim().equalsIgnoreCase("quit")) {
						processInput(line, 1, graph, null);
					} else {
						continueLoop = false;
						continue;
					}
				} catch (IOException e) {
					System.out
							.println("IOException occured while reading input from console");
					e.printStackTrace();
					continueLoop = false;
				}
			}
			in.close();
		}
	}

	/**
	 * Process the input based on the problem case and display the output.
	 *
	 * @param line
	 * @param counter
	 * @param graph
	 * @param problemFile
	 */
	private static void processInput(String line, int counter, Graph graph,
			File problemFile) {
		line = line.trim();
		try {
			if (line.length() > 0) {
				// remove all double spaces
				line = line.replaceAll(" +", " ");
				line = line.toUpperCase();
				String[] parts = line.split(" ");
				if (parts == null || parts.length < 2) {
					printInputErrorMessage(problemFile, counter);
					System.exit(-1);
				}
				int problem = Integer.parseInt(parts[0]);
				switch (problem) {
				case 1:
					findShortestPathBetweenTwoCities(line, parts, graph,
							problemFile, counter);
					break;
				case 2:
					findShortestPathContainingCities(line, parts, graph,
							problemFile, counter);
					break;
				case 3:
					findShortestPathToAllCities(line, parts, graph,
							problemFile, counter);
					break;
				default:
					printInputErrorMessage(problemFile, counter);
					System.exit(-1);
					break;
				}
			}
		} catch (NumberFormatException e) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}

	}

	/**
	 * Problem case# 3. It calculates the shortest path from source to all other
	 * nodes.
	 *
	 * @param line
	 * @param parts
	 * @param graph
	 * @param counter
	 */
	private static void findShortestPathToAllCities(String line,
			String[] parts, Graph graph, File problemFile, int counter) {
		if (parts.length != 2) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		String fromCity = parts[1];
		if (!isValidName(fromCity)) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		Node source = graph.getCityMap().get(fromCity);
		if (source == null) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		System.out.println("Answer to problem " + line);
		List<Node> intermediateNodes = new ArrayList<Node>();
		for (Node node : graph.getNodeList()) {
			if (!node.getName().equalsIgnoreCase(source.getName())) {
				intermediateNodes.add(node);
			}
		}
		List<Node> path1 = null;
		if (graph.getNodeList().size() <= 20) {
			path1 = graph.findShortestPathByAlgorithm1(source,
					intermediateNodes);
		}
		List<Node> path2 = graph.findShortestPathByAlgorithm2(source);
		List<Node> path3 = null;
		path3 = graph.findShortestPathByAlgorithm3(source);
		List<Node> shortestPath = null;
		double shortestPathDistance = Double.POSITIVE_INFINITY;
		if(path1!=null){
			double path1Distance = graph.getRouteDistance(path1);
			shortestPath = path1;
			shortestPathDistance = path1Distance;
		}
		if(path2!=null){
			double path2Distance = graph.getRouteDistance(path2);
			if(path2Distance<shortestPathDistance){
				shortestPath = path2;
				shortestPathDistance = path2Distance;
			}
		}
		if(path3!=null){
			double path3Distance = graph.getRouteDistance(path3);
			if(path3Distance<shortestPathDistance){
				shortestPath = path3;
				shortestPathDistance = path3Distance;
			}
		}
		System.out.print(graph.getRouteDistance(shortestPath));
		printPath(shortestPath);
	}

	/**
	 * Problem case# 2. It calculates the shortest path that contains the
	 * intermediate nodes.
	 *
	 * @param line
	 * @param parts
	 * @param graph
	 * @param counter
	 */
	private static void findShortestPathContainingCities(String line,
			String[] parts, Graph graph, File problemFile, int counter) {
		if (parts.length < 4) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		String fromCity = parts[1];
		if (!isValidName(fromCity)) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		Node source = graph.getCityMap().get(fromCity);
		if (source == null) {
			printInputErrorMessage(problemFile, counter);
		}
		List<Node> intermediateNodes = new ArrayList<Node>();
		for (int i = 2; i < parts.length; i++) {
			String part = parts[i];
			if (!isValidName(part)) {
				printInputErrorMessage(problemFile, counter);
				System.exit(-1);
			}
			Node node = graph.getCityMap().get(part);
			if (node == null) {
				printInputErrorMessage(problemFile, counter);
				System.exit(-1);
			}
			intermediateNodes.add(node);
		}
		System.out.println("Answer to problem " + line);
		List<Node> path = graph.findShortestPathByAlgorithm1(
				source, intermediateNodes);
		if (path != null && path.size() > 0) {
			System.out.print(graph.getRouteDistance(path));
			printPath(path);
		} else {
			System.out.println("Path does not exist");
		}
	}

	/**
	 * Problem case# 1. It finds the shortest path between two nodes.
	 *
	 * @param line
	 * @param parts
	 * @param graph
	 * @param counter
	 */
	private static void findShortestPathBetweenTwoCities(String line,
			String[] parts, Graph graph, File problemFile, int counter) {
		if (parts.length != 3) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		String fromCity = parts[1];
		String toCity = parts[2];
		if (!isValidName(fromCity)) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		if (!isValidName(toCity)) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		Node source = graph.getCityMap().get(fromCity);
		Node destination = graph.getCityMap().get(toCity);
		if (source == null || destination == null) {
			printInputErrorMessage(problemFile, counter);
			System.exit(-1);
		}
		List<Node> path = graph.findShortestPath(source, destination);
		System.out.println("Answer to problem " + line);
		System.out.print(destination.getMinDistance());
		printPath(path);
	}

	/**
	 * Prints the path as per the output format.
	 *
	 * @param path
	 */
	private static void printPath(List<Node> path) {
		if (path != null && path.size() > 0) {
			Node prevNode = null;
			for (Node node : path) {
				if (prevNode == null) {
					prevNode = node;
					System.out.print(" " + prevNode.getName());
					continue;
				}
				List<Edge> edges = prevNode.getAdjacencies();
				double minDist = Double.POSITIVE_INFINITY;
				for (Edge edge : edges) {
					if (edge.getTarget().getName()
							.equalsIgnoreCase(node.getName())) {
						if (edge.getWeight() < minDist) {
							minDist = edge.getWeight();
						}
					}
				}
				System.out.print(" " + minDist + " " + node.getName());
				prevNode = node;
			}
			System.out.println("");
		} else {
			System.out.println("Path does not exist");
		}
	}

	/**
	 * Check if the name is valid or not.
	 *
	 * @param name
	 * @return
	 */
	public static boolean isValidName(String name) {
		Pattern pattern1 = Pattern.compile("[^a-zA-Z0-9\\-\\._]");
		Matcher matcher1 = pattern1.matcher(name);
		if (matcher1.find()) {
			return false;
		}
		return true;
	}

	public static void printInputErrorMessage(File problemFile, int counter) {
		if (problemFile != null) {
			System.out.println(problemFile.getAbsolutePath()
					+ " contains invalid data at line " + counter);
		} else {
			System.out.println("Input contains invalid data");
		}
	}
}
