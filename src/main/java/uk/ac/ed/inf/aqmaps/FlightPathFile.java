package uk.ac.ed.inf.aqmaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FlightPathFile {
    private File file;
    
    public FlightPathFile(MyDate date) {
        boolean bool = false;
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

    public void append(String line) {
        try (
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(file,true)) 
            ){
            bw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
