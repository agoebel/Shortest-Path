/*
	Andrew Goebel - atggg3@mail.umsl.edu
	Dr. Janikow - Software Engineering - P1b
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

public class Graph {

	// List of all nodes.
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	// To easily locate any Node, all nodes are stored in this HashMap with
	// name as key.
	private HashMap<String, Node> cityMap = new HashMap<String, Node>();
	// List of all edges.
	private ArrayList<Edge> edgeList = new ArrayList<Edge>();

	/**
	 * @return the edgeList
	 */
	public ArrayList<Edge> getEdgeList() {
		return edgeList;
	}

	/**
	 * @param edgeList
	 *            the edgeList to set
	 */
	public void setEdgeList(ArrayList<Edge> edgeList) {
		this.edgeList = edgeList;
	}

	/**
	 * @return the nodeList
	 */
	public ArrayList<Node> getNodeList() {
		return nodeList;
	}

	/**
	 * @param nodeList
	 *            the nodeList to set
	 */
	public void setNodeList(ArrayList<Node> nodeList) {
		this.nodeList = nodeList;
	}

	/**
	 * @return the cityMap
	 */
	public HashMap<String, Node> getCityMap() {
		return cityMap;
	}

	/**
	 * @param cityMap
	 *            the cityMap to set
	 */
	public void setCityMap(HashMap<String, Node> cityMap) {
		this.cityMap = cityMap;
	}

	/**
	 * Constructs the graph from dataFile. Reads the file and creates a node for
	 * every city and edges that connect two cities.
	 *
	 * @param dataFile
	 * @throws Exception
	 */
	public void constructGraph(File dataFile) throws Exception {

		if (!dataFile.exists()) {
			throw new Exception("File " + dataFile.getAbsolutePath()
					+ " does not exist");
		}
		Long numberOfNodes = null;
		BufferedReader input = new BufferedReader(new FileReader(dataFile));
		String line = null;
		try {
			// First line should be number of Nodes.
			line = input.readLine();
			if (line != null && line.trim().length() > 0) {
				line = line.trim();
				try {
					numberOfNodes = Long.parseLong(line);
				} catch (NumberFormatException e) {
					System.out
							.println(dataFile.getAbsolutePath()
									+ " : First Line should be number of Nodes. Please correct the first line of the file.");
					System.exit(-1);
				}
			} else {
				System.out.println(dataFile.getAbsolutePath()
						+ " : First Line is empty. Please input valid file.");
				System.exit(-1);
			}

			// Second line should contain the names of all the nodes.
			line = input.readLine();
			if (line != null && line.trim().length() > 0) {
				line = line.trim();
				// remove all double spaces
				line = line.replaceAll(" +", " ");
				line = line.toUpperCase();
				try {
					String[] cityNames = line.split(" ");
					if (cityNames == null || cityNames.length != numberOfNodes) {
						System.out
								.println(dataFile.getAbsolutePath()
										+ " : Number of Nodes in second line do not match with the number of Nodes in the first line. Please correct the second line of the file.");
						System.exit(-1);
					}
					for (String city : cityNames) {
						if (!ShortestPath.isValidName(city)) {
							System.out.println(dataFile.getAbsolutePath()
									+ " : Invalid name " + city);
							System.out
									.println(dataFile.getAbsolutePath()
											+ " : Node names can contain only letters, digits, '_', '-', or '.'. Please correct the second line of the file.");
							System.exit(-1);
						}
						// city name is ok, create the node.
						Node node = new Node(city);
						nodeList.add(node);
						cityMap.put(city, node);
					}
				} catch (NumberFormatException e) {
					System.out
							.println(dataFile.getAbsolutePath()
									+ " : First Line should be number of Nodes. Please correct the first line of the file.");
					System.exit(-1);
				}
			} else {
				System.out.println(dataFile.getAbsolutePath()
						+ " : First Line is empty. Please input valid file.");
				System.exit(-1);
			}
			int counter = 2;
			// from third line onwards, it should contain all the edges
			while ((line = input.readLine()) != null) {
				counter++;
				if (line != null && line.trim().length() > 0) {
					line = line.trim();
					// remove all double spaces
					line = line.replaceAll(" +", " ");
					line = line.toUpperCase();
					String[] parts = line.split(" ");
					if (parts.length != 3) {
						System.out.println(dataFile.getAbsolutePath()
								+ " : Line " + counter
								+ " contains invalid data.");
						System.exit(-1);
					}
					String fromCity = parts[0];
					String toCity = parts[1];
					String distanceStr = parts[2];
					Node fromNode = cityMap.get(fromCity);
					if (fromNode == null) {
						System.out.println(dataFile.getAbsolutePath()
								+ " : Line " + counter
								+ " contains invalid data. Node " + fromCity
								+ " is not valid name.");
						System.exit(-1);
					}
					Node toNode = cityMap.get(toCity);
					if (toNode == null) {
						System.out.println(dataFile.getAbsolutePath()
								+ " : Line " + counter
								+ " contains invalid data. Node " + toCity
								+ " is not valid name.");
						System.exit(-1);
					}
					Double distance = null;
					try {
						distance = Double.parseDouble(distanceStr);
					} catch (NumberFormatException e) {
						System.out.println(dataFile.getAbsolutePath()
								+ " : Line " + counter
								+ " contains invalid data. Distance "
								+ distanceStr + " is not valid.");
						System.exit(-1);
					}
					Edge edge = new Edge(fromNode, toNode, distance);
					fromNode.getAdjacencies().add(edge);
					edgeList.add(edge);
					Edge edge2 = new Edge(toNode, fromNode, distance);
					toNode.getAdjacencies().add(edge2);
					edgeList.add(edge2);

				}
			}
			// System.out.println("Graph Constructed Successfully");
		} finally {
			input.close();
		}
	}

	/**
	 * Sets the shortest path for every node reaching from source.
	 *
	 * @param source
	 */
	private void computePaths(Node source) {
		source.setMinDistance(0);
		PriorityQueue<Node> pq = new PriorityQueue<Node>();
		pq.add(source);

		while (!pq.isEmpty()) {
			Node u = pq.poll();

			// Visit each Edge exiting u
			for (Edge e : u.getAdjacencies()) {
				Node v = e.getTarget();
				double weight = e.getWeight();
				double distanceThroughU = u.getMinDistance() + weight;
				if (distanceThroughU < v.getMinDistance()) {
					pq.remove(v);
					v.setMinDistance(distanceThroughU);
					v.setPrevious(u);
					pq.add(v);
				}
			}
		}
	}

	/**
	 * Returns the shortest path to target from source.
	 *
	 * @param target
	 * @return
	 */
	private List<Node> getShortestPathTo(Node target) {
		List<Node> path = new ArrayList<Node>();
		for (Node Node = target; Node != null; Node = Node.getPrevious()) {
			path.add(Node);
		}
		Collections.reverse(path);
		return path;
	}

	/**
	 * Finds the shortest path between source and target using Dijkstra's
	 * algorithm
	 *
	 * @param src
	 * @param dst
	 * @return
	 */
	public List<Node> findShortestPath(Node src, Node dst) {
		for (Node node : nodeList) {
			node.setMinDistance(Double.POSITIVE_INFINITY);
			node.setPrevious(null);
		}
		computePaths(src);
		return getShortestPathTo(dst);
	}

	/**
	 * Finds all the paths containing the intermediate nodes using breadth-first
	 * search.
	 *
	 * @param visited
	 * @param target
	 * @param intermediateNodes
	 * @param allPaths
	 * @param maxPath
	 */
	private void findAllPaths(LinkedList<Node> visited, Node target,
			List<Node> intermediateNodes, List<List<Node>> allPaths,
			List<Node> maxPath) {
		Node n = visited.getLast();
		List<Edge> edges = n.getAdjacencies();
		List<Node> nodes = new ArrayList<Node>();
		for (Edge edge : edges) {
			nodes.add(edge.getTarget());
		}
		for (Node node : nodes) {
			if (visited.contains(node)) {
				continue;
			}
			if (node.getName().equalsIgnoreCase(target.getName())) {
				visited.add(node);
				addPath(visited, intermediateNodes, allPaths, maxPath);
				visited.removeLast();
				break;
			}
		}
		for (Node node : nodes) {
			if (visited.contains(node)
					|| (node.getName().equalsIgnoreCase(target.getName()))) {
				continue;
			}
			visited.addLast(node);
			findAllPaths(visited, target, intermediateNodes, allPaths, maxPath);
			visited.removeLast();
		}
	}

	private void findAllPaths3(LinkedList<Node> visited,
			List<List<Node>> allPaths, HashSet<Edge> mstGraph) {
		Node n = visited.getLast();

		List<Edge> edges = n.getAdjacencies();
		List<Node> nodes = new ArrayList<Node>();
		for (Edge edge : edges) {
			for (Edge mstEdge : mstGraph) {
				if ((edge.getSource().getName()
						.equalsIgnoreCase(mstEdge.getSource().getName()) && edge
						.getTarget().getName()
						.equalsIgnoreCase(mstEdge.getTarget().getName()))
						|| (edge.getSource()
								.getName()
								.equalsIgnoreCase(mstEdge.getTarget().getName()) && edge
								.getTarget()
								.getName()
								.equalsIgnoreCase(mstEdge.getSource().getName()))) {
					nodes.add(edge.getTarget());
				}
			}
		}

		for (Node node : nodes) {
			if (visited.contains(node)) {
				continue;
			}
			visited.addLast(node);
			findAllPaths3(visited, allPaths, mstGraph);
			addPath3(visited, allPaths);
			visited.removeLast();
		}
	}

	/**
	 * Adds the path to allPaths if the visited nodes contains all of the
	 * intermediate nodes.
	 *
	 * @param visited
	 * @param intermediateNodes
	 * @param allPaths
	 * @param maxPath
	 */
	private void addPath(LinkedList<Node> visited,
			List<Node> intermediateNodes, List<List<Node>> allPaths,
			List<Node> maxPath) {
		List<Node> path = new ArrayList<Node>();
		for (Node node : visited) {
			path.add(node);
		}
		if (visited.containsAll(intermediateNodes)) {
			allPaths.add(path);
		} else {
			if (maxPath == null || maxPath.size() <= 0) {
				maxPath.clear();
				maxPath.addAll(path);
			} else if (path.size() > maxPath.size()) {
				maxPath.clear();
				maxPath.addAll(path);
			} else if ((path.size() == maxPath.size())
					&& getRouteDistance(path) < getRouteDistance(maxPath)) {
				maxPath.clear();
				maxPath.addAll(path);
			}
		}
	}

	private void addPath3(LinkedList<Node> visited, List<List<Node>> allPaths) {
		List<Node> path = new ArrayList<Node>();
		for (Node node : visited) {
			path.add(node);
		}
		allPaths.add(path);
	}

	/**
	 * Finds the shortest path that contains all the intermediate nodes.
	 *
	 * @param source
	 * @param intermediateNodes
	 * @return
	 */
	public List<Node> findShortestPathByAlgorithm1(Node source,
			List<Node> intermediateNodes) {
		for (Node node : nodeList) {
			node.setMinDistance(Double.POSITIVE_INFINITY);
			node.setPrevious(null);
			node.setSubPaths(new ArrayList<List<Node>>());
		}
		for (Edge edge : edgeList) {
			edge.getSource().setVisited(false);
			edge.getTarget().setVisited(false);
		}
		List<List<Node>> allPaths = new ArrayList<List<Node>>();
		List<Node> maxPath = new ArrayList<Node>();
		for (Node node : intermediateNodes) {
			LinkedList<Node> visited = new LinkedList<Node>();
			visited.add(source);
			findAllPaths(visited, node, intermediateNodes, allPaths, maxPath);
		}
		List<Node> finalPath = null;
		double minRouteDistance = Double.POSITIVE_INFINITY;
		for (List<Node> path : allPaths) {
			double routeDistance = getRouteDistance(path);
			if (routeDistance < minRouteDistance) {
				minRouteDistance = routeDistance;
				finalPath = path;
			}
		}
		List<List<Node>> traversedPaths = new ArrayList<List<Node>>();
		if (finalPath == null) {
			if ((maxPath != null) && (maxPath.size() > 0)) {
				List<Node> untracedNodes = new ArrayList<Node>();
				mergeUntracedNodes(untracedNodes, maxPath, traversedPaths);
				finalPath = getFinalPath(maxPath);
			}
		}
		if (finalPath != null && finalPath.containsAll(intermediateNodes)) {
			return finalPath;
		} else {
			return null;
		}
	}

	private void mergeUntracedNodes(List<Node> untracedNodes,
			List<Node> maxPath, List<List<Node>> traversedPaths) {
		for (Node node : nodeList) {
			if (!maxPath.contains(node)) {
				untracedNodes.add(node);
			}
		}
		if (untracedNodes.size() > 0) {
			for (Node node : untracedNodes) {
				boolean edgeFound = false;
				Node connectingNode = null;
				Edge minEdge = null;
				for (Edge edge : edgeList) {
					edgeFound = false;
					if (edge.getSource().getName()
							.equalsIgnoreCase(node.getName())) {
						for (Node maxPathNode : maxPath) {
							if (edge.getTarget().getName()
									.equalsIgnoreCase(maxPathNode.getName())) {
								edgeFound = true;
								connectingNode = maxPathNode;
								break;
							}
						}
					} else if (edge.getTarget().getName()
							.equalsIgnoreCase(node.getName())) {
						for (Node maxPathNode : maxPath) {
							if (edge.getSource().getName()
									.equalsIgnoreCase(maxPathNode.getName())) {
								edgeFound = true;
								connectingNode = maxPathNode;
								break;
							}
						}
					}
					if (edgeFound) {
						if (minEdge == null) {
							minEdge = edge;
						} else if (edge.getWeight() < minEdge.getWeight()) {
							minEdge = edge;
						}
					}
				}

				if (minEdge != null) {
					List<Node> path = new ArrayList<Node>();
					path.add(node);
					connectingNode.getSubPaths().add(path);
					traversedPaths.add(path);
				}
			}
		}
	}

	/**
	 * Calculates the total distance of entire path.
	 *
	 * @param path
	 * @return
	 */
	public double getRouteDistance(List<Node> path) {
		if (path == null || path.size() <= 0) {
			return Double.POSITIVE_INFINITY;
		}
		Node prevNode = null;
		double routeDistance = 0.0;
		for (Node node : path) {
			if (prevNode == null) {
				prevNode = node;
				continue;
			}
			List<Edge> edges = prevNode.getAdjacencies();
			double minDist = Double.POSITIVE_INFINITY;
			for (Edge edge : edges) {
				if (edge.getTarget().getName().equalsIgnoreCase(node.getName())) {
					if (edge.getWeight() < minDist) {
						minDist = edge.getWeight();
					}
				}
			}
			routeDistance += minDist;
			prevNode = node;
		}
		return routeDistance;
	}

	public List<Node> findShortestPathByAlgorithm2(Node source) {
		for (Node node : nodeList) {
			node.setMinDistance(Double.POSITIVE_INFINITY);
			node.setPrevious(null);
			node.setSubPaths(new ArrayList<List<Node>>());
		}
		for (Edge edge : edgeList) {
			edge.getSource().setVisited(false);
			edge.getTarget().setVisited(false);
		}
		HashSet<Edge> mstGraph = mst();
		List<List<Node>> allPaths = new ArrayList<List<Node>>();
		LinkedList<Node> visited = new LinkedList<Node>();
		visited.add(source);
		findAllPaths3(visited, allPaths, mstGraph);
		Collections.sort(allPaths, new SizeComparator());
		List<List<Node>> allUniquePaths = new ArrayList<List<Node>>();
		sortUniquePaths(allPaths, allUniquePaths);
		// Collections.sort(allUniquePaths, new PathDistanceComparator());
		List<Node> mainPath = new ArrayList<Node>();
		List<List<Node>> traversedPaths = new ArrayList<List<Node>>();
		for (List<Node> path : allUniquePaths) {
			mergePath(path, mainPath, traversedPaths);
			traversedPaths.add(path);
		}
		List<Node> finalPath = getFinalPath(mainPath);
		return finalPath;
	}

	private List<Node> getFinalPath(List<Node> mainPath) {
		List<Node> finalPath = new ArrayList<Node>();
		for (int i = 0; i < mainPath.size(); i++) {
			Node node = mainPath.get(i);
			finalPath.add(node);
			Node nextNode = null;
			if (i < (mainPath.size() - 1)) {
				nextNode = mainPath.get(i + 1);
			}
			Stack<Node> stack = new Stack<Node>();
			stack.push(nextNode);
			addNodeSubPaths(node, stack, finalPath, mainPath);
		}
		finalPath = removeCycles(finalPath);
		finalPath = removeRedundantSingleLegs(finalPath);
		return finalPath;
	}

	public List<Node> removeCycles(List<Node> finalPath) {
		finalPath = removeSuccessiveDuplicates(finalPath);
		boolean cycleFound = false;
		outer: for (int i = 0; i < finalPath.size() - 1; i++) {
			Node n = finalPath.get(i);
			Node next = finalPath.get(i + 1);
			for (int j = i + 2; j < finalPath.size() - 1; j++) {
				Node n1 = finalPath.get(j);
				Node next1 = finalPath.get(j + 1);
				if (n.getName().equalsIgnoreCase(n1.getName())
						&& next.getName().equalsIgnoreCase(next1.getName())) {
					cycleFound = true;
					List<Node> temp = new ArrayList<Node>();
					for (int k = 0; k <= i; k++) {
						temp.add(finalPath.get(k));
					}
					for (int k = j - 1; k > i + 1; k--) {
						temp.add(finalPath.get(k));
					}
					for (int k = j + 1; k < finalPath.size(); k++) {
						temp.add(finalPath.get(k));
					}
					finalPath = temp;
					finalPath = removeSuccessiveDuplicates(finalPath);
					break outer;
				}
			}
		}
		if (cycleFound) {
			finalPath = removeCycles(finalPath);
		}
		cycleFound = false;
		outer1: for (int i = 0; i < finalPath.size() - 1; i++) {
			Node n = finalPath.get(i);
			Node next = finalPath.get(i + 1);
			for (int j = i + 1; j < finalPath.size() - 1; j++) {
				Node n1 = finalPath.get(j);
				Node next1 = finalPath.get(j + 1);
				if (n.getName().equalsIgnoreCase(next1.getName())
						&& next.getName().equalsIgnoreCase(n1.getName())) {
					List<Node> retrace = new ArrayList<Node>();
					retrace.add(n);
					retrace.add(next);
					int t = -1;
					for (int k = i + 2; k <= (i + ((j + 1 - i) / 2)); k++) {
						if (finalPath
								.get(k)
								.getName()
								.equalsIgnoreCase(
										finalPath.get(j - (k - i - 1))
												.getName())) {
							retrace.add(finalPath.get(k));
							t = k;
							continue;
						} else {
							break;
						}
					}
					if (retrace.size() > 0 && t != -1) {
						if (i > 0) {
							for (int l = retrace.size() - 1; l > 0; l--) {
								Node n2 = retrace.get(l);
								Node n0 = finalPath.get(i - 1);
								for (Edge edge : edgeList) {
									if ((edge.getSource().getName()
											.equalsIgnoreCase(n0.getName()) && edge
											.getTarget().getName()
											.equalsIgnoreCase(n2.getName()))
											|| (edge.getTarget()
													.getName()
													.equalsIgnoreCase(
															n0.getName()) && edge
													.getSource()
													.getName()
													.equalsIgnoreCase(
															n2.getName()))) {
										List<Node> temp = new ArrayList<Node>();
										for (int m = 0; m < i; m++) {
											temp.add(finalPath.get(m));
										}
										for (int m = l; m < retrace.size(); m++) {
											temp.add(retrace.get(m));
										}
										for (int m = t + 1; m < finalPath
												.size(); m++) {
											temp.add(finalPath.get(m));
										}
										finalPath = temp;
										finalPath = removeSuccessiveDuplicates(finalPath);
										cycleFound = true;
										break outer1;
									}
								}
							}
						}
					}
				}
			}
		}
		if (cycleFound) {
			finalPath = removeCycles(finalPath);
		}
		return finalPath;
	}

	public List<Node> removeSuccessiveDuplicates(List<Node> finalPath) {
		boolean flag = true;
		while (flag) {
			flag = false;
			for (int i = 0; i < finalPath.size() - 1; i++) {
				if (finalPath.get(i).getName()
						.equalsIgnoreCase(finalPath.get(i + 1).getName())) {
					finalPath.remove(i + 1);
					flag = true;
					break;
				}
			}
		}
		return finalPath;
	}

	private List<Node> removeRedundantSingleLegs(List<Node> finalPath) {
		boolean redundantLegFound = false;
		for (int i = 0; i < finalPath.size() - 1; i++) {
			if (finalPath.get(i).getName()
					.equalsIgnoreCase(finalPath.get(i + 1).getName())) {
				finalPath.remove(i + 1);
			}
		}
		for (int i = 0; i < (finalPath.size() - 2); i++) {
			Node firstNode = finalPath.get(i);
			Node secondNode = finalPath.get(i + 1);
			Node thirdNode = finalPath.get(i + 2);
			if (firstNode.getName().equalsIgnoreCase(thirdNode.getName())) {
				for (int j = 0; j < i; j++) {
					if (finalPath.get(j).getName()
							.equalsIgnoreCase(secondNode.getName())) {
						redundantLegFound = true;
						break;
					}
				}
				if (!redundantLegFound) {
					for (int k = i + 2; k < finalPath.size(); k++) {
						if (finalPath.get(k).getName()
								.equalsIgnoreCase(secondNode.getName())) {
							redundantLegFound = true;
							break;
						}
					}
				}
				if (redundantLegFound) {
					finalPath.remove(i + 1);
					finalPath.remove(i + 1);
					break;
				}
			}
		}
		if (redundantLegFound) {
			finalPath = removeRedundantSingleLegs(finalPath);
		}
		return finalPath;
	}

	private void addNodeSubPaths(Node node, Stack<Node> stack,
			List<Node> finalPath, List<Node> mainPath) {
		if (node.getSubPaths().size() > 0) {
			List<List<Node>> subPaths = node.getSubPaths();
			for (List<Node> subPath : subPaths) {
				subPath.add(0, node);
			}
			Collections.sort(subPaths, new PathDistanceComparator());
			for (List<Node> subPath : subPaths) {
				subPath.remove(0);
			}
			for (int j = 0; j < subPaths.size(); j++) {
				List<Node> subPath = subPaths.get(j);
				if (mainPath.containsAll(subPath)
						|| finalPath.containsAll(subPath)) {
					continue;
				}
				if (j < subPaths.size() - 1) {
					stack.push(subPaths.get(j + 1).get(0));
				}
				for (int i = 0; i < subPath.size(); i++) {
					Node subNode = subPath.get(i);
					finalPath.add(subNode);
					if (subNode.getSubPaths().size() > 0) {
						if (i < (subPath.size() - 1)) {
							stack.push(subPath.get(i + 1));
						}
						addNodeSubPaths(subNode, stack, finalPath, mainPath);
					}
				}
				List<Node> rtnShortestPath = new ArrayList<Node>();
				if (stack != null) {
					rtnShortestPath = findShortestPath(
							subPath.get(subPath.size() - 1), stack.pop());
				}
				boolean first = true;
				for (Node nodeRtn : rtnShortestPath) {
					if (first) {
						first = false;
						continue;
					}
					finalPath.add(nodeRtn);
				}

			}
		}
	}

	private void mergePath(List<Node> path, List<Node> mainPath,
			List<List<Node>> traversedPaths) {
		if (mainPath.size() <= 0) {
			mainPath.addAll(path);
		} else {
			List<Node> pathToMergeWith = null;
			int maxNoMatchFoundAt = -1;
			for (List<Node> uniquePath : traversedPaths) {
				for (int i = 0; i < path.size(); i++) {
					if (path.get(i).equals(uniquePath.get(i))) {
						continue;
					} else {
						if (i > maxNoMatchFoundAt) {
							maxNoMatchFoundAt = i;
							pathToMergeWith = uniquePath;
						}
						break;
					}
				}
			}
			if (pathToMergeWith != null) {
				Node node = pathToMergeWith.get(maxNoMatchFoundAt - 1);
				List<Node> subPath = new ArrayList<Node>();
				for (int i = maxNoMatchFoundAt; i < path.size(); i++) {
					subPath.add(path.get(i));
				}
				if (subPath.size() > 0) {
					node.getSubPaths().add(subPath);
				}
			}
		}
	}

	private void sortUniquePaths(List<List<Node>> allPaths,
			List<List<Node>> allUniquePaths) {
		for (List<Node> path : allPaths) {
			if (!allNodesOfThisPathExistInUniquePaths(path, allUniquePaths)) {
				allUniquePaths.add(path);
			}
		}
	}

	private boolean allNodesOfThisPathExistInUniquePaths(List<Node> path,
			List<List<Node>> allUniquePaths) {
		boolean pathExist = false;
		for (List<Node> uniquePath : allUniquePaths) {
			if (uniquePath.containsAll(path)) {
				pathExist = true;
				break;
			}
		}
		return pathExist;
	}

	private HashSet<Edge> mst() {
		HashSet<Edge> mst = new HashSet<Edge>();
		KEdges vv = new KEdges();
		ArrayList<Edge> edgeListTemp = new ArrayList<Edge>();
		for (Edge edge : edgeList) {
			boolean edgeAlreadyPresent = false;
			for (int i = 0; i < edgeListTemp.size(); i++) {
				Edge tempEdge = edgeListTemp.get(i);
				if ((tempEdge.getSource().getName()
						.equalsIgnoreCase(edge.getSource().getName()) && tempEdge
						.getTarget().getName()
						.equalsIgnoreCase(edge.getTarget().getName()))
						|| (tempEdge.getTarget().getName()
								.equalsIgnoreCase(edge.getSource().getName()) && tempEdge
								.getSource().getName()
								.equalsIgnoreCase(edge.getTarget().getName()))) {
					if (edge.getWeight() < tempEdge.getWeight()) {
						edgeListTemp.remove(i);
						edgeListTemp.add(edge);
					}
					edgeAlreadyPresent = true;
					break;
				}
			}
			if (!edgeAlreadyPresent) {
				edgeListTemp.add(edge);
			}
		}
		for (Edge edge : edgeListTemp) {
			KEdge kedge = new KEdge(edge.getSource().getName(), edge
					.getTarget().getName(), edge.getWeight());
			vv.insertEdge(kedge);
		}
		for (KEdge edge : vv.getEdges()) {
			mst.add(getEdgeFromKEdge(edge, edgeListTemp));
		}
		// System.out.println("Total weight is " + total);
		return mst;
	}

	private Edge getEdgeFromKEdge(KEdge kedge, ArrayList<Edge> edgeListTemp) {
		Edge rtnEdge = null;
		for (Edge edge : edgeListTemp) {
			if (edge.getSource().getName().equalsIgnoreCase(kedge.getVertexA())
					&& edge.getTarget().getName()
							.equalsIgnoreCase(kedge.getVertexB())) {
				rtnEdge = edge;
				break;
			}
		}
		return rtnEdge;
	}

	public List<Node> findShortestPathByAlgorithm3(Node source) {

		List<Node> localNodeList = new ArrayList<Node>();
		Map<String, Node> localCityMap = new HashMap<String, Node>();
		List<Edge> localEdgeList = new ArrayList<Edge>();
		for (Node node : nodeList) {
			Node localNode = new Node(node.getName());
			localNode.setVisited(false);
			localNodeList.add(localNode);
			localCityMap.put(localNode.getName(), localNode);
		}
		for (Edge edge : edgeList) {
			Node srcNode = edge.getSource();
			Node targetNode = edge.getTarget();
			double weight = edge.getWeight();
			Node localSrcNode = localCityMap.get(srcNode.getName());
			Node localTargetNode = localCityMap.get(targetNode.getName());
			Edge localEdge = new Edge(localSrcNode, localTargetNode, weight);
			localSrcNode.getAdjacencies().add(localEdge);
			localEdgeList.add(localEdge);
		}
		List<List<Node>> paths = new ArrayList<List<Node>>();
		Node startNode = localCityMap.get(source.getName());
		List<Edge> adjList = new ArrayList<Edge>();

		for (Edge edge : startNode.getAdjacencies()) {
			adjList.add(edge);
		}
		List<Node> currentPath = new ArrayList<Node>();
		paths.add(currentPath);
		tracePath(startNode, currentPath, paths, adjList);
		List<Node> mergedPath = mergePaths(paths);
		mergedPath = getFinalPath(mergedPath);
		return mergedPath;
	}

	private List<Node> mergePaths(List<List<Node>> paths) {
		List<Node> mergedPath = null;
		if (paths.size() == 1) {
			mergedPath = paths.get(0);
		} else {
			while (paths.size() > 1) {
				List<Node> pathToMerge = paths.get(paths.size() - 1);
				Node startNode = pathToMerge.get(0);
				List<Node> pathToMergeWith = null;
				int idx = -1;
				outer: for (int i = paths.size() - 2; i >= 0; i--) {
					pathToMergeWith = paths.get(i);
					for (Node node : pathToMergeWith) {
						if (node.getName()
								.equalsIgnoreCase(startNode.getName())) {
							paths.remove(paths.size() - 1);
							idx = i;
							break outer;
						}
					}
				}
				if (pathToMerge != null && pathToMergeWith != null
						&& startNode != null) {
					pathToMergeWith = mergeTwoPaths(pathToMerge,
							pathToMergeWith);
					paths.add(idx, pathToMergeWith);
					paths.remove(idx + 1);
				}
			}
			mergedPath = paths.get(0);
		}
		if (mergedPath != null && mergedPath.size() > 0) {
			List<Node> temp1 = new ArrayList<Node>();
			for (int i = 0; i < mergedPath.size(); i++) {
				temp1.add(getOriginalNode(mergedPath.get(i)));
			}
			mergedPath = temp1;
		}
		return mergedPath;
	}

	private List<Node> mergeTwoPaths(List<Node> pathToMerge,
			List<Node> pathToMergeWith) {
		List<List<Node>> allPaths = new ArrayList<List<Node>>();
		Node startNode = pathToMerge.get(0);
		Node lastNodeOnPathToMergeWith = pathToMergeWith.get(pathToMergeWith
				.size() - 1);
		Node nextNodeOnPathToMerge = pathToMerge.get(1);
		Node lastNodeOnPathToMerge = pathToMerge.get(pathToMerge.size() - 1);
		int startNodeIndexOnPathToMergeWith = -1;
		for (int j = 0; j < pathToMergeWith.size(); j++) {
			Node node = pathToMergeWith.get(j);
			if (node.getName().equalsIgnoreCase(startNode.getName())) {
				startNodeIndexOnPathToMergeWith = j;
				break;
			}
		}
		Node nextNodeOnPathToMergeWith = pathToMergeWith
				.get(startNodeIndexOnPathToMergeWith + 1);

		List<Node> path1 = new ArrayList<Node>();
		for (int k = startNodeIndexOnPathToMergeWith; k < pathToMergeWith
				.size(); k++) {
			path1.add(getOriginalNode(pathToMergeWith.get(k)));
		}
		for (int k = pathToMergeWith.size() - 2; k >= startNodeIndexOnPathToMergeWith; k--) {
			path1.add(getOriginalNode(pathToMergeWith.get(k)));
		}
		for (int k = 1; k < pathToMerge.size(); k++) {
			path1.add(getOriginalNode(pathToMerge.get(k)));
		}
		List<Node> path2 = new ArrayList<Node>();
		for (int k = 0; k < pathToMerge.size(); k++) {
			path2.add(getOriginalNode(pathToMerge.get(k)));
		}
		for (int k = pathToMerge.size() - 2; k >= 0; k--) {
			path2.add(getOriginalNode(pathToMerge.get(k)));
		}
		for (int k = startNodeIndexOnPathToMergeWith + 1; k < pathToMergeWith
				.size(); k++) {
			path2.add(getOriginalNode(pathToMergeWith.get(k)));
		}
		List<Node> path3 = new ArrayList<Node>();
		for (int k = startNodeIndexOnPathToMergeWith; k < pathToMergeWith
				.size() - 1; k++) {
			path3.add(getOriginalNode(pathToMergeWith.get(k)));
		}
		Node fromNode = cityMap.get(lastNodeOnPathToMergeWith.getName());
		Node toNode = cityMap.get(nextNodeOnPathToMerge.getName());
		List<Node> temp = findShortestPath(fromNode, toNode);
		path3.addAll(temp);
		for (int k = 2; k < pathToMerge.size(); k++) {
			path3.add(getOriginalNode(pathToMerge.get(k)));
		}
		List<Node> path4 = new ArrayList<Node>();
		for (int k = 0; k < pathToMerge.size() - 1; k++) {
			path4.add(getOriginalNode(pathToMerge.get(k)));
		}
		fromNode = cityMap.get(lastNodeOnPathToMerge.getName());
		toNode = cityMap.get(nextNodeOnPathToMergeWith.getName());
		temp = findShortestPath(fromNode, toNode);
		path4.addAll(temp);
		for (int k = startNodeIndexOnPathToMergeWith + 2; k < pathToMergeWith
				.size(); k++) {
			path4.add(pathToMergeWith.get(k));
		}
		allPaths.add(path1);
		allPaths.add(path2);
		allPaths.add(path3);
		allPaths.add(path4);
		Collections.sort(allPaths, new PathDistanceComparator());
		List<Node> mergedPath = new ArrayList<Node>();
		for (int k = 0; k < startNodeIndexOnPathToMergeWith; k++) {
			mergedPath.add(pathToMergeWith.get(k));
		}
		mergedPath.addAll(allPaths.get(0));
		return mergedPath;
	}

	private Node getOriginalNode(Node node) {
		return cityMap.get(node.getName());
	}

	private void tracePath(Node startNode, List<Node> currentPath,
			List<List<Node>> paths, List<Edge> adjList) {
		currentPath.add(startNode);
		while (!adjList.isEmpty()) {
			Edge edge = adjList.get(0);
			adjList.remove(0);
			Collections.sort(startNode.getAdjacencies());
			removeUnwantedEdges(startNode, paths);
			List<Edge> adjacencies = startNode.getAdjacencies();
			if (adjacencies.contains(edge)) {
				Node nextNodeToVisit = edge.getTarget();
				adjacencies.remove(edge);
				List<Edge> adjList1 = new ArrayList<Edge>();
				for (Edge e : nextNodeToVisit.getAdjacencies()) {
					adjList1.add(e);
				}
				if (currentPath == null
						|| currentPath.size() <= 0
						|| !(currentPath.get(currentPath.size() - 1).getName()
								.equalsIgnoreCase(startNode.getName()))) {
					currentPath = new ArrayList<Node>();
					currentPath.add(startNode);
					paths.add(currentPath);
				}
				tracePath(nextNodeToVisit, currentPath, paths, adjList1);
			}
		}
		currentPath = new ArrayList<Node>();
	}

	private void removeUnwantedEdges(Node startNode, List<List<Node>> paths) {
		List<Edge> adjacencies = startNode.getAdjacencies();
		List<Edge> adjacenciesToRemove = new ArrayList<Edge>();
		if (adjacencies != null && adjacencies.size() > 0) {
			for (int i = 0; i < adjacencies.size(); i++) {
				Edge edge = adjacencies.get(i);
				for (List<Node> path : paths) {
					for (Node node : path) {
						if (node.getName().equalsIgnoreCase(
								edge.getTarget().getName())) {
							adjacenciesToRemove.add(edge);
						}
					}
				}
			}
		}
		if (adjacenciesToRemove.size() > 0) {
			for (Edge edge : adjacenciesToRemove) {
				adjacencies.remove(edge);
			}
		}
	}
}
