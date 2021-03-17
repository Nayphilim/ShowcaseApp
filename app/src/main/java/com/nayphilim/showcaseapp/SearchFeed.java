package com.nayphilim.showcaseapp;

public class SearchFeed {
    String UserName, Location, Specialization;

    public SearchFeed(String userName, String location, String specialization){
        this.UserName = userName;
        this.Location = location;
        this.Specialization = specialization;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public void setSpecialization(String specialization) {
        Specialization = specialization;
    }
}
