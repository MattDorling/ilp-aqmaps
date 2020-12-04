
package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *This class is an abstracted representation of the Drone.
 *It mainly interacts with instantiations of the ServerController and Navigator classes
 *to implement the Drone's function.
 */
public class Drone {
    private final Navigator nav;
    private LinkedList<Coordinate> route;
    private final ServerController server;
    private final MyDate date;
    
    
    /**
     * Constructor for the class. It also instantiates a Navigator and loads the route from it.
     *
     * @param sc is the ServerController object
     * @param start is the starting location of the drone
     * @param date is the date on which the drone is being used
     */
    public Drone(ServerController sc, Coordinate start, MyDate date, long seed) {
        this.date = date;
        var points = sc.getAqData();
        var targets = new ArrayList<Target>();
        
        // iterate through Air Quality points, getting their coordinates
        for (AqPoint p : points) {
            targets.add(new Target(sc.getCoordinates(p.getW3W())));
        }
        // instantiate an object of Navigator class
        this.nav = new Navigator(targets, sc.getNoFlyZones(), start, seed);
        this.server = sc;
        
        // generate a route from the Navigator
        this.route = nav.generateRoute();
    }
    
    /**
     *  This is an abstraction of the Drone's movements along the route that has been generated
     *  by the Navigator object. It travels the route, checking for sensors after each move
     *  using a SensorConnector. It uses FlightPathFile and ReadingsFile instantiations to
     *  write the output files as it generates the information.
     */
    public void travelRoute() {
        var fpFile = new FlightPathFile(date);
        var rFile = new ReadingsFile(date);
        var writtenW3W = new HashSet<String>();
        
        // stage the route flightpath to be written to geojson file (as a LineString)
        rFile.stage(this.route.toArray(new Coordinate[0]));
        
        SensorConnector sensors = new SensorConnector(this.server);
        // iterate through all the positions in the route
        int moves = this.route.size();
//        if (moves > 149) { moves = 149; }
        for (int i = 0; i < moves - 1; i++) {

            // current position
            Coordinate currentPos = this.route.get(i);
            // next position
            Coordinate nextPos = this.route.get(i+1);
            // scan from current position for a sensor
            AqPoint aqSensor = sensors.readSensor(nextPos);
            
            // initialize a What3Words string - default is the word "null" if no sensor detected
            String w3w = "null";
            
            // if an Air Quality sensor is detected
            if (aqSensor != null) {
                rFile.stage(aqSensor, server.getCoordinates(aqSensor.getW3W()));
                w3w = aqSensor.getW3W();
            }
            // send info for the current move to be appended to flightpath file
            fpFile.append(i, currentPos, currentPos.angleTo(nextPos), nextPos, w3w);
        }
        // call to write all staged information to the readings file
        rFile.write();
    }
}
