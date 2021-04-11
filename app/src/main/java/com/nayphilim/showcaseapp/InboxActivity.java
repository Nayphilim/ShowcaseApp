package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {

    private FirebaseUser user;
    private String userID;
    private DatabaseReference FeedbackReference = FirebaseDatabase.getInstance().getReference("Feedback");

    private RecyclerView recyclerView;
    private ArrayList<FeedbackFeed> feedbackFeedArrayList = new ArrayList<>();
    private AdapterFeedbackFeed adapterFeedbackFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        recyclerView = findViewById(R.id.inboxUnreadFeedbackList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapterFeedbackFeed = new AdapterFeedbackFeed(this, feedbackFeedArrayList);
        recyclerView.setAdapter(adapterFeedbackFeed);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        populateFeedbackList();
    }

    private void populateFeedbackList() {
        feedbackFeedArrayList.clear();

        Query query = FeedbackReference.orderByChild("receiverID").equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        FeedbackFeed feedbackFeed = new FeedbackFeed(ds.getKey(), ds.child("projectTitle").getValue().toString(), ds.child("submitterName").getValue().toString(), ds.child("feedbackDateTime").getValue().toString(), Uri.parse(ds.child("projectThumbnail").getValue().toString()), ds.child("feedbackDialog").getValue().toString());
                        feedbackFeedArrayList.add(feedbackFeed);
                        adapterFeedbackFeed.notifyItemInserted(feedbackFeedArrayList.size());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}