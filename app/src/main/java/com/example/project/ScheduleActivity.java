package com.example.project;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ScheduleActivity extends AppCompatActivity {

    private Spinner spnDay;
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private JSONArray fullScheduleData;
    private ScheduleAdapter adapterItem;
    BottomNavigationView bottomNavigationView;
    private ImageView setting;
    private TextView headerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

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
                findViewById(R.id.ass).setVisibility(View.VISIBLE);
                findViewById(R.id.spnDay).setVisibility(View.VISIBLE);
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
                findViewById(R.id.sech).setVisibility(View.GONE);
                findViewById(R.id.spnDay).setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });
        spnDay = findViewById(R.id.spnDay);
        String[] days = getResources().getStringArray(R.array.week_days);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_text,
                R.id.spinnerText,
                days
        );
        spnDay.setAdapter(adapter);

        recyclerView = findViewById(R.id.recyclerAss);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapterItem = new ScheduleAdapter(new ArrayList<>());
        queue = Volley.newRequestQueue(this);

        viewSchedule();

        spnDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedDay = parent.getItemAtPosition(position).toString();
                filterScheduleByDay(selectedDay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    public void viewSchedule() {
        String url = "http://10.0.2.2:80/school_backend/viewSchedule.php?user_id=" + MainActivityStudent.user_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        fullScheduleData = response;
                        String selectedDay = spnDay.getSelectedItem().toString();
                        filterScheduleByDay(selectedDay);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley_error", error.toString());
                    }
                });

        queue.add(request);
    }

    private void filterScheduleByDay(String selectedDay) {
        ArrayList<ScheduleItem> filtered = new ArrayList<>();

        if (fullScheduleData == null) return;

        try {
            for (int i = 0; i < fullScheduleData.length(); i++) {
                JSONObject obj = fullScheduleData.getJSONObject(i);
                String day = obj.getString("day");
                if (day.equalsIgnoreCase(selectedDay)) {
                    String subject = obj.getString("subject_name");
                    String start = obj.getString("start_time");
                    String end = obj.getString("end_time");
                    filtered.add(new ScheduleItem(subject, start + " - " + end));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapterItem.updateData(filtered);
        recyclerView.setAdapter(adapterItem);
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
                            Toast.makeText(ScheduleActivity.this, "Error parsing the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> Toast.makeText(ScheduleActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showStudentDataDialog(String studentName, String email, String role, String gender,
                                       String className, String numberOfAbsences, String homeroomTeacherName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
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
                Toast.makeText(ScheduleActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_id");
                editor.remove("role");
                editor.apply();

                Intent intent = new Intent(ScheduleActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }
}