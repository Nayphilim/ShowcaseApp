package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class projectViewAcitivty extends AppCompatActivity implements View.OnClickListener {

   // private ImageSlider imgSlider;
    private String userName;
    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");
    private StorageReference StorageReference = FirebaseStorage.getInstance().getReference();
    private List<SlideModel> projectImages = new ArrayList<>();
    private String ProjectId, projectRepository;
    private Project project;
    private TextView projectTitle, projectDate, projectUsername, projectCategory, projectDescription, projectCredits, projectSourceTitle;
    private RelativeLayout projectRepositoryButtonArea, projectSourceTitleArea, projectCreditsArea, projectCreditsTitleArea;
    private ImageView projectRepositoryButton;
    private ImageButton backArrow;
    private String viewerID;

    private ImageSlider imgSlider;

    //String[] sampleImage = {"content://com.android.providers.media.documents/document/image%3A189"};


    private User userProfile = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view_acitivty);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

       // imgSlider = findViewById(R.id.image_slider);
        projectTitle = findViewById(R.id.projViewTitle);
        backArrow = findViewById(R.id.projViewBackArrow);
        projectDate = findViewById(R.id.projViewDate);
        projectUsername = findViewById(R.id.projViewUser);
        projectCategory = findViewById(R.id.projViewCategory);
        projectDescription = findViewById(R.id.projViewDescription);
        projectCredits = findViewById(R.id.projViewCredits);
        projectRepositoryButton = findViewById(R.id.projViewGithub);
        projectSourceTitle = findViewById(R.id.projViewSourceTitle);
        projectRepositoryButtonArea = findViewById(R.id.projViewGithubArea);
        projectSourceTitleArea = findViewById(R.id.projViewSourceTitleArea);
        projectCreditsArea = findViewById(R.id.projViewCreditsArea);
        projectCreditsTitleArea = findViewById(R.id.projViewCreditsTitleArea);
        imgSlider = findViewById(R.id.image_slider);


//        if (imgSlider != null) {
//            imgSlider.setPageCount(sampleImage.length);
//            imgSlider.setImageListener(imageListener);
//        }

        backArrow.setOnClickListener(this);
        projectRepositoryButton.setOnClickListener(this);

        project = new Project();


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            ProjectId = extras.getString("selectedProjectId");
            viewerID = extras.getString("viewerID");
        }


        getProjectDetails();

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
                        projectCreditsTitleArea.setVisibility(View.VISIBLE);
                        projectCreditsArea.setVisibility(View.VISIBLE);
                    }
                    if(snapshot.child("repository").getValue() != null) {
                        project.setRepository(snapshot.child("repository").getValue().toString().trim());
                        setRepositoryUrl(project.getRepository());
                        projectRepositoryButtonArea.setVisibility(View.VISIBLE);
                        projectSourceTitleArea.setVisibility(View.VISIBLE);
                    }

                    //set project details here
                    projectTitle.setText(project.getTitle());
                    projectDate.setText(project.getUploadDate());
                    projectCategory.setText(project.getCategory());
                    projectDescription.setText(project.getDescription()); 
                    projectCredits.setText(project.getCredits());

                    //WIP doesnt not properly authenticate that its not the current users project
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid() != viewerID){
                        if(snapshot.child("projectViews").getValue() != null) {
                            int currentViews = Integer.parseInt(snapshot.child("projectViews").getValue().toString());
                            ProjectReference.child(ProjectId).child("projectViews").setValue(currentViews + 1);
                        }
                        else{
                            ProjectReference.child(ProjectId).child("projectViews").setValue(1);
                        }
                    }

                    UserReference.child(project.getUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userProfile.setFirstName(snapshot.child("firstName").getValue().toString().trim());
                            userProfile.setLastName(snapshot.child("lastName").getValue().toString().trim());
                            projectUsername.setText(userProfile.getFirstName() + " " + userProfile.getLastName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                   populateSlides(project.getImageUrls());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void setRepositoryUrl(String repository) {
        projectRepository = repository;
    }

    private void getUsername(String user) {


    }

    private void populateSlides(String imageUrlList) {
        String[] imageUrls = imageUrlList.split(",");

        for(String url : imageUrls){
            Uri uri = Uri.parse(url);
            projectImages.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
        }

        if(projectImages != null) {
            imgSlider.setImageList(projectImages);
        }














//        String[] testUrl = {"content://com.android.providers.media.documents/document/image%3A189"};
////        ArrayList<Uri> imageUris = new ArrayList<Uri>();
////
////        for(String url : imageUrls){
////            imageUris.add(Uri.parse(url));
////        }
//
//        ImageListener firebaseImages = new ImageListener() {
//            @Override
//            public void setImageForPosition(int position, ImageView imageView) {
//                Glide.with(getApplicationContext()).load(Uri.parse(testUrl[position])).into(imageView);
//            }
//        };
//
//        imgSlider.setPageCount(imageUrls.length);
//        imgSlider.setImageListener(firebaseImages);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.projViewBackArrow:
                finish();
                break;
            case R.id.projViewGithub:
                openGithubBrowser();
                break;
        }
    }

    private void openGithubBrowser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(projectRepository));
        startActivity(intent);
    }

//    ImageListener imageListener = new ImageListener() {
//
//
//        @Override
//        public void setImageForPosition(int position, ImageView imageView) {
//            Glide.with(getApplicationContext()).load(Uri.parse(sampleImage[position])).into(imageView);
//        }
//    };
    }


