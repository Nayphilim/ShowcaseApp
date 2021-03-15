package com.nayphilim.showcaseapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment implements View.OnClickListener, AdapterProfileFeed.OnProjectListener {

    private static profileFragment instance;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    

    private TextView profileName, profileLocation, profileSpecialization;

    private FirebaseUser user;
    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");

    private String userID;

    private RecyclerView recyclerView;
    private ArrayList<ProfileFeed> profileFeedArrayList = new ArrayList<>();
    private AdapterProfileFeed adapterProfileFeed;

    private ImageButton profileSettingsButton;

    private User userProfile = new User();

    public profileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance(String param1, String param2) {
        profileFragment fragment = new profileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inflate the layout for this fragment
        return v;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileName = view.findViewById(R.id.profileName);
        profileLocation = view.findViewById(R.id.profileLocation);
        profileSpecialization = view.findViewById(R.id.profileSpecialization);
        recyclerView = view.findViewById(R.id.profileLineFeed);
        profileSettingsButton = view.findViewById(R.id.profileSettingsButton);

        profileSettingsButton.setOnClickListener(this);







        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        updateProfile();
        populateRecyclerView();


    }

    private void populateRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterProfileFeed = new AdapterProfileFeed(getContext(), profileFeedArrayList, this);
        recyclerView.setAdapter(adapterProfileFeed);
          profileFeedArrayList.clear();
          String projectListStr = User.getProjectList(userID);
          if(projectListStr != null) {
              String[] projectList = projectListStr.split(",");
              for (String projectId : projectList) {
                  ProjectReference.child(projectId).addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                          String imageUrlsStr = snapshot.child("imageUrls").getValue().toString().trim();
                          String[] imageUrls = imageUrlsStr.split(",");
                          Uri imageUri = Uri.parse(imageUrls[0]);
                          ProfileFeed profileFeed = new ProfileFeed(projectId, snapshot.child("title").getValue().toString().trim(), snapshot.child("category").getValue().toString().trim(), snapshot.child("uploadDate").getValue().toString().trim(), imageUri);
                          profileFeedArrayList.add(profileFeed);
                          adapterProfileFeed.notifyItemInserted(profileFeedArrayList.size());
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                  });

              }
          }
    }

    private void updateProfile(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        UserReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile.setFirstName(snapshot.child("firstName").getValue().toString().trim());
                userProfile.setLastName(snapshot.child("lastName").getValue().toString().trim());
                userProfile.setEmail(snapshot.child("email").getValue().toString().trim());

                if((snapshot.child("showLocation").getValue() != null)){
                    userProfile.setShowLocation(snapshot.child("showLocation").getValue().toString().trim());
                }
                if((snapshot.child("location").getValue() != null)){
                    userProfile.setLocation(snapshot.child("location").getValue().toString().trim());
                }
                if((snapshot.child("specialization").getValue() != null)){
                    userProfile.setSpecialization(snapshot.child("specialization").getValue().toString().trim());
                    profileSpecialization.setText(userProfile.getSpecialization());
                    profileSpecialization.setVisibility(View.VISIBLE);
                }

                profileName.setText(userProfile.getFirstName() + " " + userProfile.getLastName());

                if(userProfile.getShowLocation() == "true"){
                    profileLocation.setText(userProfile.getLocation());
                    profileLocation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static profileFragment getInstance(){
        if(instance==null){
            instance = new profileFragment();
        }
        return instance;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileSettingsButton:
                startSettingsActivity();
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(getActivity(),profileSettingsActivity.class );
        startActivity(intent);
    }


    @Override
    public void onProjectClick(int position) {
        ProfileFeed selectedProject =  profileFeedArrayList.get(position);
        String projectId = selectedProject.getProjectId();

        Intent intent = new Intent(getContext(), projectViewAcitivty.class);
        intent.putExtra("selectedProjectId", projectId);
        startActivity(intent);
    }
}