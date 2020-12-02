package uk.ac.ed.inf.aqmaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.*;
import com.mapbox.geojson.*;

public class ReadingsFile {
    private File file;
    private final List<Feature> features = new LinkedList<>();
    public ReadingsFile(MyDate date) {
        try {         
            this.file = new File("readings-" + date.toString() + ".geojson");
            if (this.file.exists()) {
            this.file.delete();
            }
            this.file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stage(Coordinate[] flightpath) {
        Feature path;
        var lop = new LinkedList<Point>();
        for (Coordinate c : flightpath) {
            lop.add(Point.fromLngLat(c.getLongitude(), c.getLatitude()));
        }
        path = Feature.fromGeometry(LineString.fromLngLats(lop));
        this.features.add(path);
    }

    public void stage(AqPoint reading, Coordinate loc) {
        var f = Feature.fromGeometry(Point.fromLngLat(loc.getLongitude(), loc.getLatitude()));
        f.properties().addProperty("marker-size", "medium");
        f.properties().addProperty("location", reading.getW3W());
        String symbol;
        String rgb;
        if (reading.lowBattery()) {
            rgb = "#000000";
            symbol = "cross";
        } else {
            rgb = getRgbString(reading.getReading());
            if (reading.getReading() < 128) {
                symbol = "lighthouse";
            } else {
                symbol = "danger";
            }
        }
        f.properties().addProperty("rgb-string", rgb);
        f.properties().addProperty("marker-color", rgb);
        f.properties().addProperty("marker-symbol", symbol);
        features.add(f);
    }
    
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
    
    public void write() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(FeatureCollection.fromFeatures(this.features).toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
