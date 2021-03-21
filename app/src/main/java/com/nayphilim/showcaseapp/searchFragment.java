package com.nayphilim.showcaseapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link searchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class searchFragment extends Fragment {

    private static searchFragment instance;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");

    private AutoCompleteTextView userSearch;
    private ListView searchListData;




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public searchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment searchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static searchFragment newInstance(String param1, String param2) {
        searchFragment fragment = new searchFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userSearch = (AutoCompleteTextView)view.findViewById(R.id.userSearch);
        searchListData =(ListView) view.findViewById(R.id.searchListData);

        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        UserReference.addListenerForSingleValueEvent(event);

        userSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    keyboardController.hideKeyboard(getActivity());
                    userSearch.dismissDropDown();
                    searchUser(userSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });


    }


    private void populateSearch(DataSnapshot snapshot) {
        ArrayList<String> names = new ArrayList<>();
//        ArrayList<String> emails = new ArrayList<>();
//        ArrayList<String> showLocations = new ArrayList<>();
//        ArrayList<String> locations = new ArrayList<>();
//        ArrayList<String> specializations = new ArrayList<>();

        if(snapshot.exists()){
            for(DataSnapshot ds:snapshot.getChildren()){
                String firstName = ds.child("firstName").getValue(String.class);
                String lastName = ds.child("lastName").getValue(String.class);
//                String email = ds.child("email").getValue(String.class);
//                String showLocation = ds.child("showLocation").getValue(String.class);
//                String location = ds.child("location").getValue(String.class);
//                String specialization = ds.child("specialization").getValue(String.class);
                names.add(firstName + " " + lastName);
//                emails.add(ds.child("email").getValue(String.class));
//                showLocations.add(ds.child("showLocation").getValue(String.class));
//                locations.add(ds.child("location").getValue(String.class));
//                specializations.add( ds.child("specialization").getValue(String.class));
                Log.d("users", firstName);
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, names);
            userSearch.setAdapter(arrayAdapter);
            userSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name = userSearch.getText().toString();
                    searchUser(name);

                }
            });
        }
        else{
            Log.d("users", "no users found");
            Toast.makeText(getContext(),"no users found", Toast.LENGTH_LONG);
        }


    }

    private void searchUser(String name) {
        Query query = UserReference.orderByChild("firstName").startAt(name.toUpperCase()).endAt(name.toLowerCase() + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ArrayList<String> listUsers = new ArrayList();
                    ArrayList<String> userIds = new ArrayList();
                    for(DataSnapshot ds:snapshot.getChildren()){
                        UserSearchResult userSearchResult = new UserSearchResult(ds.child("firstName").getValue(String.class) + " " + ds.child("lastName").getValue(String.class),
                                ds.child("specialization").getValue(String.class),
                                ds.child("location").getValue(String.class),
                                ds.child("showLocation").getValue(Boolean.class));
                        if(userSearchResult.getShowLocation()) {
                            listUsers.add(userSearchResult.getName() + "\n" + userSearchResult.getSpecialization() + " - " + userSearchResult.getLocation());
                        }
                        else{
                            listUsers.add(userSearchResult.getName() + "\n" + userSearchResult.getSpecialization());
                        }
                        userIds.add(ds.getKey());
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listUsers);
                    searchListData.setAdapter(arrayAdapter);

                    searchListData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            getUserProfile(userIds.get(position));
                        }
                    });




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserProfile(String userId) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        profileViewFragment profileViewFragment = com.nayphilim.showcaseapp.profileViewFragment.newInstance(userId);
        ft.replace(R.id.fragment, profileViewFragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    public static searchFragment getInstance(){
        if(instance==null){
            instance = new searchFragment();
        }
        return instance;
    }
}