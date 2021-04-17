package com.nayphilim.showcaseapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * Use the {@link profileViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileViewFragment extends Fragment implements View.OnClickListener, AdapterProfileFeed.OnProjectListener {

    private static profileViewFragment instance;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private TextView profileName, profileLocation, profileSpecialization, profileProjectNum, profileYearsNum;

    private FirebaseUser user;
    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");

    private String userID;

    private RecyclerView recyclerView;
    private ArrayList<ProfileFeed> profileFeedArrayList = new ArrayList<>();
    private AdapterProfileFeed adapterProfileFeed;
    private ProgressBar progressBar;

    private ImageButton profileGithubButton;

    private User userProfile = new User();

    private String projects;

    public profileViewFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static profileViewFragment newInstance(String userId) {
        profileViewFragment fragment = new profileViewFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        if (getArguments() != null) {
            userID = getArguments().getString("userId");


        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_profile, container, false);


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
        profileGithubButton = view.findViewById(R.id.profileGithubButton);
        profileProjectNum = view.findViewById(R.id.profileProjectsNum);
        profileYearsNum = view.findViewById(R.id.profileYearsNum);
        progressBar = view.findViewById(R.id.profileProgressBar);

        profileGithubButton.setOnClickListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterProfileFeed = new AdapterProfileFeed(getContext(), profileFeedArrayList, this);
        recyclerView.setAdapter(adapterProfileFeed);

        updateProfile();
        populateRecyclerView();

        user = FirebaseAuth.getInstance().getCurrentUser();
        Session.startSession(user.getUid(),userID); //start recording session


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void populateRecyclerView() {

        progressBar.setVisibility(View.VISIBLE);

        profileFeedArrayList.clear();
        UserReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("projects").getValue() != null) {
                    projects = snapshot.child("projects").getValue().toString().trim();
                    if(projects != null) {
                        String[] projectList = projects.split(",");
                        profileProjectNum.setText(Integer.toString(projectList.length));

                        for (String projectId : projectList) {

                            ProjectReference.child(projectId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.child("visibility").getValue().toString().trim().equals("hidden")) {
                                        } else {
                                            String imageUrlsStr = snapshot.child("imageUrls").getValue().toString().trim();
                                            String[] imageUrls = imageUrlsStr.split(",");
                                            Uri imageUri = Uri.parse(imageUrls[0]);
                                            ProfileFeed profileFeed = new ProfileFeed(projectId, snapshot.child("title").getValue().toString().trim(), snapshot.child("category").getValue().toString().trim(), snapshot.child("uploadDate").getValue().toString().trim(), imageUri);
                                            profileFeedArrayList.add(profileFeed);
                                            adapterProfileFeed.notifyItemInserted(profileFeedArrayList.size());
                                        }
                                    }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        progressBar.setVisibility(View.GONE);
                    }
                    else{
                        profileProjectNum.setText("0");
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateProfile(){
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
                if((snapshot.child("githubLink").getValue() != null)){
                    String link = snapshot.child("githubLink").getValue().toString().trim();
                    if(!link.isEmpty()) {
                        userProfile.setGithubLink(link);
                        profileGithubButton.setVisibility(View.VISIBLE);
                    }
                }

                if((snapshot.child("numYears").getValue() != null)){
                    userProfile.setNumYears(snapshot.child("numYears").getValue().toString().trim());
                    profileYearsNum.setText(userProfile.getNumYears());
                }

                profileName.setText(userProfile.getFirstName() + " " + userProfile.getLastName());

                if(userProfile.getShowLocation() == "true"){
                    profileLocation.setText(userProfile.getLocation());
                    profileLocation.setVisibility(View.VISIBLE);
                }

                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public static profileViewFragment getInstance(){
        if(instance==null){
            instance = new profileViewFragment();
        }
        return instance;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileGithubButton:
                Session.itrInteractions();
                openGithubBrowser();
        }
    }



    @Override
    public void onProjectClick(int position) {
        Session.itrInteractions();
        ProfileFeed selectedProject =  profileFeedArrayList.get(position);
        String projectId = selectedProject.getProjectId();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = new Intent(getContext(), projectViewActivity.class);
        intent.putExtra("selectedProjectId", projectId);
        intent.putExtra("viewerID", user.getUid());
        startActivity(intent);
    }

    private void openGithubBrowser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(userProfile.getGithubLink()));
        startActivity(intent);
    }


}