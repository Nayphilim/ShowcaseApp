package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Feedback {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference FeedbackReference = FirebaseDatabase.getInstance().getReference("Feedback");
    private static DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private  static String SubmitterID, ReceiverID, FeedbackDialog, FeedbackID;
    private static boolean successful;


    public static boolean submitFeedback(String submitterID,String receiverID, String feedbackDialog) {
        SubmitterID = submitterID;
        ReceiverID = receiverID;
        FeedbackDialog = feedbackDialog;
        FeedbackID = database.getReference("Feedback").push().getKey();
        UserReference.child(SubmitterID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("firstName").getValue() != null && snapshot.child("lastName").getValue() != null){
                    String submitterName = snapshot.child("firstName").getValue().toString() + " " + snapshot.child("lastName").getValue().toString();
                    FeedbackReference.child(FeedbackID).child("submitterID").setValue(SubmitterID);
                    FeedbackReference.child(FeedbackID).child("submitterName").setValue(submitterName);
                    FeedbackReference.child(FeedbackID).child("receiverID").setValue(ReceiverID);
                    FeedbackReference.child(FeedbackID).child("feedbackDialog").setValue(FeedbackDialog);
                    FeedbackReference.child(FeedbackID).child("feedbackDateTime").setValue(getCurrentDateTime());
                    successful = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                successful = false;
            }
        });
        return successful;
    }


    static public String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentDate = formatter.format(date);
        return currentDate;
    }
}
