package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private ArrayList<Coordinate> nodes;
    private ArrayList<Edge> edges;
    
    public Graph(List<Coordinate> c) {
        nodes = (ArrayList<Coordinate>) c;
        edges = new ArrayList<>();
    }
    public Coordinate getNode(int index) {
        return nodes.get(index);
    }
    public int nodeCount() {
        return nodes.size();
    }

}
