package com.stefanos.order.TzirosActivity;

public class TzirosItem {

    private int dayOfTziros;
    private double sum;
    private int monthOfTziros;
    private int yearOfTziros;

    public int getTzirosDateDays() {
        return tzirosDateDays;
    }

    public void setTzirosDateDays(int tzirosDateDays) {
        this.tzirosDateDays = tzirosDateDays;
    }

    private int tzirosDateDays;



    public int getDayOfTziros() {
        return dayOfTziros;
    }

    public void setDayOfTziros(int dayOfTziros) {
        this.dayOfTziros = dayOfTziros;
    }

    public int getMonthOfTziros() {
        return monthOfTziros;
    }

    public void setMonthOfTziros(int monthOfTziros) {
        this.monthOfTziros = monthOfTziros;
    }

    public int getYearOfTziros() {
        return yearOfTziros;
    }

    public void setYearOfTziros(int yearOfTziros) {
        this.yearOfTziros = yearOfTziros;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
