package com.nayphilim.showcaseapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ru.nikartm.support.BadgePosition;
import ru.nikartm.support.ImageBadgeView;

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
    

    private TextView profileName, profileLocation, profileSpecialization, profileProjectNum, profileYearsNum;

    private FirebaseUser user;
    private DatabaseReference  UserReference = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");
    private DatabaseReference FeedbackReference = FirebaseDatabase.getInstance().getReference("Feedback");

    private String userID;
    private String projects;
    private int projectNum;

    private RecyclerView recyclerView;
    private ArrayList<ProfileFeed> profileFeedArrayList = new ArrayList<>();
    private AdapterProfileFeed adapterProfileFeed;

    private ProgressBar progressBar;

    private ImageButton profileSettingsButton;
    private ImageBadgeView profileInboxButton;

    private User userProfile = new User();

    public profileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance(String userId) {
        profileFragment fragment = new profileFragment();
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
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

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
        profileProjectNum = view.findViewById(R.id.profileProjectsNum);
        profileInboxButton = view.findViewById(R.id.profileInboxButton);
        profileYearsNum = view.findViewById(R.id.profileYearsNum);
        progressBar = view.findViewById(R.id.profileProgressBar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterProfileFeed = new AdapterProfileFeed(getContext(), profileFeedArrayList, this);
        recyclerView.setAdapter(adapterProfileFeed);
        adapterProfileFeed.notifyDataSetChanged();

        profileSettingsButton.setOnClickListener(this);
        profileInboxButton.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();


        updateProfile();
        populateRecyclerView();







    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void populateRecyclerView() {

        progressBar.setVisibility(View.VISIBLE);
        profileFeedArrayList.clear();
        projectNum = 0;

        UserReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("projects").getValue() != null) {
                    projects = snapshot.child("projects").getValue().toString().trim();
                    if(projects != "") {
                        String[] projectList = projects.split(",");
                        profileProjectNum.setText(Integer.toString(projectList.length));

                        for (String projectId : projectList) {
                            ProjectReference.child(projectId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child("visibility").getValue().toString().trim().equals("hidden")){
                                    }
                                    else{
                                        projectNum++;
                                        String imageUrlsStr = snapshot.child("imageUrls").getValue().toString().trim();
                                        String[] imageUrls = imageUrlsStr.split(",");
                                        Uri imageUri = Uri.parse(imageUrls[0]);
                                        ProfileFeed profileFeed = new ProfileFeed(projectId, snapshot.child("title").getValue().toString().trim(), snapshot.child("category").getValue().toString().trim(), snapshot.child("uploadDate").getValue().toString().trim(), imageUri);
                                        profileFeedArrayList.add(profileFeed);
                                        adapterProfileFeed.notifyItemInserted(profileFeedArrayList.size());
                                        profileProjectNum.setText(Integer.toString(projectNum));
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
                else {
                    profileProjectNum.setText("0");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




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

                if((snapshot.child("numYears").getValue() != null)){
                    userProfile.setNumYears(snapshot.child("numYears").getValue().toString().trim());
                    profileYearsNum.setText(userProfile.getNumYears());
                }

                profileName.setText(userProfile.getFirstName() + " " + userProfile.getLastName());

                if(userProfile.getShowLocation().equals("true")){
                    profileLocation.setText(userProfile.getLocation());
                    profileLocation.setVisibility(View.VISIBLE);
                }


                if(snapshot.child("unreadFeedback").getValue() != null){
                    if(snapshot.child("unreadFeedback").getValue().toString().trim() == "true"){
                        profileInboxButton.setBadgeValue(1)
                                .setBadgeTextSize(16)
                                .setMaxBadgeValue(999)
                                .setBadgeBackground(getResources().getDrawable(R.drawable.inbox_notification_badge))
                                .setBadgePosition(BadgePosition.BOTTOM_RIGHT)
                                .setBadgeTextStyle(Typeface.NORMAL);
                    }
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
                break;
            case R.id.profileInboxButton:
                startInboxActivity();
                break;
        }
    }

    private void startInboxActivity() {
        Intent intent = new Intent(getActivity(),InboxActivity.class );
        startActivity(intent);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(getActivity(),profileSettingsActivity.class );
        startActivity(intent);
    }


    @Override
    public void onProjectClick(int position) {
        ProfileFeed selectedProject =  profileFeedArrayList.get(position);
        String projectId = selectedProject.getProjectId();

        Intent intent = new Intent(getContext(), projectActivity.class);
        intent.putExtra("selectedProjectId", projectId);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}