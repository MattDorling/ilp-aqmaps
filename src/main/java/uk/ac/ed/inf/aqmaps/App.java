package uk.ac.ed.inf.aqmaps;



/**
 * Hello world!
 *
 */
public class App 
{
    
    public static void main( String[] args )
    {
        MyDate date = new MyDate(args[2], args[1], args[0]);
        double startLat = Double.parseDouble(args[3]);
        double startLon = Double.parseDouble(args[4]);
        Coordinate start = new Coordinate(startLat, startLon);
        
        int seed = Integer.parseInt(args[5]);
        int port = Integer.parseInt(args[6]);

        ServerController s = new ServerController(port);
        Map m = new Map(s, date);
        Drone d = new Drone(m, s, start);
        d.travelRoute();
    }
}
