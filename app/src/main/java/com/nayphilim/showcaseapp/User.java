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
    private  String FirstName,LastName,Email;
    private static String ProjectList;
    public User(){

    }

    public User(String firstName, String lastName,String email){
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Email = email;
    }

    public static String getProjectList(String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("projects").getValue() != null) {
                    ProjectList = snapshot.child("projects").getValue().toString().trim();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return ProjectList;

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
