package airRoutes.utils.common;

import airRoutes.utils.Airport;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * This Class represents an adjacency list implementation of a graph.
 * It links multiple airports together making use of a HashMap and Edges.
 * Vertex can be linked directionally as well as undirectionally.
 *
 * @author David Abderhalden / Joris Hänseler
 * @version 0.1
 * @since 30.10.2021
 */
public class AirNavigationGraph {
    //private Map<Airport, LinkedList<Edge<Airport, Integer>>> graph;
    private Map<Airport, Map<Airport, Integer>> graph;

    /**
     * constructor
    */
    public AirNavigationGraph(){
        this.graph = new HashMap<>();
    }

    /**
     * Adds an airport as a new vertex to the graph
     * @param vertex the specific airport
     */
    public void addVertex(Airport vertex){
        this.graph.putIfAbsent(vertex, new HashMap<>());
    }

    /**
     * Removes an airport as a vertex from the graph
     * @param vertex the specific airport
     */
    public void removeVertex(Airport vertex){
        this.graph.remove(vertex);
        this.graph
                .forEach((k, v) -> v.remove(vertex)
                );
    }

    /**
     * Adds a new edge to the graph
     * @param firstVertex the first airport of the connection (if directional this will be the start vertex)
     * @param lastVertex the second airport of the connection (if directional this will be the destination vertex)
     * @param weight the weight of the edge
     * @param directional true if directional, false if undirectional
     */
    public void addEdge(Airport firstVertex, Airport lastVertex, int weight, boolean directional){
        addVertex(firstVertex);
        addVertex(lastVertex);
        this.graph.get(firstVertex).put(lastVertex, weight);
        if(!directional){
            this.graph.get(lastVertex).put(firstVertex, weight);
        }
    }

    /**
     * Removes an edge from the graph
     * @param firstVertex the first airport of the connection (if directional this will be the start vertex)
     * @param lastVertex the second airport of the connection (if directional this will be the destination vertex)
     * @param directional type of removal, true ignores direction
     */
    public void removeEdge(Airport firstVertex, Airport lastVertex, boolean directional){
        this.graph.get(firstVertex).remove(lastVertex);
        if(!directional){
            this.graph.get(lastVertex).remove(firstVertex);
        }

    }

    /**
     * Implementation of Dijkstas fastest route
     * @param departure the airport of departure
     * @param destination the airport of arrival
     */
    public void route(Airport departure, Airport destination){
        //Create queue & list
        LinkedList<Airport> checkedVertex = new LinkedList<>();
        PriorityQueue<QueueBucket> queue = new PriorityQueue<>();

        //Handle first round
        QueueBucket currentVertex = new QueueBucket(departure, 0, null);
        queue.add(currentVertex);

        //While the vertex to be checked isn't the destination
        while (currentVertex.getVertex() != destination){
            //Add airport to checked vertex &  remove it from queue
            queue.poll();
            checkedVertex.add(currentVertex.getVertex());
            //Needs to be a final copy for lambda
            QueueBucket finalCurrentVertex = currentVertex;
            //key is new vertex / value is the weight of the edge
            Map<Airport, Integer> test = this.graph.get(currentVertex.getVertex());
            test.forEach((k, v) -> {
                if(!checkedVertex.contains(k)) {
                    try {
                        //Filter Queue for vertex
                        QueueBucket bucket = (QueueBucket) queue.stream().filter((b) -> b.getVertex().equals(k)).toArray()[0];
                        //Check if new route  is faster than last
                        if(finalCurrentVertex.getRouteLength()+v < bucket.getRouteLength()){
                            //Overwrite last route with new, faster route
                            bucket.setRouteLength(finalCurrentVertex.getRouteLength()+v);
                            bucket.setLastVertex(finalCurrentVertex.getVertex());
                        }
                    //If vertex is not yet present in queue
                    } catch (ArrayIndexOutOfBoundsException e) {
                        //Add a new vertex with route length
                        queue.add(new QueueBucket(k, v + finalCurrentVertex.getRouteLength(), finalCurrentVertex.getVertex()));
                    }
                }
            });
            //Get new current vertex
            currentVertex = queue.peek();
        }

        //TODO: Process result in queue
        System.out.println("Dijkstra End");
    }

    /**
     * modifies the weight of the edge (both directions if undirectional)
     * @param firstVertex the first airport of the connection
     * @param lastVertex the second airport of the connection
     * @param weight the new weight of the route
     */
    public void alterWeight(Airport firstVertex, Airport lastVertex, int weight){
        this.graph.get(firstVertex).replace(lastVertex, weight);
        this.graph.get(lastVertex).replace(firstVertex, weight);
    }
}
