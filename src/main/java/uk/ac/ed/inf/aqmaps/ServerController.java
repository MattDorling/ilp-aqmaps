package uk.ac.ed.inf.aqmaps;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.*;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.*;
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
    
    public FeatureCollection getNoFlyZones() {
        String locator = "/buildings/no-fly-zones.geojson";
        return FeatureCollection.fromJson(getJson(locator));
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
