package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Session {
    static String AccountID;
    static String AccountViewedID;
    static int numInteractions;
    static Long startTime;
    static Long finishTime;
    static private DatabaseReference SessionReference = FirebaseDatabase.getInstance().getReference("Sessions");
    static private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    static FirebaseDatabase database = FirebaseDatabase.getInstance();

    static public void session(){

    }

    static public void startSession(String accountID, String accountViewedID){
        startTime = System.nanoTime();
        AccountID = accountID;
        AccountViewedID = accountViewedID;
    }

    static public void endSession() {
        if (startTime != null) {
            finishTime = System.nanoTime();
            Long elapsedTime = finishTime - startTime; //sessions length in seconds
            Long sessionLength = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
            String sessionID = database.getReference("Sessions").push().getKey();


            SessionReference.child(sessionID).child("accountID").setValue(AccountID);
            SessionReference.child(sessionID).child("accountViewedID").setValue(AccountViewedID);
            SessionReference.child(sessionID).child("interactions").setValue(numInteractions);
            SessionReference.child(sessionID).child("sessionLength").setValue(sessionLength);
            SessionReference.child(sessionID).child("sessionDateTime").setValue(getCurrentDateTime());



            updatePageViews();
        }
    }

    static public void updatePageViews() {
        UserReference.child(AccountViewedID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.child("pageViews").getValue() != null){
                    int currentViews = Integer.parseInt(snapshot.child("pageViews").getValue().toString());
                    itrPageViews(currentViews);
                }
                else{
                    setPageViews();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    static public void itrInteractions(){
        numInteractions += 1;
    }

    static public String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentDate = formatter.format(date);
        return currentDate;
    }

    static public void setPageViews(){
        UserReference.child(AccountViewedID).child("pageViews").setValue(1);
    }

    static public void itrPageViews(int currentViews){
        UserReference.child(AccountViewedID).child("pageViews").setValue(currentViews+1);
    }


}
