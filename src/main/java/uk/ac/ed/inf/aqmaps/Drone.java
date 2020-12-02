
package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;



///\brief An abstracted representation of the Drone.
///Interacts with the ServerController and Navigator classes to implement the drone movement.

public class Drone {
    private final Navigator nav;
    private LinkedList<Coordinate> route;
    private final ServerController server;
    private final MyDate date;
    
    
    /**
     * @brief Constructor for the class.
     *
     * @param sc Takes a ServerController object
     * @param start The given starting location of the drone
     * @param date The given date used to interact with the server
     */
    public Drone(ServerController sc, Coordinate start, MyDate date) {
        this.date = date;
        var points = sc.getAqData();
        var coords = new ArrayList<Coordinate>();
        
        // iterate through Air Quality points, getting their coordinates
        for (AqPoint p : points) {
            coords.add(sc.getCoordinates(p.getW3W()));
        }
        // instantiate an object of Navigator class
        this.nav = new Navigator(coords, sc.getNoFlyZones(), start);
        this.server = sc;
        
        // generate a route from the Navigator
        this.route = nav.generateRoute();
    }
    
    public void travelRoute() {
        var fpFile = new FlightPathFile(date);
        var rFile = new ReadingsFile(date);
        var writtenW3W = new HashSet<String>();
        
        // stage the route flightpath to be written to geojson file (as a LineString)
        rFile.stage(this.route.toArray(new Coordinate[0]));
        
        SensorConnector sensors = new SensorConnector(this.server);
        for (int i = 0; i < this.route.size() - 1; i++) {
            // scan from current position for a sensor
            AqPoint aqSensor = sensors.readSensor(this.route.get(i));
            // current position
            Coordinate from = this.route.get(i);
            // next position
            Coordinate to = this.route.get(i+1);
            String w3w = "null";
            
            // if an Air Quality sensor is detected
            if (aqSensor != null) {
                // stage the reading to be written in the readings file
                // but only if it has not been written before
                w3w = aqSensor.getW3W();
                if (!writtenW3W.contains(w3w)) {
                    rFile.stage(aqSensor, server.getCoordinates(w3w));
                    writtenW3W.add(w3w);
                }
                
            }
            
            // send info for the current move to be appended to flightpath file
            fpFile.append(i, from, from.angleTo(to), to, w3w);
        }
        // call to write all staged information to the readings file
        rFile.write();
    }
}
