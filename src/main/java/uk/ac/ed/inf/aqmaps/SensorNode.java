package uk.ac.ed.inf.aqmaps;

public class SensorNode extends Coordinate{
    private int index;
    public int getIndex() {
        return index;
    }

    public SensorNode(int index, Coordinate coordinates) {
        super(coordinates.getLatitude(), coordinates.getLongitude());
        this.index = index;
    }
    

}
