package uk.ac.ed.inf.aqmaps;



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
        
        ServerController sc = new ServerController(port, date);
        String status = sc.checkStatus();
        if (status == "ok") {
            Drone d = new Drone(sc, start, date);
            d.travelRoute();
            System.out.println("Complete");
        } else {
            System.out.println(status);
        }
        
    }
}
