package uk.ac.ed.inf.aqmaps;
/** An interface to define an object or physical space that the drone should not collide with.
 *
 */
public interface Collidable {
    public boolean collides(Coordinate c1, Coordinate c2);
}
