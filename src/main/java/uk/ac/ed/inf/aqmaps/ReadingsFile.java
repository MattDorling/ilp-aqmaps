package uk.ac.ed.inf.aqmaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.*;
import com.mapbox.geojson.*;

/**
 * This class handles the preparation and writing of the readings output geojson file.
 * The geojson file requires all parts of the file to be ready before writing, so each part
 * of the file is staged as it is made available to the instantiation of this class.
 * The file can be written once all parts are staged.
 */
public class ReadingsFile {
    private File file;
    private final List<Feature> features = new LinkedList<>();
    
    /**
     * Constructor for the ReadingsFile class. Checks if the file already exists.
     * If so, delete it in order to replace it. Create the file.
     * @param date is used to write the file with the correct filename.
     */
    public ReadingsFile(MyDate date) {
        try {
            // instantiate a java.io.File object with the specified filename using the date.
            this.file = new File("readings-" + date.toString() + ".geojson");
            
            // if the file exists already, it is deleted first.
            if (this.file.exists()) {
                this.file.delete();
            }
            
            // create the file.
            this.file.createNewFile();
        } catch (IOException e) {       // catch any IO exceptions.
            e.printStackTrace();
        }
    }
    
    /**
     * This method creates a LineString Feature representing the drone's flightpath,
     * and adds it to a list of Feature to be written later.
     * @param flightpath is an array of the positions that the drone takes on its flight,
     * to be written as a GeoJson LineString.
     */
    public void stage(Coordinate[] flightpath) {
        Feature path;
        var lop = new LinkedList<Point>();
        
        // iterate through flightpath coordinates
        for (Coordinate c : flightpath) {
            // create a Point for each coordinate and add it to the list of Points
            lop.add(Point.fromLngLat(c.getLongitude(), c.getLatitude()));
        }
        
        // create a GeoJson LineString Feature from the list of Points
        path = Feature.fromGeometry(LineString.fromLngLats(lop));
        
        // add it to the list of Features to be written later.
        this.features.add(path);
    }

    /**
     * This method creates a Marker Feature representing the reading collected from an
     * air quality sensor, and adds it to a list of Features to be written later.
     * @param reading is the AqPoint reading collected from an air quality sensor that will be
     * staged for writing.
     * @param loc is the location of the sensor from which the reading was taken.
     */
    public void stage(AqPoint reading, Coordinate loc) {
        // create a Point feature for the sensor reading at the given location
        var f = Feature.fromGeometry(Point.fromLngLat(loc.getLongitude(), loc.getLatitude()));
        
        // adding the properties to the Point feature:
        f.properties().addProperty("marker-size", "medium");
        f.properties().addProperty("location", reading.getW3W());
        String symbol;
        String rgb;
        
        // if the reading had low battery, get the correct rgb and symbol properties
        if (reading.lowBattery()) {
            rgb = "#000000";
            symbol = "cross";
        } else {
            // choose the rgb string
            rgb = getRgbString(reading.getReading());
            
            // choose the symbol
            if (reading.getReading() < 128) {
                symbol = "lighthouse";
            } else {
                symbol = "danger";
            }
        }
        f.properties().addProperty("rgb-string", rgb);
        f.properties().addProperty("marker-color", rgb);
        f.properties().addProperty("marker-symbol", symbol);
        
        // add the Point to the list of Features
        features.add(f);
    }
    
    /**
     * This method writes all of the previously staged Features to the GeoJson file.
     */
    public void write() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            // convert the staged list of features to Json string and write to the GeoJson file.
            bw.write(FeatureCollection.fromFeatures(this.features).toJson());
        } catch (IOException e) {
            // catch any exceptions
            e.printStackTrace();
        }
    }
    
    /**
     * Simple helper function to get color code for a reading.
     * @param val is the reading
     * @return the color code for that reading
     */
    private static String getRgbString(double val) {
        // simple switch case to return correct color code
        switch ((int) (val / 32)) {
        case 0: return "#00ff00";
        case 1: return "#40ff00";
        case 2: return "#80ff00";
        case 3: return "#c0ff00";
        case 4: return "#ffc000";
        case 5: return "#ff8000";
        case 6: return "#ff4000";
        case 7: return "#ff0000";
        default:return "#000000"; // an error color
        }
    }
}
