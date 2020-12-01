package uk.ac.ed.inf.aqmaps;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Navigator {
    private static final double MOVE_SIZE = 0.0003;
//    private Graph graph;
    private Coordinate start;
    private List<AqPoint> AqPoints;
    private List<Building> noFlyZones;
    private List<Coordinate> nodes;


    public Navigator(List<Coordinate> coordinates, 
            List<AqPoint> AqData, List<Building> noFlyZones, Coordinate start) {
        this.start = start;
        this.nodes = coordinates;
        this.AqPoints = AqData;
        this.noFlyZones = noFlyZones;
        }

    public LinkedList<Coordinate> nnAlgorithm() {
        var unvisited = new HashSet<Integer>();
        var nodePath = new LinkedList<>();
        var path = new LinkedList<Coordinate>();
        LinkedList<Coordinate> path2append;
        for (int i = 0; i < nodes.size() - 1; i++) {
            unvisited.add(i);
        }

        Coordinate dronePos = this.start;
        Coordinate targetNode;
        path.add(this.start);
        int targetIndex;
        do {
            targetIndex = nearestNode(unvisited, dronePos);
            nodePath.add(targetIndex);
            targetNode = nodes.get(targetIndex);

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
            dist = startNode.getDist(nodes.get(i));
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
                hitTar = false;
                path = new LinkedList<>();
                path.add(start);
                prevAng = (int) (Math.round(path.getLast().angleTo(tar)/10.0)*10);
                do {
                    ranAng = (int) Math.round((Math.random() - 0.5) * 2)*10;
                    ang = (int) Math.round(path.getLast().angleTo(tar)/10.0)*10 + ranAng;
                    ang = this.nonCollidingAngle(path.getLast(), ang);
                    backStep = checkBackStep(ang,prevAng);
                    c = path.getLast().findFrom(ang, MOVE_SIZE);
                    path.add(c);
                    prevAng = ang;
                    hitTar = tar.isHit(c);
                } while (!hitTar && !backStep && path.size() < 150);
            } while (!hitTar || backStep  || path.size() > 150);
        }
        return path;
    }
    
    private boolean checkBackStep(int ang1, int ang2) {
        if (ang1 < 0) { ang1 += 360; }
        if (ang2 < 0) { ang2 += 360; }
        return (360 - Math.abs(ang1-ang2) > 170 && Math.abs(ang1-ang2) > 170);
    }
    
    private int nonCollidingAngle(Coordinate from, int angle) {
        boolean found = false;
        for (int i = 0; i < 36; i++) {
            angle += i*10;
            var testCoordinate = from.findFrom(angle, MOVE_SIZE);
            if (!collides(from, testCoordinate)) {
                break;
            } else {
                angle -= i*20;
                testCoordinate = from.findFrom(angle, MOVE_SIZE);
                if (!collides(from, testCoordinate)) {
                    break;
                }
            }
        }
        return angle;
    }
    
    private boolean collides(Coordinate c1, Coordinate c2) {
        boolean collision = false;
        for (Building b : this.noFlyZones) {
            if (b.collides(c1, c2)){
                collision = true;
            }
        }
        return collision;
    }
}
