package uk.ac.ed.inf.aqmaps;

/**
 * This class provides a simple way of storing
 * the information of an air quality sensor as an object.
 */
public class AqPoint {
    private String location;
    private double battery;
    private String reading;
    
    /**
     * Constructor for the AqPoint class.
     * @param location is the What3Words location
     * @param battery is the value indicating the battery level percentage
     * @param reading is the value indicating the air quality reading
     */
    public AqPoint(String location, double battery, String reading) {
        this.location = location;
        this.battery = battery;
        this.reading = reading;
    }
    /**
     * Getter for the What3Words location.
     * @return W3W location string.
     */
    public String getW3W() {
        return location;
    }
    /**
     * 
     * @return true if the battery is below 10%, false otherwise
     */
    public boolean lowBattery() {
        return this.battery < 10.0;
    }
    /**
     * Getter for the air quality reading.
     * @return Air quality reading as double.
     */
    public double getReading() {
        return Double.parseDouble(reading);
    }
}
