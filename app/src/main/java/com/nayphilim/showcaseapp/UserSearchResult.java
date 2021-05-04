package com.nayphilim.showcaseapp;

public class UserSearchResult {
    private String Name = "";
    private String  Specialization= "";
    private String Location= "";
    private String ShowLocation;

    public UserSearchResult(String name, String specialization, String location, String showLocation) {
        Name = name;
        Specialization = specialization;
        Location = location;
        ShowLocation = showLocation;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public void setSpecialization(String specialization) {
        Specialization = specialization;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getShowLocation() {
        return ShowLocation;
    }

    public void setShowLocation(String showLocation) {
        ShowLocation = showLocation;
    }
}
