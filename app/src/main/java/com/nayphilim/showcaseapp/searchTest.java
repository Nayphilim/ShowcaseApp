package com.nayphilim.showcaseapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class searchTest extends AppCompatActivity {

    private DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users");

    private AutoCompleteTextView userSearch;
    private ListView searchListData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

        userSearch = (AutoCompleteTextView)findViewById(R.id.userSearch);
        searchListData =(ListView) findViewById(R.id.searchListData);

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
//                String lastName = ds.child("lastName").getValue(String.class);
//                String email = ds.child("email").getValue(String.class);
//                String showLocation = ds.child("showLocation").getValue(String.class);
//                String location = ds.child("location").getValue(String.class);
//                String specialization = ds.child("specialization").getValue(String.class);
                names.add(firstName);
//                emails.add(ds.child("email").getValue(String.class));
//                showLocations.add(ds.child("showLocation").getValue(String.class));
//                locations.add(ds.child("location").getValue(String.class));
//                specializations.add( ds.child("specialization").getValue(String.class));
                Log.d("users", firstName);
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
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
            Toast.makeText(this,"no users found", Toast.LENGTH_LONG);
        }


    }

    private void searchUser(String name) {
        Query query = UserReference.orderByChild("firstName").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ArrayList<String> listUsers = new ArrayList();
                    for(DataSnapshot ds:snapshot.getChildren()){
                        UserSearchResult userSearchResult = new UserSearchResult(ds.child("firstName").getValue(String.class) + " " + ds.child("lastName").getValue(String.class),
                                ds.child("specialization").getValue(String.class),
                                ds.child("location").getValue(String.class),
                                ds.child("showLocation").getValue(String.class));
                        if(userSearchResult.getShowLocation() == "true") {
                            listUsers.add(userSearchResult.getName() + "\n" + userSearchResult.getSpecialization() + " - " + userSearchResult.getLocation());
                        }
                        else{
                            listUsers.add(userSearchResult.getName() + "\n" + userSearchResult.getSpecialization());
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(searchTest.this, android.R.layout.simple_list_item_1, listUsers);
                    searchListData.setAdapter(arrayAdapter);
//                    ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, listUsers){
//                        @NonNull
//                        @Override
//                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                            View view = super.getView(position, convertView, parent);
//                            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//                            TextView text2 = (TextView) view.findViewById(android.R.id.text2);
//
//                            text1.setText(names.get(position));
//
//                            if(showLocations.get(position) == "true"){
//                                text2.setText(specializations.get(position) + " - " + locations.get(position));
//                            }
//                            else{
//                                text2.setText(specializations.get(position));
//                            }
//
//                            return view;
//                        }
//                    };
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}