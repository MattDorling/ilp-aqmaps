package uk.ac.ed.inf.aqmaps;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * This class carries out all the algorithms and calculations needed to find a route to
 * navigate to the air quality sensors.
 */
public class Navigator {
    private static final double MOVE_SIZE = 0.0003;
    private final Coordinate start;
    private final List<Collidable> noFlyZones;
    private final List<Target> nodes;
    private final long seed;

    /**
     * Constructor for the Navigator class.
     * @param targets is the list of coordinates of the sensors that the drone should visit.
     * @param noFlyZones is the list of Buildings that the that the drone
     * should avoid colliding with.
     * @param start is the Coordinate location of the Drone's starting point.
     * @param seed is the seed that will be used for the randomized pathing.
     */
    public Navigator(List<Target> targets, 
            List<Collidable> noFlyZones, Coordinate start, long seed) {
        this.start = start;
        this.nodes = targets;
        this.noFlyZones = noFlyZones;
        this.seed = seed;
        
        // adding to the noFlyZones list the Boundary Collidables that together 
        // represent the edges of the confinement area
        var forrestHill = new Coordinate(55.946233,-3.192473);
        var topMeadows = new Coordinate(55.942617,-3.192473);
        var buccBusStop = new Coordinate(55.942617,-3.184319);
        var kfc = new Coordinate(55.946233,-3.184319);
        this.noFlyZones.add(new Boundary(forrestHill, topMeadows));
        this.noFlyZones.add(new Boundary(topMeadows, buccBusStop));
        this.noFlyZones.add(new Boundary(buccBusStop, kfc));
        this.noFlyZones.add(new Boundary(kfc, forrestHill));
        }
    
    /**
     *  Find a route using a nearest-neighbour algorithm decide the order in which to visit sensors.
     *  A randomized-pathing algorithm is used to generate moves between the points decided by 
     *  the nearest-neighbour algorithm, while meeting the
     *  constraints such as not hitting buildings, the confinement area, and the limited
     *  range of motion (i.e. 10 degree compass bearings).
     * @return a route as a linked-list of coordinates
     */
    public LinkedList<Coordinate> generateRoute() {
        // initialize a HashSet to store indices of unvisited nodes.
        var unvisited = new HashSet<Integer>();
        // initialize a LinkedList to hold the path of the drone.
        var path = new LinkedList<Coordinate>();
        // LinkedList to hold segments of the path that will be concatenated to the whole path.
        LinkedList<Coordinate> pathToAppend;
        
        // fill the HashSet of unvisited indices
        for (int i = 0; i < nodes.size(); i++) {
            unvisited.add(i);
        }
        
        // initialize a Coordinate to hold the hypothetical current position
        // of the drone, starting at the start.
        Coordinate dronePos = this.start;
        // declare a Coordinate to hold the Coordinate of the next sensor to path towards.
        Coordinate targetNode;
        // add the starting position to the route as the first position.
        path.add(this.start);
        int targetIndex;
        do {
            // get the index of the next unvisited sensor node.
            targetIndex = nearestNode(unvisited, dronePos);
            
            // use the index to get the Coordinate of the next sensor to path towards.
            targetNode = nodes.get(targetIndex);

            var failedVisits = new Stack<Integer>();
            boolean exit = false;
            do {
                // find a path from the drone's current position to the next sensor (near it)
                pathToAppend = randomlyPath(dronePos, new Target(targetNode));
                // on a rare occasion, the random pathing could get stuck navigating
                // to the nearest node. when this happens, it returns an empty path.
                // if there are other unvisited vertices available, try the next nearest node
                if (pathToAppend.isEmpty() && unvisited.size() > 1) {
                    // push the index that failed onto a stack
                    failedVisits.add(targetIndex);
                    // remove the failed index from the unvisited HashSet
                    unvisited.remove(targetIndex);
                    // find the next nearest node
                    targetIndex = nearestNode(unvisited, dronePos);
                    targetNode = nodes.get(targetIndex);
                // if there are no other unvisited indices, simply exit
                } else if (pathToAppend.isEmpty() && unvisited.size() == 1) {
                    // clear the unvisited indices and failed visits
                    unvisited.clear();
                    failedVisits.clear();
                    exit = true;
                }
            } while (pathToAppend.isEmpty() && !exit);
            // pop the indices of the nodes that failed to try to route to them from another point.
            if (!failedVisits.isEmpty()) {
                Integer popped = failedVisits.pop();
                do {
                    if (!unvisited.contains(popped)) {
                    unvisited.add(popped);
                    }
                } while (!failedVisits.isEmpty());
                
            }
            
            // remove the first Coordinate as this is already included as the last element in path.
            pathToAppend.removeFirst();
            
            // append the path to the next sensor to the path.
            path.addAll(pathToAppend);
            
            if (unvisited.contains(targetIndex)){
                // remove the index of that target from unvisited because it is now visited.
                unvisited.remove(targetIndex);
            }
            // update the hypothetical drone position
            dronePos = path.getLast();
            
        } while (!unvisited.isEmpty());
        
        // get a path back to the starting position.
        pathToAppend = randomlyPath(dronePos, new Target(this.start));
        
        // there is a very rare case that it is unable to path back to start, so check for that
        if (!pathToAppend.isEmpty()) {
            pathToAppend.removeFirst();
            // append to the route.
            path.addAll(pathToAppend);
        }
        // return the route.
        return path;
    }

    /**
     * Find the index of the nearest node to a given Coordinate.
     * @param indices is the list of unvisited indices to check through.
     * @param startNode is the position from which to find the nearest node.
     * @return
     */
    private int nearestNode(HashSet<Integer> indices, Coordinate startNode) {
        // initialize the shortest distance as the maximum possible double value.
        double shortestDist = Double.MAX_VALUE;
        double dist;
        int nearest = 0;
        // iterate through all nodes of the indices provided.
        for (int i : indices) {
            // calculate the distance to the node
            dist = startNode.getDist(nodes.get(i));
            // if it is shorter, use that to compare to the next index and update shortest distance.
            if (dist < shortestDist) {
                shortestDist = dist;
                nearest = i;
            }
        }
        // return the index of the nearest node.
        return nearest;
    }

    /**
     * Use a randomized-pathing algorithm to find a path from a given start point to a target.
     * @param start is the start point from which to begin the path.
     * @param tar is the Target to which the method will find a path (in this case, a sensor).
     * @return the path to the target node from the given start node.
     */
    private LinkedList<Coordinate> randomlyPath(Coordinate start, Target tar) {
        var randGen = new Random(this.seed);
        // declare angle variables
        int ang;
        int prevAng;
        int ranAng;
        // initialize exit condition variables
        boolean hitTar = false;
        boolean backStep = false;
        boolean trapped = false;
        boolean hitMoreThanOneTar = false;
        int attemptCounter = 0;
        int attemptCounter2 = 0;
        // declare the path
        LinkedList<Coordinate> path;
        Coordinate c;
        do {
            // reset the path
            path = new LinkedList<>();
            
            // reset inner loop counter
            attemptCounter2 = 0;
            
            // emergency infinite loop preventer (should never be needed, but just in case)
            attemptCounter += 1;
            if (attemptCounter > 500) { return path; }
            
            path.add(start);

            // get the angle from the start position to the target.
            prevAng = (int) (Math.round(path.getLast().angleTo(tar)/10.0)*10);
            do {
                // emergency infinite loop preventer (should never be needed, but just in case)
                attemptCounter2 += 1;
                if (attemptCounter2 > 500) { return new LinkedList<>(); }
                
                // default range of random angles is +/- 30 degrees from angle to target.
                int angleRange = 3;
                // if the position is close to the target, give it +/- 90 degrees range
                if (path.getLast().getDist(tar) < MOVE_SIZE * 2) { angleRange = 9;} 
                // generate a random offset angle 
                ranAng = (randGen.nextInt(angleRange*2) - angleRange) * 10;
                // find the angle to the target and add the offset angle.
                ang = (int) Math.round(path.getLast().angleTo(tar)/10.0)*10 + ranAng;
                // check if the angle is colliding with a no-fly zone 
                // and if so, adjust it to avoid.
                ang = nonCollidingAngle(path.getLast(), ang);

                // using the generated angle, get the next point on the path and add it.
                c = path.getLast().findFrom(ang, MOVE_SIZE);
                path.add(c);


                // in the rare (maybe impossible) case where a non-colliding angle cannot be  
                //  found, this can exit the loop to try again:
                if (collides(path.getLast(), c)) { trapped = true; }
                // check if the angle is going back on itself, which indicates a problem
                // such as the moves are repeatedly overstepping the target.
                backStep = checkBackStep(ang,prevAng);
                prevAng = ang;
                // check if the current coordinate hits the target
                hitTar = tar.isHit(c);
                // check if the current coordinate hits the target and at least one other
                hitMoreThanOneTar = hitTar && checkHittingOtherTargets(c,tar);

                // if the target is hit and there is no backstepping or trapped, exit both loops
                // also check that the path has not become far too long.
            } while (!hitTar && !backStep && !trapped && path.size() <= 150);
            // can exit the loop if a route has been found to the target without an issue.
            // if the target has not been found acceptably, try again.
        } while (!hitTar || hitMoreThanOneTar || backStep || trapped || path.size() >= 150);
        // return the path to the target.
        return path;
    }
    
    /**
     * Check through all targets other than the passed one to see if the point
     * is hitting any of the other target sensor nodes
     * @param point the point to check
     * @param tar the target that should not be checked
     * @return true if hitting other targets, false if not
     */
    private boolean checkHittingOtherTargets(Coordinate point, Target tar) {
        var nodesToCheck = this.nodes;
        for (Target node : nodesToCheck) {
            if (!node.equals(tar) && node.isHit(point)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the path has gone back on itself.
     * @param ang1 is the first angle.
     * @param ang2 is the second angle.
     * @return true if there is a backstep, false if not.
     */
    private static boolean checkBackStep(int ang1, int ang2) {
        if (ang1 < 0) { ang1 += 360; }
        if (ang2 < 0) { ang2 += 360; }
        return (360 - Math.abs(ang1-ang2) > 120 && Math.abs(ang1-ang2) > 120);
    }
    
    /**
     * Check if moving in a given direction from a given position will collide with a noflyzone.
     * Provides an alternative angle if it is.
     * @param from is the position to move from.
     * @param testAng is the proposed direction of movement
     * @return an angle that does not collide with a noflyzone.
     */
    private int nonCollidingAngle(Coordinate from, int angle) {
        int outAng = angle;
        for (int i = 0; i < 180; i += 10) {
            // iterate from 0 to 180, intervals of 10.
            // add i as a bias to the angle.
            int testAngPos = angle + i;
            // test if the angle with bias avoids collision.
            var testCoordinate = from.findFrom(testAngPos, MOVE_SIZE);
            if (!collides(from, testCoordinate)) {
                outAng = testAngPos;
                break;
            } else {
                // subtract i as a bias from the angle.
                int testAngNeg = angle - i;
                // test if the angle with bias avoids collision.
                testCoordinate = from.findFrom(testAngNeg, MOVE_SIZE);
                if (!collides(from, testCoordinate)) {
                    outAng = testAngNeg;
                    break;
                }
            }
        }
        // return the non colliding angle.
        return outAng;
    }
    
    /**
     * Checks if a move from one Coordinate to another will collide with any noflyzones.
     * @param c1 is the beginning point of the move.
     * @param c2 is the end point of the move
     * @return true if there is a collision, false if not.
     */
    private boolean collides(Coordinate c1, Coordinate c2) {
        // default is that there is no collision
        boolean collision = false;
        // iterate through all Collidable objects
        for (Collidable b : this.noFlyZones) {
            // check if there is a collision.
            if (b.collides(c1, c2)){
                collision = true;
                break;
            }
        }
        return collision;
    }
}
