package com.agiza.center.app;

public class Ambulance {

    private String latitude;
    private String longitude;
    private String name;
    private String contact;
    private String category;
    private String insurance;

    public Ambulance() {}

    public Ambulance(String name, String latitude, String longitude, String contact, String insurance, String category) {
        this.name = name;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.insurance = insurance;
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
    public String getInsurance(){
        return insurance;
    }
    public String getCategory(){
        return category;
    }


}
