package com.agiza.center.app;

public class Users {

    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    public Users() {}

    public Users(String firstname, String lastname, String email, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
    }

    public String getFirstname(){
        return firstname;
    }
    public String getLastname(){
        return lastname;
    }
    public String getEmail(){
        return email;
    }
    public String getPhone(){
        return phone;
    }

}
