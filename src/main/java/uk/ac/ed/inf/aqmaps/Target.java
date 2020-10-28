package uk.ac.ed.inf.aqmaps;

public class Target extends Coordinate{
    static final double RADIUS = 0.0002;
    
    public Target(Coordinate centre) {
        super(centre.getLatitude(), centre.getLongitude());
    }
    
    public boolean intersects(Coordinate pointA, Coordinate pointB) {
        double b = pointB.getLatitude() - pointA.getLatitude()
                   /
                   pointB.getLongitude() - pointA.getLongitude();
        double c = pointB.getLatitude() - b * pointB.getLongitude();
        double dist = (Math.abs(this.getLongitude() + b*this.getLatitude() + c)) / b ;
        return (dist < RADIUS);
    }
    
    public boolean isHit(Coordinate point) {
        return point.getDist(this) < RADIUS;
    }
}
