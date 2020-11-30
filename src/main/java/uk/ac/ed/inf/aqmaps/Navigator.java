package uk.ac.ed.inf.aqmaps;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Navigator {
    private Graph graph;
    private Coordinate start;
    private Map map;
    private static final double MOVE_SIZE = 0.0003;

    public Navigator(List<Coordinate> coordinates, Map map, Coordinate start) {
        this.start = start;
        this.graph = new Graph(coordinates);
        this.map = map;
        }

    public LinkedList<Coordinate> nnAlgorithm() {
        var unvisited = new HashSet<Integer>();
        var nodePath = new LinkedList<>();
        var path = new LinkedList<Coordinate>();
        LinkedList<Coordinate> path2append;
        for (int i = 0; i < graph.nodeCount() - 1; i++) {
            unvisited.add(i);
        }

        Coordinate dronePos = this.start;
        Coordinate targetNode;
        path.add(this.start);
        int targetIndex;
        do {
            targetIndex = nearestNode(unvisited, dronePos);
            nodePath.add(targetIndex);
            targetNode = graph.getNode(targetIndex);

            path2append = randomlyPath(dronePos, new Target(targetNode));
            path2append.removeFirst();

            path.addAll(path2append);

            unvisited.remove(targetIndex);
            dronePos = path.getLast();
        } while (!unvisited.isEmpty());
        path2append = randomlyPath(dronePos, new Target(this.start));
        path2append.removeFirst();
        path.addAll(path2append);       
        return path;
    }

    private int nearestNode(HashSet<Integer> indices, Coordinate startNode) {
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

    private LinkedList<Coordinate> randomlyPath(Coordinate start, Target tar) {
        boolean hitTar = false;
        boolean backStep = false;
        int ang;
        int prevAng;
        int ranAng;
        var path = new LinkedList<Coordinate>();
        Coordinate c;
        if (tar.isHit(start)) {
            path.add(start);
        } else {
            do {
                backStep = false;
                path = new LinkedList<>();
                path.add(start);
                prevAng = (int) (Math.round(path.getLast().angleTo(tar)/10.0)*10);
                do {
                    ranAng = (int) Math.round((Math.random() - 0.5) * 2)*10;
                    ang = (int) Math.round(path.getLast().angleTo(tar)/10.0)*10 + ranAng;
                    ang = this.nonCollidingAngle(path.getLast(), ang);
                    if (ang - prevAng > 90) {
                        backStep = true;
                    }
                    c = path.getLast().findFrom(ang, MOVE_SIZE);
                    path.add(c);
                    prevAng = ang;
                    hitTar = tar.isHit(c);
                } while (!hitTar && !backStep);
            } while (backStep || path.size() > 20);
        }
        return path;
    }
    
    private int nonCollidingAngle(Coordinate from, int angle) {
        for (int i = 0; i < 18; i++) {
            if (i > 0) {angle += 10*((-1)^i);}
            var testCoordinate = from.findFrom(angle, MOVE_SIZE);
            if (!this.map.collides(from, testCoordinate)) {
                break;
            }
        }
        return angle;
    }
}
