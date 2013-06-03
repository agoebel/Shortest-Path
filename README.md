Shortest-Path
=============

Execution
==> run datafile [problemfile]


Example Datafile:
3  // number of nodes
STL chicago NY  // list of node names
STL chicago 290 // node-to-node distance b/w nodes
NY chicago 780
STL chicago 300
chicago NY 895


Example Problemfile:
1 STL chicago\n
3 STL\n


Problem Types:
1: there are two strings and the question is to find the shortest path from the first to the second (and not having to come back)
2: there are >2 strings and the problem is to find shortest path starting from the first string while visiting all the other strings (in any order, possibly visiting other nodes) and not having to come back
3: only one string, and the problem is to find shortest path starting from this node and visiting all others, not having to come back


Implementation:

Shortest Path Methods

Structures:
Node: This class represents the Node and holds the data for Node name.
Edge: This class represents the connection between two Nodes.
Graph: This is list of all the Nodes and Edges.
KEdge: Used for finding the Minimum Spanning Tree
KEdges: collection of all Minimum Spanning Tree edges.
PathDistanceComparator: This is a standard Java Comparator class to compare the TotalDistance of the path
ShortestPath: This is main class. It accepts the user input constructs the graph and calls the respective functions for respective problem.
SizeComparator: This class is similar to PathDistanceComparator but instead of comparing the distance it compares the number of nodes in the path.

Algorithms and methods:
Problem #1: The program uses the standard Dijkstra's algorithm. Graph. graph.findShortestPath(source, destination) is called to find the shortest path between the source and destination nodes. For the details of the Dijkstra's algorithm please refer to http://en.wikipedia.org/wiki/Dijkstra's_algorithm.

Problem #2: The program tries to find the paths from the source node to each of the nodes supplied in the problem statement. graph.findShortestPathByAlgorithm1(source, intermediateNodes). If it finds more than one path going through all the intermediate nodes then it takes the shortest path out of all.

Problem #3: The program uses 3 algorithms
    (1) The same algorith as of problem#2 passing all the nodes except source as intermediate nodes and tries to find the shortest path.
    (2) Using Minimal Spanning Tree (graph.findShortestPathByAlgorithm2) - program creates the minimal spanning tree out of the graph and the traverses the minimum spanning tree from source node to all the nodes. For the details of Minimal Spanning Tree algorithm please refer to http://en.wikipedia.org/wiki/Kruskal's_algorithm.
    (3) Traversing all the nodes sequentially (graph.findShortestPathByAlgorithm3) - This traverses the graph in depth first manner and then merges all the paths into one path. It removes any unwanted edges which forms the cycle or any duplicate retraced paths.
After calculating three paths whichever path is shortest is considered as the shortest path.
