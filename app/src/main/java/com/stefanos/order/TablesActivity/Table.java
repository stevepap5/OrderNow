package com.stefanos.order.TablesActivity;

public class Table {

    private String table;
    private int priority;
    private String status;
    private String orofosName;

    public String getOrofosName() {
        return orofosName;
    }

    public void setOrofosName(String orofosName) {
        this.orofosName = orofosName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Table(String table, int priority, String statusPayment) {
        this.table = table;
        this.priority = priority;
        this.status =statusPayment;

    }

    public Table() {
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


}
