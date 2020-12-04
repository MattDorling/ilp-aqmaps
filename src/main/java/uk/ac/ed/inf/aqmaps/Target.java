package uk.ac.ed.inf.aqmaps;

import java.util.Objects;

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
    
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {return true;}
        if (!(o instanceof Target)) {
            return false;
        }
        Target target = (Target) o;
        return this.getLatitude() == target.getLatitude() &&
                this.getLongitude() == target.getLongitude();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getLatitude(), this.getLongitude());
    }
    
}
