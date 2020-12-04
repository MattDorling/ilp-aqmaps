package uk.ac.ed.inf.aqmaps;


/**
 * This acts as a starting point to take in parameters
 * and instantiate the Drone class.
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // parsing all of the input arguments:
        MyDate date = new MyDate(args[2], args[1], args[0]);
        
        // preparing start point coordinate
        double startLat = Double.parseDouble(args[3]);
        double startLon = Double.parseDouble(args[4]);
        Coordinate start = new Coordinate(startLat, startLon);
        
        long seed = Long.parseLong(args[5]);
        int port = Integer.parseInt(args[6]);
        
        // instantiate a ServerController for the given date and port
        ServerController sc = new ServerController(port, date);
        
        // check if the connection to the server is ok
        if (sc.checkConnected()) {
            // instantiate a Drone and tell it to travel the route
            Drone d = new Drone(sc, start, date, seed);
            d.travelRoute();
            System.out.println("Complete");
        }        
    }
}
