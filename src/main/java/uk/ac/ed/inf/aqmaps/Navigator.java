package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Navigator {
    private Graph graph;
    private Map map;
    private Coordinate start;
    private static final double MOVE_SIZE = 0.0003;

    public Navigator(Map m, ServerController sc, Coordinate start) {
        var points = m.getAqpoints();
        this.start = start;
        this.map = m;
        this.graph = new Graph();
        Coordinate c;
        int i = 0;
        for (AqPoint p : points) {
            graph.addNode(new SensorNode(i, sc.getCoordinates(p.getW3W())));
            i++;
        }
    }

    public void nnAlgorithm() {
        var unvisited = new HashSet<Integer>();
        var nodePath = new LinkedList<>();
        var path = new LinkedList<Coordinate>();
        var path2append = new LinkedList<Coordinate>();

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
            for (Coordinate c : path2append) {
                System.out.println("[" + c.getLongitude() + "," + c.getLatitude() + "],");
            }
            path.addAll(path2append);

            unvisited.remove(targetIndex);
            dronePos = path.getLast();
//            System.out.println("[" + graph.getNode(targetIndex).getLongitude() + "," + graph.getNode(targetIndex).getLatitude() + "]");
        } while (!unvisited.isEmpty());
        path2append = randomlyPath(dronePos, new Target(this.start));
        path2append.removeFirst();
        path.addAll(path2append);
        System.out.println(nodePath);
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
        double ang, prevAng, ranAng;
        LinkedList<Coordinate> path;
        Coordinate c;
        do {
            backStep = false;
            path = new LinkedList<Coordinate>();
            path.add(start);
            prevAng = Math.round(path.getLast().angleTo(tar)/10.0)*10;
            do {
                ranAng = Math.round((Math.random() - 0.5) * 2)*10;
                ang = Math.round(path.getLast().angleTo(tar)/10.0)*10 + ranAng;
                if (ang - prevAng > 90) {
                    backStep = true;
                }
                c = path.getLast().findFrom(ang , MOVE_SIZE);
                path.add(c);
                prevAng = ang;
                hitTar = tar.isHit(c);
            } while (!hitTar && !backStep);
        } while (backStep | path.size() > 20);
        return path;
    }

//    private LinkedList<Coordinate> calculatePath(Coordinate start, Coordinate target) {
//        Coordinate current;
//        Coordinate nextCoord;
//        Coordinate next2Coord;
//        Target tar = new Target(target);
//        double ang = 0;
//        double ang2 = 0;
//
//        LinkedList<Coordinate> newPath = new LinkedList<>();
//        newPath.add(start);
//
//        do {
//            current = newPath.getLast();
//            int i = 0;
//            ang = Math.round(current.angleTo(target) / 10.0) * 10.0; // round angle to 10
//            nextCoord = current.findFrom(ang - 10 * i, MOVE_SIZE);
//            newPath.add(nextCoord);
//        } while (!tar.isHit(nextCoord));
//        return newPath;
//    }    
    
//    private Node gridToTarget(Node node, Target tar) {
//        boolean notHit = true;
//        double angle;
//        if (tar.isHit(node.left.loc)
//                | tar.isHit(node.left.loc)){
//                notHit = false;
//        } else {
//            angle = Math.round(node.loc.angleTo(tar)/10.0)*10;
//            node.left = new Node(node.loc.findFrom(angle, MOVE_SIZE));
//            node.right = new Node(node.loc.findFrom(angle + 10.0, MOVE_SIZE));
//        }       
//    }

//    private LinkedList<Coordinate> recursivePath(Node node, Target target) {
//        var lpath = new LinkedList<Coordinate>();
//        var rpath = new LinkedList<Coordinate>();
//        var single = new LinkedList<Coordinate>();
//        double angle = Math.round((node.loc.angleTo(target)/10.0))*10;
//        node.left = new Node(node.loc.findFrom(angle, MOVE_SIZE));
//        node.right = new Node(node.loc.findFrom(angle + 10 , MOVE_SIZE));
//        if (target.isHit(node.left.loc)){
//            single.add(node.left.loc);
//            return single;
//        } else if (target.isHit(node.right.loc)){
//            single.add(node.right.loc);
//            return single;
//        } else {
//        
//        lpath.addAll(recursivePath(node.left, target));
//        rpath.addAll(recursivePath(node.right, target));
//        }
//        if (lpath.size() < rpath.size()) {
//            return lpath;
//        }
//        return rpath;
//    }

}
