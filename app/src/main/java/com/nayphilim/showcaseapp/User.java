package com.nayphilim.showcaseapp;


public class User {
    private  String FirstName,LastName,Email;

    public User(){

    }

    public User(String firstName, String lastName,String email){
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Email = email;
    }

    public  void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public  void setEmail(String email) {
        this.Email = email;
    }

    public  void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public  String getEmail() {
        return Email;
    }

    public  String getFirstName() {
        return FirstName;
    }

    public  String getLastName() {
        return LastName;
    }
}
