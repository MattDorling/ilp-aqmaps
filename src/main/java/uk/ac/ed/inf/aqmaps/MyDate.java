package uk.ac.ed.inf.aqmaps;

public class MyDate {
    private int day;
    private int month;
    private int year;
    public MyDate(String yyyy, String mm, String dd) {
        try {
        this.year = Integer.parseInt(yyyy);
        this.month = Integer.parseInt(mm);
        this.day = Integer.parseInt(dd);
        } catch (Exception e) {System.out.println(e);}
    }
    public int getDay() {
        return day;
    }
    public int getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }
    public String toString() {
        return String.format("%02d-%02d-%d", day, month, year);
    }
}
