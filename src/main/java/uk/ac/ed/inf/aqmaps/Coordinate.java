package uk.ac.ed.inf.aqmaps;

/**
 *  Provides a representation of a point on the
 *  earth as a pair of degree coordinates and some useful calculations.
 */
public class Coordinate {
    private double lat;
    private double lng;
    
    /**
     * Constructor for the Coordinate class.
     * @param latitude of the coordinate
     * @param longitude of the coordinate
     */
    public Coordinate(double latitude, double longitude) {
        this.lat = latitude;
        this.lng = longitude;
    }

    /**
     * Calculates the distance from this Coordinate to another.
     * @param c is the point to which the method will calculate the distance.
     * @return the distance; the unit is degrees.
     */
    public double getDist(Coordinate c) {
        // calculate the distance to the coordinate, return it.
        var x = this.getLongitude() - c.getLongitude();
        var y = this.getLatitude() - c.getLatitude();
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    /**
     * Calculates the angle from this Coordinate to another.
     * @param c is the point to which the method will calculate the angle.
     * @return the distance; the unit is degrees and the value will be between -180 and +180.
     */
    public float angleTo(Coordinate c) {
        var x = - this.getLongitude() + c.getLongitude();
        var y = - this.getLatitude() + c.getLatitude();
        return (float) Math.toDegrees(Math.atan2(x,y));
    }

    /**
     * Calculates a point from this Coordinate, given the angle and the distance to it.
     * @param angle is the angle to the next point.
     * @param distance is the distance to the next point.
     * @return the location of the new point.
     */
    public Coordinate findFrom(double angle, double distance) {
        angle = Math.toRadians(angle);
        double newy = this.getLatitude() + Math.cos(angle) * distance;
        double newx = this.getLongitude() + Math.sin(angle) * distance;
        return new Coordinate(newy, newx);
    }
    

    
    
    /**
     * Getter for the latitude.
     * @return the latitude.
     */
    public double getLatitude() {
        return lat;
    }
    
    /**
     * Setter for the latitude.
     * @param The latitude.
     */
    public void setLatitude(double latitude) {
        this.lat = latitude;
    }
    
    /**
     * Getter for the longitude.
     * @return The longitude
     */
    public double getLongitude() {
        return lng;
    }
    
    /**
     * Setter for the longitude.
     * @param the longitude.
     */
    public void setLongitude(double longitude) {
        this.lng = longitude;
    }

}
