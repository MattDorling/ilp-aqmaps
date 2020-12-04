package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class provides an abstraction of some imaginary module on the drone that would make
 * a connection to a nearby air quality sensor and download the data from it.
 */
public class SensorConnector {
    private final ServerController server;
    private final List<AqPoint> aqPoints;
    private final List<Target> aqTargets;
    
    /**
     * Constructor for the SensorConnector class.
     * @param sc is a ServerController object used by the class to emulate taking readings
     * from air quality sensors.
     */
    public SensorConnector(ServerController sc) {
        this.server = sc;
        
        // fetch the air quality readings from the server.
        this.aqPoints = sc.getAqData();
        
        // initialize an ArrayList of Targets.
        var targets = new ArrayList<Target>();
        // iterate through the air quality points.
        for (AqPoint a : this.aqPoints) {
            // instantiate a Target at the location of each point from the What3Words location.
            targets.add(new Target(this.server.getCoordinates(a.getW3W())));
        }
        // store the Targets.
        this.aqTargets = targets;
    }
    
    /**
     * This method checks if there is an air quality sensor near to a given position,
     * and fetches the data from that air quality sensor.
     * @param pos is the position to check for nearby sensors from.
     * @return the data from the sensor as an AqPoint
     */
    public AqPoint readSensor(Coordinate pos) {
        // default point if no sensor is detected is null.
        AqPoint p = null;
        // iterate through the targets
        for (int i = 0; i < this.aqTargets.size(); i++) {
            // check if the position is close enough to an air quality sensor.
            if (this.aqTargets.get(i).isHit(pos)) {
                // if so, get the AqPoint of that position
                p = aqPoints.get(i);
            }
        }
        // return the AqPoint if near enough to a sensor, or null if not.
        return p;
    }
}
