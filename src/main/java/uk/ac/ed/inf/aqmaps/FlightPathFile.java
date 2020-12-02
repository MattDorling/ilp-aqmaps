package uk.ac.ed.inf.aqmaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class handles the preparation and writing of the flightpath output text file.
 */
public class FlightPathFile {
    private File file;
    
    /**
     * Constructor for the FlightPathFile class. Checks if the file already exists.
     * If so, delete it in order to replace it. Create the file.
     * @param date is used to write the file with the correct filename.
     */
    public FlightPathFile(MyDate date) {
        try {
            // instantiate a java.io.File object with the specified filename using the date.
            this.file = new File("flightpath-" + date.toString() + ".txt");
            
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
     * This method takes all of the required information of a move for one line of the output file,
     * converts it into an appropriate string for the line, then appends it to the file.
     * This file is essentially a serial file so we append each line one at a time with this method.
     * @param i is the integer indicating which move it is.
     * @param from is the starting position of the move.
     * @param angle is the angle of the move.
     * @param to is the ending position of the move.
     * @param w3w is the What3Words position of any detected sensor.
     * If there is no detected sensor, this should be "null"
     */
    public void append(int i, Coordinate from, double angle, Coordinate to, String w3w) {
        // ensure the angle is an integer
        int ang = (int) Math.round(angle);
        // if the angle is negative, convert it to a 0-360 angle.
        if (ang < 0) {
            ang += 360;
        }
        
        // format the information to the correctly formatted string for the line
        String fpLine = String.format("%d,%f,%f,%d,%f,%f,%s",
                i+1,
                from.getLongitude(),
                from.getLatitude(),
                ang,
                to.getLongitude(),
                to.getLatitude(),
                w3w
                ) + "\n";
        
        // try writing the line to the file.
        try (BufferedWriter bw = new BufferedWriter( new FileWriter(file,true))){
            bw.write(fpLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
