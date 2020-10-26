package uk.ac.ed.inf.aqmaps;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.*;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.*;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ServerController {
    // Create HttpClient with default settings
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String address;
    public ServerController(int port) {
        address = "http://localhost:" + Integer.toString(port);
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
    
    public List<AqPoint> getAqData(MyDate d) {
        String locator = "/maps/" + Integer.toString(d.getYear())
        + "/" + Integer.toString(d.getMonth()) 
        + "/" + Integer.toString(d.getDay()) 
        + "/air-quality-data.json";
        String json =  getJson(locator);
        Type listType = new TypeToken<ArrayList<AqPoint>>() {}.getType();
        return new Gson().fromJson(json, listType);
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
