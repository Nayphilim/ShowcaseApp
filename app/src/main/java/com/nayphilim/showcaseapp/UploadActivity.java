package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner uploadCategories;
    private ImageView uploadImageBox, imagePreview1,imagePreview2,imagePreview3,imagePreview4;
    private ProgressBar progressBar;

    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");
    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference AnalyticReference = FirebaseDatabase.getInstance().getReference("Analytics");
    private StorageReference StorageReference = FirebaseStorage.getInstance().getReference();
    private TextView cancelButton,publishButton, uploadImagePreviewNum, uploadImageText;
    private EditText titleBox, descriptionBox,creditsBox,repositoryBox, demoBox;
    private String Category;
    private FirebaseUser user;
    private String userID;

    ArrayList<Uri> imageUris = new ArrayList<>();
    ArrayList<String> imageUrls = new ArrayList<>();

    private String projectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadCategories = findViewById(R.id.uploadCategory);

        uploadImageBox = findViewById(R.id.uploadImageBox);
        imagePreview1 = findViewById(R.id.uploadImagePreview1);
        imagePreview2 = findViewById(R.id.uploadImagePreview2);
        imagePreview3 = findViewById(R.id.uploadImagePreview3);
        imagePreview4 = findViewById(R.id.uploadImagePreview4);

        uploadImagePreviewNum = findViewById(R.id.uploadImagePreviewNum);


        progressBar = findViewById(R.id.uploadProgressBar);

        cancelButton = findViewById(R.id.uploadCancel);
        publishButton = findViewById(R.id.uploadPublish);

        titleBox = findViewById(R.id.uploadProjectTitle);
        descriptionBox = findViewById(R.id.uploadDescription);
        creditsBox = findViewById(R.id.uploadCredits);
        repositoryBox = findViewById(R.id.uploadRepository);
        demoBox = findViewById(R.id.uploadDemo);

        projectId = ProjectReference.push().getKey();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        uploadImageText = findViewById(R.id.uploadImageText);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        uploadCategories.setAdapter(adapter);
        uploadCategories.setOnItemSelectedListener(this);

        uploadImageBox.setOnClickListener(this);
        imagePreview1.setOnClickListener(this);
        imagePreview2.setOnClickListener(this);
        imagePreview3.setOnClickListener(this);
        imagePreview4.setOnClickListener(this);


        publishButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==2 && resultCode == RESULT_OK && data != null){
            uploadImageText.setVisibility(View.GONE);
            imageUris.clear();
            imageUrls.clear();
            ClipData clipData = data.getClipData();

            if(clipData != null){
                uploadImageBox.setVisibility(View.GONE);

                for(int i = 0;i<clipData.getItemCount();i++){
                    if(i >= 8 ){
                        Toast.makeText(UploadActivity.this, "More than 8 images have been uploaded, please try again", Toast.LENGTH_LONG).show();
                        break;
                    }
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri imageUri = item.getUri();
                    imageUris.add(imageUri);


                }
                switch(imageUris.size()){
                    case 2:
                        imagePreview1.setImageURI(imageUris.get(0));
                        imagePreview2.setImageURI(imageUris.get(1));
                        imagePreview3.setBackgroundColor(0x00000000);
                        imagePreview3.setImageResource(R.drawable.ic_image_upload);
                        imagePreview1.setVisibility(View.VISIBLE);
                        imagePreview2.setVisibility(View.VISIBLE);
                        imagePreview3.setVisibility(View.VISIBLE);
                        imagePreview4.setVisibility(View.GONE);
                        uploadImagePreviewNum.setVisibility(View.GONE);
                        break;
                    case 3:
                        imagePreview1.setImageURI(imageUris.get(0));
                        imagePreview2.setImageURI(imageUris.get(1));
                        imagePreview3.setImageURI(imageUris.get(2));
                        imagePreview3.setBackgroundColor(getResources().getColor(R.color.black));
                        imagePreview4.setBackgroundColor(0x00000000);
                        imagePreview4.setImageResource(R.drawable.ic_image_upload);
                        imagePreview1.setVisibility(View.VISIBLE);
                        imagePreview2.setVisibility(View.VISIBLE);
                        imagePreview3.setVisibility(View.VISIBLE);
                        imagePreview4.setVisibility(View.VISIBLE);
                        uploadImagePreviewNum.setVisibility(View.GONE);
                        break;
                    case 4:
                        imagePreview1.setImageURI(imageUris.get(0));
                        imagePreview2.setImageURI(imageUris.get(1));
                        imagePreview3.setImageURI(imageUris.get(2));
                        imagePreview4.setImageURI(imageUris.get(3));
                        imagePreview3.setBackgroundColor(getResources().getColor(R.color.black));
                        imagePreview4.setBackgroundColor(getResources().getColor(R.color.black));
                        imagePreview1.setVisibility(View.VISIBLE);
                        imagePreview2.setVisibility(View.VISIBLE);
                        imagePreview3.setVisibility(View.VISIBLE);
                        imagePreview4.setVisibility(View.VISIBLE);
                        uploadImagePreviewNum.setVisibility(View.GONE);
                        break;
                    default:
                        imagePreview1.setImageURI(imageUris.get(0));
                        imagePreview2.setImageURI(imageUris.get(1));
                        imagePreview3.setImageURI(imageUris.get(2));
                        uploadImagePreviewNum.setText("+" + (imageUris.size()-3));
                        imagePreview1.setVisibility(View.VISIBLE);
                        imagePreview2.setVisibility(View.VISIBLE);
                        imagePreview3.setVisibility(View.VISIBLE);
                        imagePreview4.setVisibility(View.GONE);
                        uploadImagePreviewNum.setVisibility(View.VISIBLE);


                }
            }
            else{
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                uploadImageBox.setImageURI(imageUri);
            }

            //uploadImageBox.setImageURI(imageUri);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Category = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.uploadImageBox:
            case R.id.uploadImagePreview1:
            case R.id.uploadImagePreview2:
            case R.id.uploadImagePreview3:
            case R.id.uploadImagePreview4:
                keyboardController.hideKeyboard(this);
                resetPreviews();
                openImageGallery();
                break;
            case R.id.uploadPublish:
                keyboardController.hideKeyboard(this);
                publishProject();
                break;
            case R.id.uploadCancel:
                keyboardController.hideKeyboard(this);
                finish();
                break;
        }
    }

    private void resetPreviews() {
        uploadImagePreviewNum.setVisibility(View.GONE);
    }

    private void publishProject() {
        String title = titleBox.getText().toString().trim();
        String category = Category;
        String description = descriptionBox.getText().toString().trim();
        String credits = creditsBox.getText().toString().trim();
        String demo = demoBox.getText().toString().trim();
        String repository = repositoryBox.getText().toString().trim();
        final String repoPatternStringNoHttps = "^((https://)?github\\.com/).+/.+";
        final String repoPatternString = "^(https://github\\.com/).+/.+";
               /*
           Possibile Youtube urls.
           http://www.youtube.com/watch?v=WK0YhfKqdaI
           http://www.youtube.com/embed/WK0YhfKqdaI
           http://www.youtube.com/v/WK0YhfKqdaI
           http://www.youtube-nocookie.com/v/WK0YhfKqdaI?version=3&hl=en_US&rel=0
           http://www.youtube.com/watch?v=WK0YhfKqdaI
           http://www.youtube.com/watch?feature=player_embedded&v=WK0YhfKqdaI
           http://www.youtube.com/e/WK0YhfKqdaI
           http://youtu.be/WK0YhfKqdaI
        */
        final String youtubePatternString = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        Pattern repoPattern = Pattern.compile(repoPatternString);
        Pattern repoPatternNoHttps = Pattern.compile(repoPatternStringNoHttps);
        Pattern youtubePattern = Pattern.compile(youtubePatternString,Pattern.CASE_INSENSITIVE);
        Matcher matcher;

        if(imageUris.isEmpty()){
            uploadImageBox.requestFocus();
            return;
        }


        if(title.isEmpty()){
            titleBox.setError("Project Title is required");
            titleBox.requestFocus();
            return;
        }

        if(category.isEmpty()){
            uploadCategories.requestFocus();
            return;
        }

        if(description.isEmpty()){
            descriptionBox.setError("Project description is required");
            descriptionBox.requestFocus();
            return;
        }

        if(!demo.isEmpty()){
            if(!youtubePattern.matcher(demo).matches()){
                demoBox.setError("Please enter a valid youtube video link");
                demoBox.requestFocus();
                return;
            }
        }

        if(!repository.isEmpty()){
            if(repoPatternNoHttps.matcher(repository).matches()){
                repository = "https://" + repository;
            }
            if(!repoPattern.matcher(repository).matches()){
                repositoryBox.setError("Please enter a valid repository link");
                repositoryBox.requestFocus();
                return;
            }
        }





        for(Uri imageUri : imageUris){
            StorageReference fileRef = StorageReference.child(System.currentTimeMillis() + "."  + getFileExtension(imageUri));
            UploadTask uploadTask = fileRef.putFile(imageUri);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL

                            return fileRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            //if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                imageUrls.add(downloadUri.toString());

                                StringBuffer sb = new StringBuffer();
                                for(String imageUrl : imageUrls){
                                    sb.append(imageUrl);
                                    sb.append(",");

                                }
                                String imageUrlList = sb.toString();

                                ProjectReference.child(projectId).child("imageUrls").setValue(imageUrlList).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(imageUrls.size() == imageUris.size()){
                                            closeActivity();
                                        }
                                    }
                                });




                            //} else {
                                // Handle failures
                                // ...
                           // }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(@NonNull Object snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, "Failed to upload, please try again", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            });


        }



        uploadProject(title, category, description, credits, repository, demo);


    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void openImageGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("*/*");
        String[] extraMimeTypes = {"image/*"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(galleryIntent, 2);
    }



    private void uploadProject(String title, String category, String description, String credits, String repository, String demo){
        String projects = getIntent().getStringExtra("projectList");
            if(projects == null){
                projects = "";
            }
        //upload project details
        ProjectReference.child(projectId).child("title").setValue(title);
        ProjectReference.child(projectId).child("category").setValue(category);
        ProjectReference.child(projectId).child("description").setValue(description);
        ProjectReference.child(projectId).child("user").setValue(userID);
        ProjectReference.child(projectId).child("uploadDate").setValue(getCurrentDate());
        ProjectReference.child(projectId).child("visibility").setValue("public");
            if (!demo.isEmpty()) {
                ProjectReference.child(projectId).child("demoLink").setValue(demo);
            }
            if (!credits.isEmpty()) {
                ProjectReference.child(projectId).child("credits").setValue(credits);
            }
            if (!repository.isEmpty()) {

                ProjectReference.child(projectId).child("repository").setValue(repository);
            }


            if(projects == "" || projects == null){
                projects = projectId;
            }
            else {
                projects = projectId + "," + projects;
            }

            //attach project id to user
            UserReference.child(userID).child("projects").setValue(projects);

            //increment total uploaded project count
            AnalyticReference.child("totalProjectCount").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer totalProjects = mutableData.getValue(Integer.class);
                    if (totalProjects == null) {
                        return Transaction.success(mutableData);
                    }
                        mutableData.setValue(totalProjects + 1);


                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
            });





    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = formatter.format(date);
        return currentDate;
    }

    private synchronized void closeActivity(){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadActivity.this, "Project successfully uploaded", Toast.LENGTH_LONG).show();
            finish();
        }




}