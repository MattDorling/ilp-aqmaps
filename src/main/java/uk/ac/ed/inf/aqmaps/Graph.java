package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    
    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }
    public void addNode(Node n) {
        nodes.add(n);
    }
    public Node getNode(int index) {
        return nodes.get(index);
    }
    public int nodeCount() {
        return nodes.size();
    }

}
