package com.agiza.center.app;

public class Kovid {

    private String county;
    private String name;
    private String phone;

    public Kovid() {}

    public Kovid(String name, String county, String phone) {
        this.name = name;
        this.phone = phone;
        this.county = county;
    }

    public String getName(){
        return name;
    }
    public String getCounty(){
        return county;
    }
    public String getPhone(){
        return phone;
    }


}
