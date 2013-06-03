/*
	Andrew Goebel - atggg3@mail.umsl.edu
	Dr. Janikow - Software Engineering - P1b
*/

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node>, Cloneable {

	// name of the node
	private final String name;
	// list of adjacencies
	private List<Edge> adjacencies = new ArrayList<Edge>();
	// minimum distance from the source
	private double minDistance = Double.POSITIVE_INFINITY;
	// previous node used while calculating the shortest path
	private Node previous;

	private boolean visited;

	private List<List<Node>> subPaths = new ArrayList<List<Node>>();

	/**
	 * @return the subPaths
	 */
	public List<List<Node>> getSubPaths() {
		return subPaths;
	}

	/**
	 * @param subPaths
	 *            the subPaths to set
	 */
	public void setSubPaths(List<List<Node>> subPaths) {
		this.subPaths = subPaths;
	}

	/**
	 * @return the visited
	 */
	public boolean isVisited() {
		return visited;
	}

	/**
	 * @param visited
	 *            the visited to set
	 */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/**
	 * @return the adjacencies
	 */
	public List<Edge> getAdjacencies() {
		return adjacencies;
	}

	/**
	 * @param adjacencies
	 *            the adjacencies to set
	 */
	public void setAdjacencies(List<Edge> adjacencies) {
		this.adjacencies = adjacencies;
	}

	/**
	 * @return the minDistance
	 */
	public double getMinDistance() {
		return minDistance;
	}

	/**
	 * @param minDistance
	 *            the minDistance to set
	 */
	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}

	/**
	 * @return the previous
	 */
	public Node getPrevious() {
		return previous;
	}

	/**
	 * @param previous
	 *            the previous to set
	 */
	public void setPrevious(Node previous) {
		this.previous = previous;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Node(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Node other) {
		return Double.compare(minDistance, other.getMinDistance());
	}

}
