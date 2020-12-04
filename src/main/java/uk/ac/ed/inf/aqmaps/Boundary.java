package uk.ac.ed.inf.aqmaps;

/**
 * This class implements Collidable and provides an abstraction of a Boundary -
 * one side of the drone confinement area.
 *
 */
public class Boundary implements Collidable {
    private final Coordinate pointA;
    private final Coordinate pointB;
    
    /**Constructor for the Boundary class.
     * 
     * @param pointA is one end of the boundary line.
     * @param pointB is the other end of the boundary line.
     */
    public Boundary(Coordinate pointA, Coordinate pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }
    
    /**Determines whether a move from one point to another will collide with the boundary.
     * @param c1 is the start point of a move.
     * @param c2 is the end point of a move.
     */
    @Override
    public boolean collides(Coordinate c1, Coordinate c2) {
        return CollidableUtils.intersects(this.pointA, this.pointB, c1, c2);
    }

}
