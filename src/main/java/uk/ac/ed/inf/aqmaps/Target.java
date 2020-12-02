package uk.ac.ed.inf.aqmaps;

/**
 * This class extends Coordinate, adding a radius.
 * These are used to represent the sensor points while generating a route.
 */
public class Target extends Coordinate{
    static final double RADIUS = 0.0002;
    
    /**
     *  Constructor for the class.
     *  Essentially transforms a Coordinate into a Target.
     * @param centre is the location of the target
     */
    public Target(Coordinate centre) {
        super(centre.getLatitude(), centre.getLongitude());
    }
    
    /**
     * Checks whether a given point is within the radius of the target.
     * The radius is the specified constant of 0.0002 (in degrees)
     * @param point is the point to test.
     * @return true if point is within radius, false if not.
     */
    public boolean isHit(Coordinate point) {
        return point.getDist(this) < RADIUS;
    }
}
