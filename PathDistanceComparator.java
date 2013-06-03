/*
	Andrew Goebel - atggg3@mail.umsl.edu
	Dr. Janikow - Software Engineering - P1b
*/

import java.util.Comparator;
import java.util.List;

public class PathDistanceComparator implements Comparator<List<Node>> {

	public int compare(List<Node> o1, List<Node> o2) {
		if (getRouteDistance(o1) < getRouteDistance(o2)) {
			return -1;
		} else if (getRouteDistance(o1) > getRouteDistance(o2)) {
			return 1;
		} else {
			return 0;
		}
	}

	private double getRouteDistance(List<Node> path) {
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
}
