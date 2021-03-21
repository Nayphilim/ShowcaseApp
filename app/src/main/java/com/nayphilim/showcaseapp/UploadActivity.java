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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner uploadCategories;
    private ImageView uploadImageBox;
    private ProgressBar progressBar;

    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");
    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private StorageReference StorageReference = FirebaseStorage.getInstance().getReference();
    private TextView cancelButton,publishButton;
    private EditText titleBox, descriptionBox,creditsBox,repositoryBox;
    private String Category;
    private FirebaseUser user;
    private String userID;
    private String projects;
    List<Uri> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadCategories = findViewById(R.id.uploadCategory);

        uploadImageBox = findViewById(R.id.uploadImageBox);

        progressBar = findViewById(R.id.uploadProgressBar);

        cancelButton = findViewById(R.id.uploadCancel);
        publishButton = findViewById(R.id.uploadPublish);

        titleBox = findViewById(R.id.uploadProjectTitle);
        descriptionBox = findViewById(R.id.uploadDescription);
        creditsBox = findViewById(R.id.uploadCredits);
        repositoryBox = findViewById(R.id.uploadRepository);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        uploadCategories.setAdapter(adapter);
        uploadCategories.setOnItemSelectedListener(this);

        uploadImageBox.setOnClickListener(this);

        publishButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==2 && resultCode == RESULT_OK && data != null){
            ClipData clipData = data.getClipData();

            if(clipData != null){
                for(int i = 0;i<clipData.getItemCount();i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri imageUri = item.getUri();
                    imageUrls.add(imageUri);

                }
            }
            else{
                Uri imageUri = data.getData();
                imageUrls.add(imageUri);
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
                uploadImage();
                break;
            case R.id.uploadPublish:
                publishProject();
                break;
            case R.id.uploadCancel:
                finish();
                break;
        }
    }

    private void publishProject() {
        String title = titleBox.getText().toString().trim();
        String category = Category;
        String description = descriptionBox.getText().toString().trim();
        String credits = creditsBox.getText().toString().trim();
        String repository = repositoryBox.getText().toString().trim();
        final String RepoPattern = "^(https://github\\.com/).+/.+";
        Pattern pattern = Pattern.compile(RepoPattern);
        Matcher matcher;

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

        if(!repository.isEmpty()){
            if(!pattern.matcher(repository).matches()){
                repositoryBox.setError("Please enter a valid repository link");
                repositoryBox.requestFocus();
                return;
            }
        }



        for(Uri imageUri : imageUrls){
            StorageReference fileRef = StorageReference.child(System.currentTimeMillis() + "."  + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, "Upload Failed, please try again", Toast.LENGTH_LONG);
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            });
        }

        uploadProject(title, category, description, credits, repository);



    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void uploadImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("*/*");
        String[] extraMimeTypes = {"image/*", "video/mp4"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(galleryIntent, 2);
    }

    private void uploadProject(String title, String category, String description, String credits, String repository){
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        String projectId = ProjectReference.push().getKey();
        String projectList = getProjectList(userID);

        if(projectList == null){
            projectList = "";
        }

        StringBuffer sb = new StringBuffer();
        for(Uri imageUri : imageUrls){
            sb.append(imageUri.toString());
            sb.append(",");

        }
        String imageUrlList = sb.toString();

            Project project = new Project();
            project.setTitle(title);
            project.setCategory(category);
            project.setDescription(description);
            project.setImageUrls(imageUrlList);
            project.setUser(userID);
            project.setUploadDate(getCurrentDate());
            if (!credits.isEmpty()) {
                project.setCredits(credits);
            }
            if (!repository.isEmpty()) {
                project.setRepository(repository);
            }

            ProjectReference.child(projectId).setValue(project);

            projectList += "," + projectId;
            UserReference.child(userID).child("projects").setValue(projectList);
            progressBar.setVisibility(View.GONE);

            finish();

    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = formatter.format(date);
        return currentDate;
    }

    private String getProjectList(String userID) {
        UserReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("projects").getValue() != null) {
                    projects = snapshot.child("projects").getValue().toString().trim();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return projects;
    }

}