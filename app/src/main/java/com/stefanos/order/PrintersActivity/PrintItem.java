package com.stefanos.order.PrintersActivity;

public class PrintItem {


    String namePrint;
    String extraPrint;
    String xwrisPrint;
    double priceItem;
    int quantityPrint;



    public String getNamePrint() {
        return namePrint;
    }

    public double getPriceItem() {
        return priceItem;
    }

    public void setPriceItem(double priceItem) {
        this.priceItem = priceItem;
    }

    public void setNamePrint(String namePrint) {
        this.namePrint = namePrint;
    }

    public String getExtraPrint() {
        return extraPrint;
    }

    public void setExtraPrint(String extraPrint) {
        this.extraPrint = extraPrint;
    }

    public String getXwrisPrint() {
        return xwrisPrint;
    }

    public void setXwrisPrint(String xwrisPrint) {
        this.xwrisPrint = xwrisPrint;
    }

    public int getQuantityPrint() {
        return quantityPrint;
    }

    public void setQuantityPrint(int quantityPrint) {
        this.quantityPrint = quantityPrint;
    }
}
