package com.example.project;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MarkActivity extends AppCompatActivity {

    private ListView listMarks;
    private RequestQueue queue;
    BottomNavigationView bottomNavigationView;
    private ImageView setting;
    private TextView headerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);

        headerTitle = findViewById(R.id.header_title);
        headerTitle.setText("Welcome " + MainActivityStudent.name);

        setting = findViewById(R.id.settings_icon);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchStudentData(MainActivityStudent.user_id);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                findViewById(R.id.mainViews).setVisibility(View.VISIBLE);
                findViewById(R.id.container).setVisibility(View.GONE);
                findViewById(R.id.marks).setVisibility(View.VISIBLE);
                findViewById(R.id.listMarks).setVisibility(View.VISIBLE);

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
                findViewById(R.id.marks).setVisibility(View.GONE);
                findViewById(R.id.listMarks).setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });


        listMarks = findViewById(R.id.listMarks);
        queue = Volley.newRequestQueue(this);
        marks();

    }

    public void marks(){
        String url = "http://10.0.2.2:80/school_backend/viewMark.php?user_id=" + MainActivityStudent.user_id ;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<MarkItem> marks = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        double assignment = obj.getDouble("assignment_mark");
                        double mid = obj.getDouble("mid_exam_mark");
                        double finalMark = obj.getDouble("final_exam_mark");
                        double total = 0.0;
                        String details = "";
                        if(mid != -1 && finalMark != -1 && assignment != -1){
                            total = assignment + mid + finalMark;
                            details = "Assignment: " + assignment +
                                    "\nMid: " + mid +
                                    "\nFinal: " + finalMark;
                        }else if(mid == -1 && finalMark != -1 && assignment != -1){
                            total = assignment + finalMark;
                            details = "Assignment: " + assignment +
                                    "\nMid: Still Not Posted" +
                                    "\nFinal: " + finalMark;
                        }else if(mid != -1 && finalMark == -1 && assignment != -1){
                            total = assignment + mid;
                            details = "Assignment: " + assignment +
                                    "\nMid: " + mid +
                                    "\nFinal: Still Not Posted";
                        }else if (mid != -1 && finalMark != -1 && assignment == -1){
                            total = mid + finalMark;
                            details = "Assignment: Still Not Posted" +
                                    "\nMid: " + mid +
                                    "\nFinal: " + finalMark;
                        }else if(mid == -1 && finalMark == -1 && assignment != -1){
                            total = assignment;
                            details = "Assignment: " + assignment +
                                    "\nMid: Still Not Posted" +
                                    "\nFinal: Still Not Posted";
                        }else if(mid != -1 && finalMark == -1 && assignment == -1){
                            total = mid;
                            details = "Assignment: Still Not Posted"  +
                                    "\nMid: " + mid +
                                    "\nFinal: Still Not Posted";
                        }else{
                            total = finalMark;
                            details = "Assignment: Still Not Posted"  +
                                    "\nMid: Still Not Posted"  +
                                    "\nFinal: " + finalMark;
                        }

                        MarkItem mark = new MarkItem(obj.getString("subject_name"), total, details);
                        marks.add(mark);
                    }catch(JSONException exception){
                        Log.d("volley_error", exception.toString());
                    }
                }
                MarkAdapter adapter = new MarkAdapter(MarkActivity.this, marks);
                listMarks.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley_error", error.toString());
            }
        });

        queue.add(request);

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
                            Toast.makeText(MarkActivity.this, "Error parsing the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> Toast.makeText(MarkActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showStudentDataDialog(String studentName, String email, String role, String gender,
                                       String className, String numberOfAbsences, String homeroomTeacherName) {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(MarkActivity.this);
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
                Toast.makeText(MarkActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_id");
                editor.remove("role");
                editor.apply();

                Intent intent = new Intent(MarkActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }
}