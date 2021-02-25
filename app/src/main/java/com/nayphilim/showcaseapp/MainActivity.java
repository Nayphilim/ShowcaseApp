package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
     BottomNavigationView bottomNavigationView;
     NavController navController;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    User userProfile = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //navController= Navigation.findNavController(this,R.id.fragment);
        //NavigationUI.setupActionBarWithNavController(this,navController);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, new NavHostFragment())
                .commit();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();




        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile.setFirstName(snapshot.child("firstName").getValue().toString().trim());
                userProfile.setLastName(snapshot.child("lastName").getValue().toString().trim());
                userProfile.setEmail(snapshot.child("email").getValue().toString().trim());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        





    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.profileFragment:
                    selectedFragment = new profileFragment();
                    break;
                case R.id.feedFragment:
                    selectedFragment = new feedFragment();
                    break;
                case R.id.searchFragment:
                    selectedFragment = new searchFragment();
                    break;
                case R.id.analyticsFragment:
                    selectedFragment = new analyticsFragment();
                    break;
                case R.id.uploadFragment:
                    selectedFragment = new uploadFragment();
                    break;
            }

            if(selectedFragment != null){
                profileFragment.updateValues(userProfile.getFirstName(), userProfile.getLastName());
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, selectedFragment)
                    .commit();
            return true;
        }
        };
}