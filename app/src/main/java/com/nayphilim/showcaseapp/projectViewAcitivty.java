package com.nayphilim.showcaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class projectViewAcitivty extends AppCompatActivity {

    private ImageSlider imgSlider;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private List<SlideModel> projectImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view_acitivty);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imgSlider = findViewById(R.id.image_slider);

        populateSlides();

    }

    private void populateSlides() {

    }
}