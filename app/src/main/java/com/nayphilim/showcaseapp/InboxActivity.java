package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class InboxActivity extends AppCompatActivity implements View.OnClickListener, AdapterFeedbackFeed.OnFeedbackListener {

    private FirebaseUser user;
    private String userID;
    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference FeedbackReference = FirebaseDatabase.getInstance().getReference("Feedback");

    private RecyclerView recyclerView;
    private ArrayList<FeedbackFeed> feedbackFeedArrayList = new ArrayList<>();
    private AdapterFeedbackFeed adapterFeedbackFeed;

    private ImageButton backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        recyclerView = findViewById(R.id.inboxUnreadFeedbackList);
        backArrow = findViewById(R.id.feedbackBackArrow);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapterFeedbackFeed = new AdapterFeedbackFeed(this, feedbackFeedArrayList, this);
        recyclerView.setAdapter(adapterFeedbackFeed);

        backArrow.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        UserReference.child(userID).child("unreadFeedback").setValue(false);

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
                        FeedbackFeed feedbackFeed = new FeedbackFeed(ds.getKey(), ds.child("projectTitle").getValue().toString(),ds.child("projectID").getValue().toString(), ds.child("submitterName").getValue().toString(), ds.child("feedbackDateTime").getValue().toString(), Uri.parse(ds.child("projectThumbnail").getValue().toString()), ds.child("feedbackDialog").getValue().toString());
                        feedbackFeedArrayList.add(feedbackFeed);

                    }
                    Collections.reverse(feedbackFeedArrayList);
                    adapterFeedbackFeed.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feedbackBackArrow:
                finish();
                break;
        }
    }

    @Override
    public void onFeedbackClick(int position) {
        FeedbackFeed selectedFeedback =  feedbackFeedArrayList.get(position);
        String projectId = selectedFeedback.getProjectId();

        Intent intent = new Intent(this, projectActivity.class);
        intent.putExtra("selectedProjectId", projectId);
        startActivity(intent);
    }
}