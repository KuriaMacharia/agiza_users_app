package com.agiza.center.app;

public class Hospital {

    private String latitude;
    private String longitude;
    private String name;
    private String contact;
    private String phone;

    public Hospital() {}

    public Hospital(String name, String latitude, String longitude, String contact, String phone) {
        this.name = name;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone= phone;
    }

    public String getName(){
        return name;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getContact(){
        return contact;
    }
    public String getPhone(){
        return phone;
    }
}
