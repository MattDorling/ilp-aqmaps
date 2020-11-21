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
    public String getW3W() {
        return location;
    }
    public boolean lowBattery() {
        return this.battery < 10.0;
    }
    public double getReading() {
        return Double.parseDouble(reading);
    }
}
