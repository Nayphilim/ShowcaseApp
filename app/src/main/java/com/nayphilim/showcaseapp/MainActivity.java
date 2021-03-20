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

import android.content.Intent;
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //navController= Navigation.findNavController(this,R.id.fragment);
        //NavigationUI.setupActionBarWithNavController(this,navController);

//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment,  profileFragment.getInstance()) //profile fragment
//                .commit();



//        Fragment homeFragment = profileFragment.getInstance();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment, homeFragment)
//                .addToBackStack(null)
//                .commit();





    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.profileFragment:
                    selectedFragment = profileFragment.getInstance();
                    break;
                case R.id.feedFragment:
                    selectedFragment = feedFragment.getInstance();
                    break;
                case R.id.searchFragment:
                    selectedFragment = searchFragment.getInstance();
//                    Intent intent = new Intent(MainActivity.this,searchTest.class );
//                    startActivity(intent);
                    break;
                case R.id.analyticsFragment:
                    selectedFragment = analyticsFragment.getInstance();
                    break;
                case R.id.uploadFragment:
                    //selectedFragment = uploadFragment.getInstance();
                    startUploadActivity();
                    break;
            }

            if(selectedFragment != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment, selectedFragment)
                        .addToBackStack(null)
                        .commit();

            }

            return true;
        }
        };

    private void startUploadActivity() {
        Intent intent = new Intent(MainActivity.this,UploadActivity.class );
        startActivity(intent);
    }

    public void onBackPressed() {
        //if on first fragment exit app when back is pressed
        if(bottomNavigationView.getSelectedItemId() == R.id.profileFragment){
            super.onBackPressed();
            finish();
        }
        else{
            bottomNavigationView.setSelectedItemId(R.id.profileFragment);
        }

    }
}