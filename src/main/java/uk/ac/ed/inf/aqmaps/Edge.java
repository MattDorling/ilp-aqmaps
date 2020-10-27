package uk.ac.ed.inf.aqmaps;

import java.util.LinkedList;

public class Edge {
    private int from;
    private int to;
    private double cost;
    private LinkedList<Coordinate> path;
    
    public int getFrom() {
        return from;
    }
    public int getTo() {
        return to;
    }
    public double getCost() {
        return cost;
    }
    public Edge(int from, int to, double cost, LinkedList<Coordinate> path) {
       this.from = from;
       this.to = to;
       this.cost = cost;
       this.path = path;
    }
    

}
