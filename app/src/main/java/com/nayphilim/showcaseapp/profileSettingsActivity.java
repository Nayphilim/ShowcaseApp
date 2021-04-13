package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.zxing.BarcodeFormat;
import com.hbb20.CountryCodePicker;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.security.AccessController.getContext;

public class profileSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CountryCodePicker.OnCountryChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CountryCodePicker ccp;
    private SwitchCompat toggleLocation;
    private Spinner specializationSpinner;
    private EditText githubLinkText;
    private String Specialization, Location;
    private Boolean ShowLocation;
    private Boolean SpecializationChanged = false;
    private TextView cancel, save;

    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private com.google.firebase.storage.StorageReference StorageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseUser user;
    private String userID;
    private Button QRGenerationButton, QREmailButton;
    private ImageView QRCodeView;
    private String userEmail, userName;
    private Uri QRCode;
    private RequestManager glide;

    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        ccp = findViewById(R.id.profileSettingsCountryPicker);
        toggleLocation = findViewById(R.id.toggleLocation);
        specializationSpinner = findViewById(R.id.profileSettingsSpec);
        githubLinkText = findViewById(R.id.profileSettingsGithubLink);
        cancel = findViewById(R.id.profileSettingsCancel);
        save = findViewById(R.id.profileSettingsSave);
        QRGenerationButton = findViewById(R.id.QRGenerationButton);
        QREmailButton = findViewById(R.id.QREmailButton);
        QRCodeView = findViewById(R.id.QRCodeView);

        adapter = ArrayAdapter.createFromResource(this, R.array.specializations, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        specializationSpinner.setAdapter(adapter);
        specializationSpinner.setOnItemSelectedListener(this);

        ccp.setOnCountryChangeListener(this);

        toggleLocation.setOnCheckedChangeListener(this);

        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        QRGenerationButton.setOnClickListener(this);
        QREmailButton.setOnClickListener(this);

        glide = Glide.with(this);


        setPresets();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        SpecializationChanged = true;
        Specialization = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Specialization = null;
    }

    @Override
    public void onCountrySelected() {
        String country = ccp.getSelectedCountryNameCode();
        Location = country;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileSettingsCancel:
                finish();
                break;
            case R.id.profileSettingsSave:
                applyChanges();
                break;
            case R.id.QRGenerationButton:
                generateQR();
                break;
            case R.id.QREmailButton:
                emailQRCode(QRCode);
                break;
        }
    }

    private void generateQR() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap QRCode = barcodeEncoder.encodeBitmap(userID, BarcodeFormat.QR_CODE, 400, 400);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            QRCode.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
            String QRCodeKey = System.currentTimeMillis()+"";
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), QRCode, "QRCode-"+QRCodeKey, null);
            Uri QRUri = Uri.parse(path);



            StorageReference fileRef = StorageReference.child("QRCodes").child(QRCodeKey);
            UploadTask uploadTask = fileRef.putFile(QRUri);

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
                            Uri downloadUri = task.getResult();
                            Toast.makeText(profileSettingsActivity.this, "QR code has been generated and saved to your device", Toast.LENGTH_SHORT).show();
                            displayQRCode(downloadUri);


                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(@NonNull Object snapshot) {
                   // progressBar.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(UploadActivity.this, "Failed to upload, please try again", Toast.LENGTH_LONG).show();
                   // progressBar.setVisibility(View.GONE);
                    finish();
                }
            });
        } catch(Exception e) {

        }


    }

    private void displayQRCode(Uri QRCode) {
        this.QRCode = QRCode;
        glide.load(QRCode).into(QRCodeView);
        //QRCodeView.setImageURI(QRCode);
        QREmailButton.setVisibility(View.VISIBLE);
    }

    private void emailQRCode(Uri QRCode) {
        try {
            String subject = "Profile QR Code";
            String message;
            if (userName != null) {
                message = "Dear " + userName + "," + "\n\nHere is your automatically generated QR code, scan this in the app to take you to your profile!\n\n"+ QRCode;
            } else {
                message = "Dear User " + "," + "\n\nHere is your automatically generated QR code, scan this in the app to take you to your profile!\n\n" + QRCode;
            }
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{userEmail});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
        }
     catch (Throwable t) {

     }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            ShowLocation = true;
        }
        else{
            ShowLocation = false;
        }
    }

    private void applyChanges() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        String githubLink = githubLinkText.getText().toString().trim();
        final String GithubPattern = "^(https://github\\.com/).+";
        Pattern pattern = Pattern.compile(GithubPattern);
        Matcher matcher;

        UserReference.child(userID).child("showLocation").setValue(ShowLocation);

        if(Location != null){
            UserReference.child(userID).child("location").setValue(Location);
        }

        if(Specialization != null && SpecializationChanged){
            UserReference.child(userID).child("specialization").setValue(Specialization);
        }


        if(!githubLink.isEmpty()){
            if(!pattern.matcher(githubLink).matches()){
                githubLinkText.setError("Please enter a valid github profile link");
                githubLinkText.requestFocus();
                return;
            }
            else{
                UserReference.child(userID).child("githubLink").setValue(githubLink);
            }
        }

        finish();
    }

    private void setPresets(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        UserReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("showLocation").getValue() != null)){
                    if(snapshot.child("showLocation").getValue().toString() == "true"){
                        toggleLocation.setChecked(true);
                    }
                }
                if((snapshot.child("location").getValue() != null)){
                    String locationPreset = snapshot.child("location").getValue().toString();
                    ccp.setDefaultCountryUsingNameCode(locationPreset);
                    ccp.resetToDefaultCountry();
                }
                if((snapshot.child("specialization").getValue() != null)){
                   String specializationPreset = snapshot.child("specialization").getValue().toString();
                   specializationSpinner.setSelection(adapter.getPosition(specializationPreset));
                }
                if((snapshot.child("githubLink").getValue() != null)){
                    String githublinkPreset = snapshot.child("githubLink").getValue().toString();
                    githubLinkText.setText(githublinkPreset);
                }

                if((snapshot.child("email").getValue() != null)){
                    userEmail = snapshot.child("email").getValue().toString();
                }
                if((snapshot.child("firstName").getValue() != null)){
                   userName =  snapshot.child("firstName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}