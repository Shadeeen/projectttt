package com.example.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AssignmentActivity extends AppCompatActivity {

    private static final int FILE_PICKER_REQUEST = 101;
    private Uri selectedFileUri;
    private RequestQueue queue;

    private  RecyclerView recyclerView;

    BottomNavigationView bottomNavigationView;
    private ImageView setting;
    private TextView headerTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

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
                findViewById(R.id.ass).setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

        queue = Volley.newRequestQueue(this);

        recyclerView = findViewById(R.id.recyclerAss);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        ass();


    }

    private void ass(){
        String url = "http://10.0.2.2:80/school_backend/submitAssignment.php?user_id=" + MainActivityStudent.user_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<AssignmentItem> ass = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String title = obj.getString("title");
                        String dueDate = obj.getString("due_date");
                        String des = obj.getString("description");
                        String subjectName = obj.getString("subject_name");
                        AssignmentItem item = new AssignmentItem(title, dueDate, des, subjectName);
                        ass.add(item);
                    }catch(JSONException exception){
                        Log.d("volley_error", exception.toString());
                    }
                }
                AssignmentAdapter adapter = new AssignmentAdapter(AssignmentActivity.this, ass);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley_error", error.toString());
            }
        });

        queue.add(request);
    }


    private String getFileName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String name = "unknown_file";
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex >= 0) {
                name = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return name;
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
                            Toast.makeText(AssignmentActivity.this, "Error parsing the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> Toast.makeText(AssignmentActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showStudentDataDialog(String studentName, String email, String role, String gender,
                                       String className, String numberOfAbsences, String homeroomTeacherName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentActivity.this);
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
                Toast.makeText(AssignmentActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_id");
                editor.remove("role");
                editor.apply();

                Intent intent = new Intent(AssignmentActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }


    private String getFilePath(Uri uri) {
        String filePath = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    filePath = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        } else if (uri.getScheme().equals("file")) {
            filePath = uri.getPath();
        }
        return filePath;
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }


    private void uploadAssignment(Uri fileUri) {
        String filePath = getFilePath(fileUri);
        String fileName = getFileName(fileUri);
        String fileUrl = "http://10.0.2.2:80/school_backend/" + fileName;

        String submissionDate = getCurrentDate();
        int userId = MainActivityStudent.user_id;

        if (userId != -1) {
            saveToDatabase(fileUrl, submissionDate, userId);
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDatabase(String fileUrl, String submissionDate, int userId) {
        String url = "http://10.0.2.2:80/school_backend/saveAssignment.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("success")) {
                            Toast.makeText(AssignmentActivity.this, "Assignment submitted successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AssignmentActivity.this, "Error saving assignment data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AssignmentActivity.this, "Network error occurred.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("file_url", fileUrl);
                params.put("submission_date", submissionDate);
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            String fileName = getFileName(selectedFileUri);
            Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_SHORT).show();
            uploadAssignment(selectedFileUri);
        }
    }


}