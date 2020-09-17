package com.agiza.center.app;

public class Info {

    private String facebook;
    private String twitter;
    private String instagram;
    private String contact;
    private String email;
    private String website;
    private String location;

    public Info() {}

    public Info(String facebook, String twitter, String instagram, String contact, String email, String website, String location) {
        this.facebook = facebook;
        this.contact = contact;
        this.twitter = twitter;
        this.instagram = instagram;
        this.email = email;
        this.website = website;
        this.location = location;
    }

    public String getFacebook(){
        return facebook;
    }
    public String getTwitter(){
        return twitter;
    }
    public String getInstagram(){
        return instagram;
    }
    public String getContact(){
        return contact;
    }
    public String getEmail(){
        return email;
    }
    public String getWebsite(){
        return website;
    }
    public String getLocation(){
        return location;
    }


}
