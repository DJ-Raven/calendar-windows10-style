package raven.calendar.model;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Raven
 */
public class ModelDate {

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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public ModelDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public ModelDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DATE);
    }

    public ModelDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DATE);
    }

    private int year;
    private int month;
    private int day;

    @Override
    public String toString() {
        return day + "-" + month + "-" + year;
    }

    public boolean compareTo(ModelDate date) {
        return date.year == year && date.month == month && date.day == day;
    }

    public Date toDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public ModelMonth toMonth() {
        return new ModelMonth(year, month);
    }

    public boolean isToday() {
        return compareTo(new ModelDate());
    }
}
