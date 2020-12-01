package uk.ac.ed.inf.aqmaps;

import java.util.List;

public class Building {
    private final List<Coordinate> coordinates;

    public Building(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
        this.coordinates.add(coordinates.get(0));
    }
    
    public boolean collides(Coordinate c1, Coordinate c2) {
        for (int i = 0; i < coordinates.size()-1; i++) {
            var p1 = coordinates.get(i);
            var p2 = coordinates.get(i+1);
            if (intersects(p1,p2,c1,c2)) { return true;}
        }
        return false;
    }
    private int direction(Coordinate a, Coordinate b, Coordinate c) {
        double val = (b.getLongitude()-a.getLongitude())*(c.getLatitude()-b.getLatitude())
                -(b.getLatitude()-a.getLatitude())*(c.getLongitude()-b.getLongitude());
        if (val == 0) {
           return 0;     //colinear
        } else if (val < 0) {
           return 2;    //anti-clockwise direction
        }
        return 1;    //clockwise direction
     }

     private boolean intersects(Coordinate a1, Coordinate a2, Coordinate b1, Coordinate b2) {
        //four direction for two lines and points of other line
        int dir1 = direction(a1, a2, b1);
        int dir2 = direction(a1, a2, b2);
        int dir3 = direction(b1, b2, a1);
        int dir4 = direction(b1, b2, a2);
        if (dir1 != dir2 && dir3 != dir4) {
            return true;
        }
        return false;

     }
}