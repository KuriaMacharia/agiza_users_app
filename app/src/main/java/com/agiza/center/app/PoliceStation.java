package com.agiza.center.app;

public class PoliceStation {

    private String latitude;
    private String longitude;
    private String name;
    private String contact;

    public PoliceStation() {}

    public PoliceStation(String name, String latitude, String longitude, String contact) {
        this.name = name;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
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


}
