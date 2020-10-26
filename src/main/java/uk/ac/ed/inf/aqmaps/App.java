package uk.ac.ed.inf.aqmaps;
import java.text.DateFormat;
import java.util.Date;

import com.mapbox.geojson.*;
/**
 * Hello world!
 *
 */
public class App 
{
    private static String yyyy;
    private static String mm;
    private static String dd;
    
    public static void main( String[] args )
    {
        MyDate date = new MyDate(args[2], args[1], args[0]);
        double startLat = Double.parseDouble(args[3]);
        double startLon = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        int port = Integer.parseInt(args[6]);

        ServerController s = new ServerController(port);
        Map m = new Map(s, date);
    }
}
