package uk.ac.ed.inf.aqmaps;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.*;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.*;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


public class ServerController {
    // Create HttpClient with default settings
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String address;
    private final MyDate date;
    public ServerController(int port, MyDate date) {
        this.address = "http://localhost:" + Integer.toString(port);
        this.date = date;
    }

    public boolean checkConnected(){
        boolean retVal = false;
        
        // build a resource locator for the json file on the server
        String locator = "/maps/" + Integer.toString(this.date.getYear())
        + "/" + String.format("%02d", this.date.getMonth()) 
        + "/" + String.format("%02d", this.date.getDay()) 
        + "/air-quality-data.json";
        
        // make a HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.address+locator))
                .build();
        HttpResponse<Void> response;
        String out = "An error occured";
        try {
            // try sending the HttpRequest
            response = httpClient.send(request, BodyHandlers.discarding());
            
            // error 404 means that the date given does not exist on the server (like 60/02/2020)
            if (response.statusCode()==404) {
                out = "Date does not exist on server";
            } else {    // anything else should mean the connectin to the server is ok.
                retVal = true;
            }
        // ConnectException usually occurs when there is no server
        //   running on the localhost address and port.
        }catch(ConnectException ce) { out = "Unable to connect on " +this.address;
        
        // any other exception
        }catch(Exception e) {System.out.println(e);}
        
        // if there is a detected problem with the server, print a lightweight error message
        if (!retVal) { System.out.println(out); }
        
        // return the result
        return retVal;
    }
    
    public List<AqPoint> getAqData() {
        String locator = "/maps/" + Integer.toString(this.date.getYear())
        + "/" + String.format("%02d", this.date.getMonth()) 
        + "/" + String.format("%02d", this.date.getDay()) 
        + "/air-quality-data.json";
        String json =  getJson(locator);
        Type listType = new TypeToken<ArrayList<AqPoint>>() {}.getType();
        return new Gson().fromJson(json, listType);
        
    }
    
    public Coordinate getCoordinates(String w3w) {
        String[] word = w3w.split("\\.");
        String locator = "/words/" + word[0] + "/" + word[1] + "/" + word[2] + "/details.json";
        String json =  getJson(locator);
        JsonElement rootNode = JsonParser.parseString(json);
        JsonObject details = rootNode.getAsJsonObject();
        JsonObject coord = details.getAsJsonObject("coordinates");
        JsonElement lng = coord.get("lng");
        JsonElement lat = coord.get("lat");
        return new Coordinate(lat.getAsDouble(), lng.getAsDouble());
    }
    
    public List<Collidable> getNoFlyZones() {
        var buildings = new ArrayList<Collidable>();
        String locator = "/buildings/no-fly-zones.geojson";
        var fc =  FeatureCollection.fromJson(getJson(locator));
        var features = fc.features();
        for (Feature f : features) {
            var coords = new ArrayList<Coordinate>();
            Polygon poly = (Polygon) f.geometry();
            List<Point> points = poly.coordinates().get(0);
            for (Point point : points) {
                coords.add(new Coordinate(point.latitude(), point.longitude()));
            }
            buildings.add(new Building(coords));
        }
        return buildings;
    }
    
    private String getJson(String locator) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(address + locator))
                .build();
        String out = "";
        try {
            var response = httpClient.send(request, BodyHandlers.ofString());
            out = response.body();
        }catch(Exception e) {System.out.println(e);}
 
        return out;
    }
}
