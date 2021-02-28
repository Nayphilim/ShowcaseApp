package com.nayphilim.showcaseapp;

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
public class profileFragment extends Fragment {

    private static profileFragment instance;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    

    private TextView profileName;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    private RecyclerView recyclerView;
    private ArrayList<ProfileFeed> profileFeedArrayList = new ArrayList<>();
    private AdapterProfileFeed adapterProfileFeed;

    User userProfile = new User();

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
        updateProfile();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        profileName = getView().findViewById(R.id.profileName);
        recyclerView =getView().findViewById(R.id.profileLineFeed);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapterProfileFeed = new AdapterProfileFeed(getContext(), profileFeedArrayList);
        recyclerView.setAdapter(adapterProfileFeed);

        populateRecyclerView();




    }

    private void populateRecyclerView() {
        //WIP
        //change method to get title, category, date and img read from database for the amount of projects
        //ISSUE WITH METHOD BEING CALLED EVERY ITEM FRAGMENT IS OPENED
        ProfileFeed profileFeed = new ProfileFeed("Project Title", "Project Category", "01/01/01", R.drawable.testpostimg);
        profileFeedArrayList.add(profileFeed);
        ProfileFeed profileFeed2 = new ProfileFeed("Project Title", "Project Category", "01/01/01", R.drawable.testpostimg);
        profileFeedArrayList.add(profileFeed);
        ProfileFeed profileFeed3 = new ProfileFeed("Project Title", "Project Category", "01/01/01", R.drawable.testpostimg);
        profileFeedArrayList.add(profileFeed);
        ProfileFeed profileFeed4 = new ProfileFeed("Project Title", "Project Category", "01/01/01", R.drawable.testpostimg);
        profileFeedArrayList.add(profileFeed);

        adapterProfileFeed.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void updateProfile(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile.setFirstName(snapshot.child("firstName").getValue().toString().trim());
                userProfile.setLastName(snapshot.child("lastName").getValue().toString().trim());
                userProfile.setEmail(snapshot.child("email").getValue().toString().trim());
                profileName.setText(userProfile.getFirstName() + " " + userProfile.getLastName());
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
}