package uk.ac.ed.inf.aqmaps;

public class Details {
    private String country;
    private String square;
    private String nearestPlace;
    private Coordinate coordinates;
    public Coordinate getCoordinates() {
        return coordinates;
    }
    private String words;
    private String language;
    private String map;
    public Details(String country, String square, String nearestPlace, Coordinate coordinates, String words,
            String language, String map) {
        super();
        this.country = country;
        this.square = square;
        this.nearestPlace = nearestPlace;
        this.coordinates = coordinates;
        this.words = words;
        this.language = language;
        this.map = map;
    }
    
}
