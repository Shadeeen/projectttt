package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
     private int userId;
     private String userRole;
     String userName;
     String emaill;
     private  TextView name;
     private TextView email;
     private TextView header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        userName = sharedPreferences.getString("name","");
        emaill= sharedPreferences.getString("email","");

        header = findViewById(R.id.header_title);
        header.setText("Welcome " + userName);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new homeFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                findViewById(R.id.mainViews).setVisibility(View.VISIBLE);
                findViewById(R.id.container).setVisibility(View.GONE);
                return true;

            } else if (id == R.id.nav_subjects) {
                selectedFragment = new subjectsFragment();
            } else if (id == R.id.nav_teachers) {
                selectedFragment = new teachersFragment();
            } else if (id == R.id.nav_classes) {
                selectedFragment = new classesFragment();
            }

            if (selectedFragment != null) {
                findViewById(R.id.mainViews).setVisibility(View.GONE);
                findViewById(R.id.container).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

        profile();
        teachers();
        subjects();
        classes();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    public void profile(){



        ImageView profile=findViewById(R.id.profile);
        profile.setOnClickListener(e->{

            ProfileBottom bottomSheet = new ProfileBottom();
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

        });
    }

    public void teachers() {
        LinearLayout teachersScreen = findViewById(R.id.teachersScreen);
        teachersScreen.setOnClickListener(e -> {

            bottomNavigationView.setSelectedItemId(R.id.nav_teachers);


            findViewById(R.id.mainViews).setVisibility(View.GONE);
            findViewById(R.id.container).setVisibility(View.VISIBLE);

            Fragment teacherFragment = new teachersFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, teacherFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    public void subjects(){
        LinearLayout subjectScreen = findViewById(R.id.subjectScreen);
        subjectScreen.setOnClickListener(e->{


            bottomNavigationView.setSelectedItemId(R.id.nav_subjects);


            findViewById(R.id.mainViews).setVisibility(View.GONE);
            findViewById(R.id.container).setVisibility(View.VISIBLE);

            Fragment subjectsFragment = new subjectsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, subjectsFragment)
                    .addToBackStack(null)
                    .commit();

        });

    }


    public void classes(){
        LinearLayout subjectScreen = findViewById(R.id.classScreen);
        subjectScreen.setOnClickListener(e->{


            bottomNavigationView.setSelectedItemId(R.id.nav_classes);


            findViewById(R.id.mainViews).setVisibility(View.GONE);
            findViewById(R.id.container).setVisibility(View.VISIBLE);

            Fragment classFragment = new classesFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, classFragment)
                    .addToBackStack(null)
                    .commit();

        });

    }


}