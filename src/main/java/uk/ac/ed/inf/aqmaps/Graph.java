package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;

public class Graph {
    private ArrayList<SensorNode> nodes;
    private ArrayList<Edge> edges;
    
    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }
    public void addNode(SensorNode n) {
        nodes.add(n);
    }
    public SensorNode getNode(int index) {
        return nodes.get(index);
    }
    public int nodeCount() {
        return nodes.size();
    }

}
