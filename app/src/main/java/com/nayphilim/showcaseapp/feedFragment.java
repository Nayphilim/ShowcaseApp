package com.nayphilim.showcaseapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link feedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class feedFragment extends Fragment implements AdapterProfileFeed.OnProjectListener{

    private static feedFragment instance;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DatabaseReference ProjectReference = FirebaseDatabase.getInstance().getReference("Projects");
    private DatabaseReference AnalyticReference = FirebaseDatabase.getInstance().getReference("Analytics");

    private RecyclerView recyclerView;
    private ArrayList<ProfileFeed> feedArrayList = new ArrayList<>();
    private feedAdapter adapterFeed;

    private Boolean isLoading = false;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public feedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment feedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static feedFragment newInstance(String param1, String param2) {
        feedFragment fragment = new feedFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);



        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.postFeed);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterFeed = new feedAdapter(getContext(), feedArrayList, this);
        recyclerView.setAdapter(adapterFeed);

        populateRecyclerView();
        initScrollListener();

    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == feedArrayList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        feedArrayList.add(null);
        adapterFeed.notifyItemInserted(feedArrayList.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                feedArrayList.remove(feedArrayList.size() - 1);
                int scrollPosition = feedArrayList.size();
                adapterFeed.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                populateRecyclerView();

                adapterFeed.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    private void populateRecyclerView() {

                    ProjectReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<String> allProjects = new ArrayList<String>();
                            for (DataSnapshot d : snapshot.getChildren()) {
                                allProjects.add(d.getKey());
                            }
                            for (int i = 0; i < 10; i++) {
                                int random = new Random().nextInt(allProjects.size());
                                DataSnapshot selectedProject = snapshot.child(allProjects.get(random));
                                allProjects.remove(random);
                                if (selectedProject.child("visibility").getValue().toString().trim().equals("hidden")) {
                                } else {
                                    String imageUrlsStr = selectedProject.child("imageUrls").getValue().toString().trim();
                                    String[] imageUrls = imageUrlsStr.split(",");
                                    Uri imageUri = Uri.parse(imageUrls[0]);
                                    ProfileFeed profileFeed = new ProfileFeed(selectedProject.getKey(), selectedProject.child("title").getValue().toString().trim(), selectedProject.child("category").getValue().toString().trim(), selectedProject.child("uploadDate").getValue().toString().trim(), imageUri);
                                    feedArrayList.add(profileFeed);
                                    adapterFeed.notifyItemInserted(feedArrayList.size());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




        }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    public static feedFragment getInstance(){
        if(instance==null){
            instance = new feedFragment();
        }
        return instance;
    }


    @Override
    public void onProjectClick(int position) {
        ProfileFeed selectedProject =  feedArrayList.get(position);
        String projectId = selectedProject.getProjectId();

        Intent intent = new Intent(getContext(), projectActivity.class);
        intent.putExtra("selectedProjectId", projectId);
        startActivity(intent);
    }
}