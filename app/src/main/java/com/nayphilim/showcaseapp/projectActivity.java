package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asura.library.posters.Poster;
import com.asura.library.posters.RemoteImage;
import com.asura.library.posters.RemoteVideo;
import com.asura.library.views.PosterSlider;
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

public class projectActivity extends AppCompatActivity implements View.OnClickListener {

   // private ImageSlider imgSlider;
    private String userName;

    private  FirebaseUser user;

    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");

    private StorageReference StorageReference = FirebaseStorage.getInstance().getReference();
    private List<SlideModel> projectImages = new ArrayList<>();
    private String ProjectId, projectRepository;
    private Project project;
    private TextView projectTitle, projectDate, projectUsername, projectCategory, projectDescription, projectCredits, projectSourceTitle;
    private RelativeLayout projectRepositoryButtonArea, projectSourceTitleArea, projectCreditsArea, projectCreditsTitleArea;
    private ImageView projectRepositoryButton;
    private ImageButton backArrow, projectOptions;
    private String viewerID;
    private ImageSlider imgSlider;
    private User userProfile = new User();
    private String[] imageUrls;
    private PosterSlider posterSlider;

    private String feedbackDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imgSlider = findViewById(R.id.image_slider);
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
        projectOptions = findViewById(R.id.projectViewOptions);
        imgSlider = findViewById(R.id.image_slider);

        //posterSlider = findViewById(R.id.poster_slider);

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
        projectOptions.setOnClickListener(this);

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

//       String videoTest =  "gs://showcaseapp-a59c0.appspot.com/2021-04-05 16-23-50.mp4";
//       String imgTest = "gs://showcaseapp-a59c0.appspot.com/1616704876497.jpg";
//
//        List<Poster> posters=new ArrayList<>();
//        posters.add(new RemoteVideo(Uri.parse("gs://showcaseapp-a59c0.appspot.com/2021-04-05 16-23-50.mp4")));
//        //posters.add(new RemoteImage(imgTest));
//        posterSlider.setPosters(posters);


        imageUrls = imageUrlList.split(",");

        for(String url : imageUrls){
            Uri uri = Uri.parse(url);
            projectImages.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_INSIDE));
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
            case R.id.projectViewOptions:
                openOptions();
                break;
        }
    }

    private void openOptions() {
        PopupMenu options = new PopupMenu(this, projectOptions);
        options.getMenuInflater().inflate(R.menu.project_options_popup, options.getMenu());
        options.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case(R.id.optionsDelete):
                        checkDeleteProject();
                        return true;
                }

                return false;
            }
        });

        options.show();
    }

    private void checkDeleteProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you would like to delete this project?");

        // Set up the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProject();
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

    private void deleteProject() {
        ProjectReference.child(ProjectId).child("visibility").setValue("hidden");
    }

    private void openFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave some feedback?");

        // Set up the input
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.feedback_box, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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


