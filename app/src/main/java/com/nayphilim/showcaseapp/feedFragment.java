package com.nayphilim.showcaseapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link feedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class feedFragment extends Fragment implements AdapterSearchFeed.OnProfileListener {

    private static feedFragment instance;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private ArrayList<SearchFeed> searchFeedArrayList = new ArrayList<>();
    private AdapterSearchFeed adapterSearchFeed;

    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");
    
    private EditText searchBox;

    private User user = new User();

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

        recyclerView = view.findViewById(R.id.searchFeed);
        searchBox = view.findViewById(R.id.searchBar);

        //wip not working
        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    firebaseUserSearch(searchBox.getText().toString().trim());
                    return true;
                }

                return false;
            }

        });
        






    }

    private void firebaseUserSearch(String searchTerm) {
        Query firebaseSearchQuery = UserReference.orderByChild("firstName").startAt(searchTerm).endAt(searchTerm + "\uf8ff");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(firebaseSearchQuery, User.class)
                .build();

        FirebaseRecyclerAdapter<User, AdapterSearchFeed.MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, AdapterSearchFeed.MyViewHolder>(options) {
            @NonNull
            @Override
            public AdapterSearchFeed.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull AdapterSearchFeed.MyViewHolder holder, int position, @NonNull User model) {
                holder.setDetails(model.getFirstName(), model.getLocation(), model.getSpecialization());
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }


    public static feedFragment getInstance(){
        if(instance==null){
            instance = new feedFragment();
        }
        return instance;
    }

    @Override
    public void onProfileClick(int position) {

    }
}