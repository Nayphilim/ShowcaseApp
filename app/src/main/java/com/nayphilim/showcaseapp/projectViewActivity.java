package com.nayphilim.showcaseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class projectViewActivity extends AppCompatActivity implements View.OnClickListener {

   // private ImageSlider imgSlider;
    private String userName;

    private  FirebaseUser user;

    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");

    private StorageReference StorageReference = FirebaseStorage.getInstance().getReference();
    private List<SlideModel> projectImages = new ArrayList<>();
    private String ProjectId, projectRepository;
    private Project project;
    private TextView projectTitle, projectDate, projectUsername, projectCategory, projectDescription, projectCredits, projectSourceTitle, projectDemo;
    private RelativeLayout projectRepositoryButtonArea, projectSourceTitleArea, projectCreditsArea, projectCreditsTitleArea, projectDemoTitleArea,projectDemoArea;
    private ImageView projectRepositoryButton;
    private ImageButton backArrow;
    private String viewerID;
    private ImageSlider imgSlider;
    private FloatingActionButton fab;
    private User userProfile = new User();
    private String[] imageUrls;

    private String feedbackDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);
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
        projectDemo = findViewById(R.id.projViewDemo);
        projectDemoArea = findViewById(R.id.projViewDemoArea);
        projectDemoTitleArea = findViewById(R.id.projViewDemoTitleArea);
        imgSlider = findViewById(R.id.image_slider);
        fab = findViewById(R.id.floating_action_button);

        imgSlider.setItemChangeListener(new ItemChangeListener() {
                                            @Override
                                            public void onItemChanged(int i) {
                                                Session.itrInteractions();
                                            }
                                        });
        imgSlider.setItemClickListener(new ItemClickListener() {
                                           @Override
                                           public void onItemSelected(int i) {
                                               Session.itrInteractions();
                                           }
                                       });


//        if (imgSlider != null) {
//            imgSlider.setPageCount(sampleImage.length);
//            imgSlider.setImageListener(imageListener);
//        }

        backArrow.setOnClickListener(this);
        projectRepositoryButton.setOnClickListener(this);
        fab.setOnClickListener(this);
        projectDemo.setOnClickListener(this);
        projectUsername.setOnClickListener(this);

        project = new Project();

        user = FirebaseAuth.getInstance().getCurrentUser();


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
                    if(snapshot.child("demoLink").getValue() != null) {
                        project.setDemo(snapshot.child("demoLink").getValue().toString().trim());
                        projectDemoTitleArea.setVisibility(View.VISIBLE);
                        projectDemoArea.setVisibility(View.VISIBLE);
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
                    projectDemo.setText(project.getDemo());

                        if(snapshot.child("projectViews").getValue() != null) {
                            int currentViews = Integer.parseInt(snapshot.child("projectViews").getValue().toString());
                            ProjectReference.child(ProjectId).child("projectViews").setValue(currentViews + 1);
                        }
                        else{

                            ProjectReference.child(ProjectId).child("projectViews").setValue(1);
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


    private void populateSlides(String imageUrlList) {
        imageUrls = imageUrlList.split(",");

        for(String url : imageUrls){
            Uri uri = Uri.parse(url);
            projectImages.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
        }

        if(projectImages != null) {
            imgSlider.setImageList(projectImages);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.projViewBackArrow:
                Session.itrInteractions();
                finish();
                break;
            case R.id.projViewGithub:
                Session.itrInteractions();
                openGithubBrowser();
                break;
            case R.id.floating_action_button:
                Session.itrInteractions();
                openFeedbackDialog();
                break;
            case R.id.projViewDemo:
                Session.itrInteractions();
                openDemoLink();
                break;
            case R.id.projViewUser:
                Session.itrInteractions();
                openUserProfile();
                break;
        }
    }

    private void openUserProfile() {
//        Intent intent = new Intent(this, profileViewFragment.class);
//        intent.putExtra("userId", project.getUser());
//        startActivity(intent);

//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        profileViewFragment profileViewFragment = com.nayphilim.showcaseapp.profileViewFragment.newInstance(project.getUser());
//        ft.replace(R.id.fragment, profileViewFragment);
//        ft.addToBackStack(null);
//        ft.commit();


    }
    private void openFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave some feedback?");

        // Set up the input
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.feedback_box, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);

        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Session.itrInteractions();
                feedbackDialog = input.getText().toString();
                Feedback.submitFeedback(user.getUid(),project.getUser(),feedbackDialog, ProjectId, project.getTitle(), Uri.parse(imageUrls[0]) );
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void openDemoLink() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(project.getDemo()));
        startActivity(intent);
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


