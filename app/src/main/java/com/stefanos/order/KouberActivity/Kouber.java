package com.stefanos.order.KouberActivity;

public class Kouber {

    private int currentDay;
    private int currentMonth;
    private int currentYear;
    private int currentDateDays;
    private int quantityKouber;
    private String nameKouber;
    private double priceKouber;



    public Kouber() {
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public int getCurrentDateDays() {
        return currentDateDays;
    }

    public void setCurrentDateDays(int currentDateDays) {
        this.currentDateDays = currentDateDays;
    }

    public int getQuantityKouber() {
        return quantityKouber;
    }

    public void setQuantityKouber(int quantityKouber) {
        this.quantityKouber = quantityKouber;
    }

    public String getNameKouber() {
        return nameKouber;
    }

    public void setNameKouber(String nameKouber) {
        this.nameKouber = nameKouber;
    }

    public double getPriceKouber() {
        return priceKouber;
    }

    public void setPriceKouber(double priceKouber) {
        this.priceKouber = priceKouber;
    }
}
