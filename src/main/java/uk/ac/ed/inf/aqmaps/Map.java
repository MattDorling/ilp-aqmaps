package uk.ac.ed.inf.aqmaps;

import java.util.List;


public class Map{
    private List<AqPoint> aqpoints;
    private List<Building> noflyzones;
    
    public Map(ServerController s) {
        this.aqpoints = s.getAqData();
        this.noflyzones = s.getNoFlyZones();
    }
    
    public List<AqPoint> getAqpoints() {
        return aqpoints;
    }
   
    public boolean collides(Coordinate c1, Coordinate c2) {
        for (Building b : this.noflyzones) {
            if (b.collides(c1, c2)){
                return true;
            }
        }
        return false;
    }
    
}
