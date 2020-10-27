package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.LinkedList;

public class Navigator {
    private Graph graph;
    private Map map;
    private Coordinate start;
    
    public Navigator(Map m, ServerController sc, Coordinate start) {
        var points = m.getAqpoints();
        this.start = start;
        this.map = m;
        this.graph = new Graph();
        Coordinate c;
        int i = 0;
        for (AqPoint p : points) {
            graph.addNode(new Node(i, sc.getCoordinates(p.getW3W())));
            i++;
        }
    }
    
    public void nnAlgorithm() {
        var unvisited = new ArrayList<Integer>();
        var nodePath = new LinkedList<>();
        var path = new LinkedList<Coordinate>();
        var path2append = new LinkedList<Coordinate>();
        
        for (int i = 0; i < graph.nodeCount(); i++) {
            unvisited.add(i);
        }
        
        Coordinate current = this.start;
        Coordinate nextCurrent;
        path.add(current);
        int next;
        do {
            next = nearestNode(unvisited, current);
            nodePath.add(next);
            nextCurrent = graph.getNode(next);
            path2append = calculatePath(current, nextCurrent);
            path2append.removeFirst();
            path.addAll(path2append);
            unvisited.remove(unvisited.indexOf(next));
            current = nextCurrent;
            
        } while (!unvisited.isEmpty());
        
        System.out.println(nodePath);
    }
    
    private int nearestNode(ArrayList<Integer> indices, Coordinate startNode) {

        double shortestDist = Double.MAX_VALUE;
        double dist;
        int nearest = 0;
        for (int i : indices) {
            dist = startNode.getDist(graph.getNode(i));
            if (dist < shortestDist) {
                shortestDist = dist;
                nearest = i;
            }
        }
        return nearest;
    }

    private LinkedList<Coordinate> calculatePath(Coordinate start, Coordinate target){
        Coordinate current;
        double ang = 0;
        LinkedList<Coordinate> path = new LinkedList<>();
        path.add(start);
        do {
            current = path.getLast();
            ang = Math.round(current.angleTo(target)/10.0)*10.0;    // round angle to 10
            path.add(current.findFrom(ang, 0.0003));
        } while (!path.getLast().inRange(target));
        return path;    
    }
    
}
