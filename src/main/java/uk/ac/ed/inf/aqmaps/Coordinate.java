package uk.ac.ed.inf.aqmaps;

public class Coordinate {
    private double lat;
    private double lng;
    
    
    public double getLatitude() {
        return lat;
    }
    public void setLatitude(double latitude) {
        this.lat = latitude;
    }
    public double getLongitude() {
        return lng;
    }
    public void setLongitude(double longitude) {
        this.lng = longitude;
    }


    public Coordinate(double latitude, double longitude) {
        this.lat = latitude;
        this.lng = longitude;
    }
    
    public double getDist(Coordinate c) {
        var x = this.getLongitude() - c.getLongitude();
        var y = this.getLatitude() - c.getLatitude();
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    public double angleTo(Coordinate c) {
        var x = - this.getLongitude() + c.getLongitude();
        var y = - this.getLatitude() + c.getLatitude();
        var angle = Math.toDegrees(Math.atan(y/x));
//        if (x > 0 && y <0) { angle += 90; }
//        else if (x < 0 && y < 0) { angle += 180; }
//        else if (x < 0 && y > 0) { angle += 270; }
        return angle;
    }
    
    public boolean inRange(Coordinate c) {
        return this.getDist(c) <= 0.0002;
    }
    
    public Coordinate findFrom(double angle, double distance) {
        double newy = this.getLatitude() + Math.cos(angle) * distance;
        double newx = this.getLongitude() + Math.sin(angle) * distance;
        return new Coordinate(newy, newx);
    }
}
