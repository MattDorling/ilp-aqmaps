package uk.ac.ed.inf.aqmaps;

import java.util.List;

/**
 * This class implements Collidable and provides an abstraction of a building - a polygon within the map
 * with which the drone cannot collide.
 *
 */
public class Building implements Collidable{
    private final List<Coordinate> coordinates;

    /**Constructor for the Building class.
     * 
     * @param coordinates is a list of points defining the perimeter of the building.
     */
    public Building(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
        // the building polygon is a closed loop, so add the first element to the end
        this.coordinates.add(coordinates.get(0));
    }
    
    /**Determines whether a move from one point to another will collide with the building.
     * @param c1 is the start point of a move.
     * @param c2 is the end point of a move.
     */
    @Override
    public boolean collides(Coordinate c1, Coordinate c2) {
        // check through each consecutive pair of coordinates of the building
        for (int i = 0; i < coordinates.size()-1; i++) {
            var p1 = coordinates.get(i);
            var p2 = coordinates.get(i+1);
            // if it intersects with any one pair, there is a collision, return true
            if (CollidableUtils.intersects(p1,p2,c1,c2)) { return true;}
        }
        // no collision detected, return true
        return false;
    }
    
}