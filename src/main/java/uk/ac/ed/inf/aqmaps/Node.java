package uk.ac.ed.inf.aqmaps;

public class Node extends Coordinate{
    private int index;
    public int getIndex() {
        return index;
    }

    public Node(int index, Coordinate coordinates) {
        super(coordinates.getLatitude(), coordinates.getLongitude());
        this.index = index;
    }
    

}
