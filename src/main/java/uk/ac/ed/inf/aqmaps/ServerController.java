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
    public String getLocationDetails(String word1, String word2, String word3) {
        String locator = "/words/" + word1 + "/" + word2 + "/" + word3 + "/details.json";
        var request = HttpRequest.newBuilder()
                .uri(URI.create(address + locator))
                .build();
        String out = null;
        try {
            var response = httpClient.send(request, BodyHandlers.ofString());
            out = response.body();
        }catch(Exception e) {System.out.println(e);}
        return out; 
    }
    
    public String checkStatus(){
        String locator = "/maps/" + Integer.toString(this.date.getYear())
        + "/" + String.format("%02d", this.date.getMonth()) 
        + "/" + String.format("%02d", this.date.getDay()) 
        + "/air-quality-data.json";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.address+locator))
                .build();
        HttpResponse<Void> response;
        String out = "An error occured";
        try {
            response = httpClient.send(request, BodyHandlers.discarding());
            if (response.statusCode()==404) {
                out = "Date does not exist on server";
            } else {
                out = "ok";
            }
        }catch(ConnectException ce) { out = "Unable to connect on " +this.address;
        }catch(Exception e) {System.out.println(e);}
        return out;
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
    
    public List<Building> getNoFlyZones() {
        var buildings = new ArrayList<Building>();
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
