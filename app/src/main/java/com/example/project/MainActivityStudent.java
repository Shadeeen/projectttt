package com.example.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

public class MainActivityStudent extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;


    private Button btnViewSec, btnViewMarks, btnSubmitAss;
    private ImageView setting;
    public static int user_id;
    public static String name;
    public static String role;

    private TextView headerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);



        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        user_id = sharedPreferences.getInt("user_id", -1);
        role = sharedPreferences.getString("role", "");
        name = sharedPreferences.getString("name","");

        headerTitle = findViewById(R.id.header_title);
        headerTitle.setText("Welcome " + name);

        if (user_id != -1) {
            fetchStudentData(user_id);
        } else {
            Toast.makeText(MainActivityStudent.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setting = findViewById(R.id.settings_icon);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchStudentData(user_id);
            }
        });

        loadFragment(new HomeStudentFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                findViewById(R.id.mainViews).setVisibility(View.VISIBLE);
                findViewById(R.id.container).setVisibility(View.GONE);
                return true;

            } else if (id == R.id.nav_subjects) {
                selectedFragment = new SubjectFragmentStudent();
            } else if (id == R.id.nav_teachers) {
                selectedFragment = new ExamsFragment();
            } else if (id == R.id.nav_classes) {
                selectedFragment = new ReminderFragment();
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


        btnViewSec = findViewById(R.id.btnViewSech);
        btnViewMarks = findViewById(R.id.btnViewMarks);
        btnSubmitAss = findViewById(R.id.btnSubmitAss);

        btnViewSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityStudent.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        btnViewMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityStudent.this, MarkActivity.class);
                startActivity(intent);
            }
        });


        btnSubmitAss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityStudent.this, AssignmentActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void fetchStudentData(int userId) {
        String url = "http://10.0.2.2:80/school_backend/information.php?user_id=" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String studentName = response.getString("studentName");
                            String email = response.getString("email");
                            String role = response.getString("role");
                            String gender = response.getString("gender");
                            String className = response.getString("className");
                            String numberOfAbsences = response.getString("numberOfAbsences");
                            String homeroomTeacherName = response.getString("homeroomTeacherName");
                            showStudentDataDialog(studentName, email, role, gender, className, numberOfAbsences, homeroomTeacherName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivityStudent.this, "Error parsing the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> Toast.makeText(MainActivityStudent.this, "Error fetching data.", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showStudentDataDialog(String studentName, String email, String role, String gender,
                                       String className, String numberOfAbsences, String homeroomTeacherName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityStudent.this);
        builder.setTitle("Student Information");

        String message = "Name: " + studentName + "\n" +
                "Email: " + email + "\n" +
                "Role: " + role + "\n" +
                "Gender: " + gender + "\n" +
                "Class: " + className + "\n" +
                "Number of Absences: " + numberOfAbsences + "\n" +
                "Homeroom Teacher: " + homeroomTeacherName;

        builder.setMessage(message);

        builder.setPositiveButton("Close", null);

        builder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivityStudent.this, "Logging out...", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_id");
                editor.remove("role");
                editor.apply();

                Intent intent = new Intent(MainActivityStudent.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }
}