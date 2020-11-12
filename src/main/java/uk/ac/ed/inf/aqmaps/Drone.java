
package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.LinkedList;

public class Drone {
    private final Navigator nav;
    private final Map map;
    private LinkedList<Coordinate> route;
    private final ServerController server;
    private final MyDate date;
    
    public Drone(ServerController sc, Coordinate start, MyDate date) {
        this.map = new Map(sc);
        this.date = date;
        var points = this.map.getAqpoints();
        var coords = new ArrayList<Coordinate>();
        int i = 0;
        for (AqPoint p : points) {
            coords.add(sc.getCoordinates(p.getW3W()));
            i++;
        }
        this.nav = new Navigator(coords, sc, start);
        this.server = sc;
        this.route = nav.nnAlgorithm(); // TODO change this to a better name like loadRoute()
    }
    
    public void travelRoute() {
        var fpFile = new FlightPathFile(date);
              
        
//        # TODO program drone to visit each point in route, collecting sensor data
        SensorConnector sensors = new SensorConnector(this.server);
        for (int i = 0; i < this.route.size() - 1; i++) {
            AqPoint aqSensor = sensors.readSensor(this.route.get(i));
            Coordinate from = this.route.get(i);
            Coordinate to = this.route.get(i+1);
            String fpLine = String.format("%d,%f,%f,%d,%f,%f,", 
                    i+1,
                    from.getLongitude(),
                    from.getLatitude(),
                    Math.round((float) from.angleTo(to)),
                    to.getLongitude(),
                    to.getLatitude()
                    );
            
            if (aqSensor != null) {
                // TODO write geojson file
                fpLine += aqSensor.getW3W();
            } else {
                fpLine += "null"; // TODO am i printing null here??
            }
            fpFile.append(fpLine + "\n");
        }
    }
}