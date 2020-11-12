package com.stefanos.order.SQLiteDatabaseForPrinters;

public class IpAdressPrinter {

    private String ipAdress;
    private String port;
    private int id;

    public IpAdressPrinter() {

    }

    public IpAdressPrinter(String ipAdress, String port, int id) {
        this.ipAdress =ipAdress;
        this.port = port;
        this.id = id;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
