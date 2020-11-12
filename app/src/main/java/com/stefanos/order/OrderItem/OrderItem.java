package com.stefanos.order.OrderItem;

public class OrderItem {

    private int quantity;
    private String nameItem;
    private double priceItem;
    private int itemID;
    private String typeItem;

    public String getTypeItem() {
        return typeItem;
    }

    public void setTypeItem(String typeItem) {
        this.typeItem = typeItem;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    private String extra;
    private String xwris;
    private boolean enabledPartialPayment;

    public boolean isEnabledPartialPayment() {
        return enabledPartialPayment;
    }

    public void setEnabledPartialPayment(boolean enabledPartialPayment) {
        this.enabledPartialPayment = enabledPartialPayment;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getXwris() {
        return xwris;
    }

    public void setXwris(String xwris) {
        this.xwris = xwris;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    public double getPriceItem() {
        return priceItem;
    }

    public void setPriceItem(double priceItem) {
        this.priceItem = priceItem;
    }


}
