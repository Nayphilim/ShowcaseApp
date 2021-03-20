package com.nayphilim.showcaseapp;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {
    private  String firstName,lastName,email, location, specialization,showLocation, githubLink;
    private static String projects;


    public User(){

    }

    public User(String email, String firstName, String githubLink, String lastName,String location, String projects, String showLocation, String specialization){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.showLocation = showLocation;
        this.specialization = specialization;
        this.location = location;
        this.projects = projects;
        this.githubLink = githubLink;
    }

    public static String getProjectList(String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("projects").getValue() != null) {
                    projects = snapshot.child("projects").getValue().toString().trim();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return projects;

    }

    public  void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public  void setEmail(String email) {
        this.email = email;
    }

    public  void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public  String getEmail() {
        return email;
    }

    public  String getFirstName() {
        return firstName;
    }

    public  String getLastName() {
        return lastName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getShowLocation() {
        return showLocation;
    }

    public void setShowLocation(String showLocation) {
        this.showLocation = showLocation;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }
}
