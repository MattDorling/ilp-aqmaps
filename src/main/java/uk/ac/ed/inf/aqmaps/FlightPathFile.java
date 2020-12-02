package uk.ac.ed.inf.aqmaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FlightPathFile {
    private File file;
    
    public FlightPathFile(MyDate date) {
        try {    
            this.file = new File("flightpath-" + date.toString() + ".txt");
            if (this.file.exists()) {
                this.file.delete();
            }
            this.file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void append(String line) {
        try (
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(file,true)) 
            ){
            bw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void append(int i, Coordinate from, double angle, Coordinate to, String w3w) {
        int ang = (int) Math.round(angle);
        if (ang < 0) {
            ang += 360;
        }
        String fpLine = String.format("%d,%f,%f,%d,%f,%f,%s",
                i+1,
                from.getLongitude(),
                from.getLatitude(),
                ang,
                to.getLongitude(),
                to.getLatitude(),
                w3w
                );
        this.append(fpLine + "\n");
    }
}
