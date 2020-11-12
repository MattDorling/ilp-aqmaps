
package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.LinkedList;

public class Drone {
    private final Navigator nav;
    private final Map map;
    private LinkedList<Coordinate> route;
    private final ServerController server;
    private Coordinate position;
    
    public Drone(Map m, ServerController sc, Coordinate start) {
        var points = m.getAqpoints();
        var coords = new ArrayList<Coordinate>();
        int i = 0;
        for (AqPoint p : points) {
            coords.add(sc.getCoordinates(p.getW3W()));
            i++;
        }
        this.nav = new Navigator(coords, sc, start);
        this.map = m;
        this.server = sc;
        this.route = nav.nnAlgorithm(); // TODO change this to a better name like loadRoute()
        this.position = start;
    }
    
    public void travelRoute() {
//        # TODO program drone to visit each point in route, collecting sensor data
    }
}
