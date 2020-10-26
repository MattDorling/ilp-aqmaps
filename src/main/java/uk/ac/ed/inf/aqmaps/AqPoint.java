package uk.ac.ed.inf.aqmaps;

public class AqPoint {
    private String location;
    private double battery;
    private String reading;
    
    public AqPoint(String location, double battery, String reading) {
        this.location = location;
        this.battery = battery;
        this.reading = reading;
    }
}
