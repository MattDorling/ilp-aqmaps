package uk.ac.ed.inf.aqmaps;

import java.util.List;

import com.mapbox.geojson.*;

public class Map{
    private List<AqPoint> aqpoints;

    private FeatureCollection noflyzones;
    public Map(ServerController s, MyDate d) {
        aqpoints = s.getAqData(d);
        this.noflyzones = s.getNoFlyZones();
    }
    
    public List<AqPoint> getAqpoints() {
        return aqpoints;
    }

}
