/*
	Andrew Goebel - atggg3@mail.umsl.edu
	Dr. Janikow - Software Engineering - P1b
*/

public class KEdge implements Comparable<KEdge> {
	String vertexA, vertexB;
	double weight;

	public KEdge(String vertexA, String vertexB, double weight) {
		this.vertexA = vertexA;
		this.vertexB = vertexB;
		this.weight = weight;
	}

	public String getVertexA() {
		return vertexA;
	}

	public String getVertexB() {
		return vertexB;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return "(" + vertexA + ", " + vertexB + ") : Weight = " + weight;
	}

	public int compareTo(KEdge edge) {
		// == is not compared so that duplicate values are not eliminated.
		return (this.weight < edge.weight) ? -1 : 1;
	}

}
