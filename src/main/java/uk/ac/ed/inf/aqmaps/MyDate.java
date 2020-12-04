package uk.ac.ed.inf.aqmaps;

/**
 * Simple class to represent a date: year, month and day. 
 * Much simpler to use for this project than any pre-written Date implementations.
 */
public class MyDate {
    private int day;
    private int month;
    private int year;
    
    /**Constructor for the MyDate class.
     * 
     * @param yyyy is a string of 4 digits representing the year
     * @param mm is a string of 2 digits representing the month (i.e. 03)
     * @param dd is a string of 2 digits representing the day (i.e. 03)
     */
    public MyDate(String yyyy, String mm, String dd) {
        try {
        this.year = Integer.parseInt(yyyy);
        this.month = Integer.parseInt(mm);
        this.day = Integer.parseInt(dd);
        } catch (Exception e) {System.out.println(e);}
    }
    /**
     * Getter for the day.
     * @return day as an int.
     */
    public int getDay() {
        return day;
    }
    /**
     * Getter for the month.
     * @return month as an int.
     */
    public int getMonth() {
        return month;
    }
    /**
     * Getter for the year.
     * @return year as an int.
     */
    public int getYear() {
        return year;
    }
    
    /**
     * Creates a string from the day, month, and year.
     * @return a string in the form "dd-mm-yyyy"
     */
    public String toString() {
        return String.format("%02d-%02d-%d", day, month, year);
    }
}
