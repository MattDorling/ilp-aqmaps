package uk.ac.ed.inf.aqmaps;

public class Target extends Coordinate{
    static final double RADIUS = 0.0002;
    
    public Target(Coordinate centre) {
        super(centre.getLatitude(), centre.getLongitude());
    }
    
    public boolean isHit(Coordinate point) {
        return point.getDist(this) < RADIUS;
    }
}
