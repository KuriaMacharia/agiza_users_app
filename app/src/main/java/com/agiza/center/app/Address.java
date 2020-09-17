package com.agiza.center.app;

public class Address {

    private String county;
    private String region;
    private String road;
    private String number;
    private String latitude;
    private String longitude;
    private String fulladdress;

    public Address() {}

    public Address(String county, String region, String road, String number, String latitude, String longitude, String fulladdress) {
        this.county = county;
        this.region = region;
        this.road = road;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fulladdress=fulladdress;
    }

    public String getCounty(){
        return county;
    }
    public String getRegion(){
        return region;
    }
    public String getRoad(){
        return road;
    }
    public String getNumber(){
        return number;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getFulladdress(){
        return fulladdress;
    }

}

