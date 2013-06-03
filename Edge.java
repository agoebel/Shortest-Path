/*
	Andrew Goebel - atggg3@mail.umsl.edu
	Dr. Janikow - Software Engineering - P1b
*/

public class Edge implements Comparable<Edge> {

	private Node source;
	// target node to which this edge is connected to.
	private Node target;
	// distance between source and target
	private double weight;

	/**
	 * @return the source
	 */
	public Node getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(Node source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public Node getTarget() {
		return target;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	public Edge(Node argSource, Node argTarget, double argWeight) {
		source = argSource;
		target = argTarget;
		weight = argWeight;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Edge o) {
		return Double.compare(weight, o.getWeight());
	}

	public void setNodesVisited() {
		source.setVisited(true);
		target.setVisited(true);
	}

	public boolean areNodesVisited() {
		return (source.isVisited() & target.isVisited());
	}

	public String toString() {
		String str = source.toString() + "->" + target.toString() + ":"
				+ weight;
		return str;
	}
}
