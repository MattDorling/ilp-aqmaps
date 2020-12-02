package uk.ac.ed.inf.aqmaps;
/**
 * This class provides functionality to determine if a Collidable object has been collided with.
 */
public class CollidableUtils {
    
    /**
     *  Utility class should not be instantiated. Throws IllegalStateException
     */
    private CollidableUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    /**Method to determine if the line between two points intersects
     * the line between another two points.
     * 
     * @param a1 is first point on first line.
     * @param a2 is second point on first line.
     * @param b1 is first point on second line.
     * @param b2 is second point on second line.
     * @return true if intersects, false if not.
     */
    public static boolean intersects(Coordinate a1, Coordinate a2, Coordinate b1, Coordinate b2) {
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
         
    private static int direction(Coordinate a, Coordinate b, Coordinate c) {
        double val = (b.getLongitude()-a.getLongitude())*(c.getLatitude()-b.getLatitude())
                -(b.getLatitude()-a.getLatitude())*(c.getLongitude()-b.getLongitude());
        if (val == 0) {
           return 0;     //colinear
        } else if (val < 0) {
           return 2;    //anti-clockwise direction
        }
        return 1;    //clockwise direction
     }


}
