/*
	Andrew Goebel - atggg3@mail.umsl.edu
	Dr. Janikow - Software Engineering - P1b
*/

import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

class KEdges {
	Vector<HashSet<String>> vertexGroups = new Vector<HashSet<String>>();
	TreeSet<KEdge> kEdges = new TreeSet<KEdge>();

	public TreeSet<KEdge> getEdges() {
		return kEdges;
	}

	HashSet<String> getVertexGroup(String vertex) {
		for (HashSet<String> vertexGroup : vertexGroups) {
			if (vertexGroup.contains(vertex)) {
				return vertexGroup;
			}
		}
		return null;
	}

	public void insertEdge(KEdge edge) {
		String vertexA = edge.getVertexA();
		String vertexB = edge.getVertexB();

		HashSet<String> vertexGroupA = getVertexGroup(vertexA);
		HashSet<String> vertexGroupB = getVertexGroup(vertexB);

		if (vertexGroupA == null) {
			kEdges.add(edge);
			if (vertexGroupB == null) {
				HashSet<String> htNewVertexGroup = new HashSet<String>();
				htNewVertexGroup.add(vertexA);
				htNewVertexGroup.add(vertexB);
				vertexGroups.add(htNewVertexGroup);
			} else {
				vertexGroupB.add(vertexA);
			}
		} else {
			if (vertexGroupB == null) {
				vertexGroupA.add(vertexB);
				kEdges.add(edge);
			} else if (vertexGroupA != vertexGroupB) {
				vertexGroupA.addAll(vertexGroupB);
				vertexGroups.remove(vertexGroupB);
				kEdges.add(edge);
			}
		}
	}
}