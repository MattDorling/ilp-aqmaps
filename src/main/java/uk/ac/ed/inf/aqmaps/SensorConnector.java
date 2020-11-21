package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

public class SensorConnector {
    private final ServerController server;
    private final List<AqPoint> aqPoints;
    private final List<Target> aqTargets;
    public SensorConnector(ServerController sc) {
        this.server = sc;
        this.aqPoints = sc.getAqData();
        var targets = new ArrayList<Target>();
        for (AqPoint a : this.aqPoints) {
            targets.add(new Target(this.server.getCoordinates(a.getW3W())));
        }
        this.aqTargets = targets;
    }
    public AqPoint readSensor(Coordinate pos) {
        AqPoint p = null;
        for (int i = 0; i < this.aqTargets.size(); i++) {
            if (this.aqTargets.get(i).isHit(pos)) {
                p = aqPoints.get(i);
            }
        }
        return p;
    }
}
