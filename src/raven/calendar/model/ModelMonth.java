package raven.calendar.model;

import java.util.Calendar;

/**
 *
 * @author Raven
 */
public class ModelMonth {

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public ModelMonth(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public ModelMonth() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
    }

    private int year;
    private int month;
}
