import java.util.*;



public class BFSGraphLib<V,E> {


    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
        //finds the shortest routes from start to every vertex
        //BFS to find shortest path tree for a current center of the universe. Return a path tree as a Graph.

        //bfs code reformatted from GraphTraversal class
        Graph<V,E> backTrack = new AdjacencyMapGraph<>(); //initialize backTrack
        backTrack.insertVertex(source); //load start vertex with null parent
        Queue<V> queue = new LinkedList<V>(); //queue to implement BFS

        queue.add(source); //enqueue start vertex

        while (!queue.isEmpty()) { //loop until no more vertices
            V u = queue.remove(); //dequeue

            for (V v : g.outNeighbors(u)) { //loop over out neighbors
                if (!backTrack.hasVertex(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
                    queue.add(v); //enqueue neighbor
                    backTrack.insertVertex(v); //save that this vertex was discovered from prior vertex
                    backTrack.insertDirected(v, u, null); //edges are directed but empty rn
                }
            }
        }
        return backTrack;
    }


    public static <V,E> List<V> getPath(Graph<V,E> tree, V v){ //plug backtrack into getPath
        //Given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe.

        //edgecases
        if (!tree.hasVertex(v) || (tree.numVertices() == 0)){
            return new ArrayList<>(); // tree doesn't have the vertex
        }


        List<V> path = new LinkedList<V>(); //this will hold the path from start to end vertex
        V current = v; //start at end vertex
        path.add(0,current);

        //loop from end vertex back to start vertex
        while (tree.outDegree(current)!= 0) {

            //checks all the edges; when current = null you end up at the end
            for (V neighbor : tree.outNeighbors(current)) {
                current = neighbor;
                //get vertex that discovered this vertex
            }
            path.add(0,current);
        }
        return path;
    }


    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
        //Given a graph and a subgraph (here shortest path tree), determine which vertices are in the graph but not the subgraph (here, not reached by BFS).
        // go thru each vertex
        Set<V> missingVertex = new HashSet<V>();

        //go thru all vertices in graph
        for (V v : graph.vertices()){

            //if not present in subgraph add
            if (!subgraph.hasVertex(v)){
                missingVertex.add(v);
            }
        }
        return missingVertex;
    }


    public static <V,E> double averageSeparation(Graph<V,E> tree, V root){ //doublecheck this
        //Find the average distance-from-root in a shortest path tree.
        // Note: do this without enumerating all the paths! Hint: think tree recursion...
        double result = helperaverageSeparation(tree, root, 0)/(double)(tree.numVertices());
        return result;
    }

    public static <V,E> double helperaverageSeparation(Graph<V,E> tree, V root, int sum){
        int total = sum;
        for (V v: tree.inNeighbors(root)) {
            total = total + 1;
            averageSeparation(tree, v);
        }
        return total;
    }}
