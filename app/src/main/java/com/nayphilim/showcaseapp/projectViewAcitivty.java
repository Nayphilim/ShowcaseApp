package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class projectViewAcitivty extends AppCompatActivity implements View.OnClickListener {

    private ImageSlider imgSlider;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");
    private List<SlideModel> projectImages = new ArrayList<>();
    private String ProjectId;
    private Project project;
    private TextView projectTitle;
    private ImageButton backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view_acitivty);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imgSlider = findViewById(R.id.image_slider);
        projectTitle = findViewById(R.id.projViewTitle);
        backArrow = findViewById(R.id.projViewBackArrow);

        backArrow.setOnClickListener(this);

        project = new Project();


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            ProjectId = extras.getString("selectedProjectId");
        }

        getProjectDetails();

    }

    private void populateProjectDetails() {
        projectTitle.setText(project.getTitle());
    }

    private void getProjectDetails() {
        if (ProjectId != null) {

            ProjectReference.child(ProjectId).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    project.setTitle(snapshot.child("title").getValue().toString().trim());
                    project.setCategory(snapshot.child("category").getValue().toString().trim());
                    project.setDescription(snapshot.child("description").getValue().toString().trim());
                    project.setUploadDate(snapshot.child("uploadDate").getValue().toString().trim());
                    project.setImageUrls(snapshot.child("imageUrls").getValue().toString().trim());
                    project.setUser(snapshot.child("user").getValue().toString().trim());
                    if(snapshot.child("credits").getValue() != null) {
                        project.setCredits(snapshot.child("credits").getValue().toString().trim());
                    }
                    if(snapshot.child("repository").getValue() != null) {
                        project.setRepository(snapshot.child("repository").getValue().toString().trim());
                    }

                    //set project details here
                    projectTitle.setText(project.getTitle());
                    populateSlides(project.getImageUrls());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            projectTitle.setText("error");
        }
    }

    private void populateSlides(String imageUrlList) {
        String[] imageUrls = imageUrlList.split(",");

        for(String url : imageUrls){
            projectImages.add(new SlideModel(url, ScaleTypes.FIT));
        }

        if(projectImages != null) {
            imgSlider.setImageList(projectImages);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.projViewBackArrow:
                finish();
        }
    }
}