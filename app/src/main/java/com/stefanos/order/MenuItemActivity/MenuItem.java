package com.stefanos.order.MenuItemActivity;

public class MenuItem {

    private int id;
    private String nameMenuItem;
    private String type;
    private double price;
    private int typeId;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MenuItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameMenuItem() {
        return nameMenuItem;
    }

    public void setNameMenuItem(String nameMenuItem) {
        this.nameMenuItem = nameMenuItem;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
