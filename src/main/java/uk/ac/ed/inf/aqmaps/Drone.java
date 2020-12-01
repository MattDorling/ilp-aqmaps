
package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.LinkedList;

public class Drone {
    private final Navigator nav;
//    private final Map map;
    private LinkedList<Coordinate> route;
    private final ServerController server;
    private final MyDate date;
    
    public Drone(ServerController sc, Coordinate start, MyDate date) {
        this.date = date;
        var points = sc.getAqData();
        var coords = new ArrayList<Coordinate>();
        int i = 0;
        for (AqPoint p : points) {
            coords.add(sc.getCoordinates(p.getW3W()));
            i++;
        }
        this.nav = new Navigator(coords, points, sc.getNoFlyZones(), start);
        this.server = sc;
        this.route = nav.nnAlgorithm();
        if (this.route.size() > 150) {
            this.route = nav.nnAlgorithm();
        }
    }
    
    public void travelRoute() {
        var fpFile = new FlightPathFile(date);
        var rFile = new ReadingsFile(date);
        rFile.stage(this.route.toArray(new Coordinate[0]));
        SensorConnector sensors = new SensorConnector(this.server);
        for (int i = 0; i < this.route.size() - 1; i++) {
            AqPoint aqSensor = sensors.readSensor(this.route.get(i));
            Coordinate from = this.route.get(i);
            Coordinate to = this.route.get(i+1);
            String w3w = "null";
            if (aqSensor != null) {
                rFile.stage(aqSensor, server.getCoordinates(aqSensor.getW3W()));
                w3w = aqSensor.getW3W();
            }
            fpFile.append(i, from, from.angleTo(to), to, w3w);
        }
        rFile.write();
    }
}
