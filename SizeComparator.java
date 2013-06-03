/*
	Andrew Goebel - atggg3@mail.umsl.edu
	Dr. Janikow - Software Engineering - P1b
*/

import java.util.Comparator;
import java.util.List;

public class SizeComparator implements Comparator<List<Node>> {

	public int compare(List<Node> o1, List<Node> o2) {
		if (o1.size() > o2.size()) {
			return -1;
		} else if (o1.size() < o2.size()) {
			return 1;
		} else {
			PathDistanceComparator pdComparator = new PathDistanceComparator();
			return ((pdComparator.compare(o1, o2)) * (-1));
		}
	}

}
