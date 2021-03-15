package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private FirebaseUser user;
    private String userID;

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

        adapter = ArrayAdapter.createFromResource(this, R.array.specializations, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        specializationSpinner.setAdapter(adapter);
        specializationSpinner.setOnItemSelectedListener(this);

        ccp.setOnCountryChangeListener(this);

        toggleLocation.setOnCheckedChangeListener(this);

        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}