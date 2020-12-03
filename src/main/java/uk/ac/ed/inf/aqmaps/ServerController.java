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

/**
 * This class provides a way of receiving information from the WebServer, which is assumed to be
 * running on https://localhost/$PORT where PORT is provided as a parameter of the constructor
 * method. It will also load some of the information from the server into necessary objects.
 */
public class ServerController {
    // Create HttpClient with default settings
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String address;
    private final MyDate date;
    
    /**
     * Constructor for ServerController class.
     * @param port is the provided port that the WebServer is running on.
     * @param date is the provided date that is used to access the information from the WebServer.
     */
    public ServerController(int port, MyDate date) {
        this.address = "http://localhost:" + Integer.toString(port);
        this.date = date;
    }

    /**
     * Checks the server to see if it is possible to connect, whether the given date
     * for the server exists on the server (error 404). Prints a lightweight error
     * message as a string if necessary.
     * @return true if connection is ok, false if there is a problem.
     */
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
    
    /**
     * Finds the air quality data from the air-quality.json file for the provided date on
     * the server. Parse the json into a List of AqPoint.
     * @return the list of AqPoint from the server.
     */
    public List<AqPoint> getAqData() {
        // build a resource locator for the json file on the server
        String locator = "/maps/" + Integer.toString(this.date.getYear())
        + "/" + String.format("%02d", this.date.getMonth()) 
        + "/" + String.format("%02d", this.date.getDay()) 
        + "/air-quality-data.json";
        
        // fetch Json string from the server.
        String json =  getJson(locator);
        
        // extract a list of AqPoints from the json string and return it.
        Type listType = new TypeToken<ArrayList<AqPoint>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }
    
    /**
     * Find the latitude and longitude of a point at the centre of a
     * What3Words tile, stored on the server.
     * @param w3w must be a three words separated by '.'. For example: "slips.mass.baking"
     * @return a Coordinate corresponding to the given w3w parameter. 
     */
    public Coordinate getCoordinates(String w3w) {
        // split the w3w input by "." to get 3 separate words
        String[] word = w3w.split("\\.");
        // build a resource locator for the json file on the server
        String locator = "/words/" + word[0] + "/" + word[1] + "/" + word[2] + "/details.json";
        // fetch Json string from the server.
        String json =  getJson(locator);
        // parse the json and find the coordinates of the points:
        JsonElement rootNode = JsonParser.parseString(json);
        JsonObject details = rootNode.getAsJsonObject();
        JsonObject coord = details.getAsJsonObject("coordinates");
        JsonElement lng = coord.get("lng");
        JsonElement lat = coord.get("lat");
        // instantiate a new Coordinate object with the latitude and longitude and return it.
        return new Coordinate(lat.getAsDouble(), lng.getAsDouble());
    }
    
    /**
     * Fetch the no-fly-zones.geojson file from the WebServer,
     * and parse it into a list of Building objects.
     * @return a list of Building objects from the server.
     */
    public List<Collidable> getNoFlyZones() {
        // initialize an ArrayList of Collidables
        var buildings = new ArrayList<Collidable>();
        // build a resource locator for the GeoJson file on the server 
        String locator = "/buildings/no-fly-zones.geojson";
        // fetch the GeoJson from the server and parse it into a FeatureCollection
        var fc =  FeatureCollection.fromJson(getJson(locator));
        // get the features from the FeatureCollection
        var features = fc.features();
        // iterate through the Features
        for (Feature f : features) {
            // initialize an ArrayList of coordinates that will hold each building's polygon points
            var coords = new ArrayList<Coordinate>();
            // get the Geometry Polygons from the feature
            Polygon poly = (Polygon) f.geometry();
            // get the points that make up the building polygon
            List<Point> points = poly.coordinates().get(0);
            // convert each point to a Coordinate object and add it to the ArrayList of coordinates
            for (Point point : points) {
                coords.add(new Coordinate(point.latitude(), point.longitude()));
            }
            // instantiate a building from the ArrayList of coordinates
            buildings.add(new Building(coords));
        }
        // return the list of Building Collidables
        return buildings;
    }
    
    /**
     * Method to fetch a Json string from the server.
     * @param locator specifies the location on the server to find the Json.
     * @return the json fetched from the server as a string.
     */
    private String getJson(String locator) {
        // create a HttpRequest for the given location on the server
        var request = HttpRequest.newBuilder()
                .uri(URI.create(address + locator))
                .build();
        // initialize the string that will hold the fetched json. 
        String out = "";    // string is empty if no json is found.
        try {
            // try getting the json from the server
            var response = httpClient.send(request, BodyHandlers.ofString());
            out = response.body();
        }catch(Exception e) {System.out.println(e);}    // catch any exceptions
        // return the fetched json string.
        return out;
    }
}
