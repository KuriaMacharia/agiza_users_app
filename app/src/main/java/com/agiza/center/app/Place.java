package com.agiza.center.app;

import java.util.Date;

public class Place {

    private String address;
    private String name;
    private Date time;
    private String category;
    private String status;

    public Place() {}

    public Place(String address, String name, Date time, String category, String status) {
        this.address = address;
        this.name = name;
        this.time = time;
        this.category = category;
        this.status = status;
    }

    public String getAddress(){
        return address;
    }
    public String getName(){
        return name;
    }
    public Date getTime(){
        return time;
    }
    public String getCategory(){
        return category;
    }
    public String getStatus(){
        return status;
    }
}

