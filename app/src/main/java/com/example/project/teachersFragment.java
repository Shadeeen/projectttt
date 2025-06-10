package com.example.project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class teachersFragment extends Fragment {

    ArrayList<Teacher> allTeachers = new ArrayList<>();
    TeacherAdapter teacherAdapter;
    ListView teacherListView;

    private TextView emptyTextView;
    public teachersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teachers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        teacherListView = view.findViewById(R.id.teacherListView);
        printTeachers();

        teacherListView.setOnItemClickListener((parent, view1, position, id) -> {
            Teacher selectedTeacher = teacherAdapter.getItem(position);
            showTeacherDetailsDialog(selectedTeacher);
        });

        EditText searchBar = view.findViewById(R.id.searchBar);
        searchTeacher(searchBar);


        FloatingActionButton fab = view.findViewById(R.id.fabAddTeacher);
        addButton(fab);

        emptyTextView = view.findViewById(R.id.emptyTeacherTextView);



    }

    public void addButton( FloatingActionButton fab){

        fab.setOnClickListener(v -> {

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.add_teacher, null);
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .create();
            dialog.show();

            EditText nameInput = dialogView.findViewById(R.id.teacherName);
            EditText emailInput = dialogView.findViewById(R.id.teacherEmail);
            RadioGroup genderGroup = dialogView.findViewById(R.id.genderGroup);
            Spinner subjectSpinner = dialogView.findViewById(R.id.subjectSpinner);
            LinearLayout checkboxContainer = dialogView.findViewById(R.id.classCheckboxContainer);
            Button saveBtn = dialogView.findViewById(R.id.addtech);

            HashMap<String, Integer> subjectMap = new HashMap<>();
            getSubects(subjectSpinner,subjectMap ) ;
            getClasses(checkboxContainer );

            saveBtn.setOnClickListener(view1 -> {
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();

                int selectedGenderId = genderGroup.getCheckedRadioButtonId();
                if (selectedGenderId == -1 || name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String gender = ((RadioButton) dialogView.findViewById(selectedGenderId)).getText().toString();
                String subjectName = subjectSpinner.getSelectedItem().toString();

                RadioGroup classRadioGroup = (RadioGroup) checkboxContainer.getChildAt(0);
                ArrayList<Integer> selectedClassIds = new ArrayList<>();

                for (int i = 0; i < classRadioGroup.getChildCount(); i++) {
                    CheckBox cb = (CheckBox) classRadioGroup.getChildAt(i);
                    if (cb.isChecked()) {
                        selectedClassIds.add((Integer) cb.getTag());
                    }
                }

                if (selectedClassIds.isEmpty()) {
                    Toast.makeText(getContext(), "Please select at least one class", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject postData = new JSONObject();

                try {

                    Log.d("DEBUG", "Selected subject: " + subjectName);
                    Log.d("DEBUG", "Subject ID: " + subjectMap.get(subjectName));

                    postData.put("name", name);
                    postData.put("email", email);
                    postData.put("gender", gender);
                    int subjectId = subjectMap.get(subjectName);
                    postData.put("subject_id", subjectId);
                    JSONArray classArray = new JSONArray();
                    for (Integer id : selectedClassIds) {
                        classArray.put(id);
                    }

                    Log.d("DEBUG", "Selected class IDs: " + selectedClassIds.toString());

                    postData.put("class_ids", classArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "http://10.0.2.2:80/school_backend/addTeacher.php";

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                        response -> {
                            try {
                                String status = response.getString("status");
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                if (status.equals("success")) {
                                    printTeachers();
                                    dialog.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            Toast.makeText(getContext(), "Failed to save teacher", Toast.LENGTH_SHORT).show();
                            Log.e("VolleyError", error.toString());
                        });

                Volley.newRequestQueue(requireContext()).add(request);
            });

        });

    }

    public void searchTeacher( EditText searchBar){
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTeachers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void printTeachers() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "http://10.0.2.2:80/school_backend/teacher.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, response -> {
            allTeachers.clear();

            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    Teacher teacher = new Teacher(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getString("email"),
                            obj.getString("gender"),
                            obj.getString("subject_name")
                    );
                    allTeachers.add(teacher);
                } catch (JSONException exception) {
                    Log.e("VolleyError", "JSON parsing error: " + exception.getMessage());
                }
            }
            updateTeacherUI();

        }, error -> {
            Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show();
            Log.d("Error_json", error.toString());
        });

        queue.add(request);
    }
    private void updateTeacherUI() {
        if (allTeachers.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            teacherListView.setVisibility(View.GONE);
        } else {

            emptyTextView.setVisibility(View.GONE);
            teacherListView.setVisibility(View.VISIBLE);

            if (teacherAdapter == null) {
                teacherAdapter = new TeacherAdapter(requireContext(), new ArrayList<>(allTeachers));
                teacherListView.setAdapter(teacherAdapter);
                teacherListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            } else {
                teacherAdapter.clear();
                teacherAdapter.addAll(allTeachers);
                teacherAdapter.notifyDataSetChanged();
            }
        }
    }
    public void getSubects(Spinner subjectSpinner, HashMap<String, Integer> subjectMap) {
        ArrayList<String> subjectNames = new ArrayList<>();
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectNames);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);

        String subjectUrl = "http://10.0.2.2:80/school_backend/subjects.php";

        JsonArrayRequest subjectRequest = new JsonArrayRequest(Request.Method.GET, subjectUrl, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String subjectName = obj.getString("subject_name");
                            int subjectId = obj.getInt("id");

                            // Fill both list and map
                            subjectNames.add(subjectName);
                            subjectMap.put(subjectName, subjectId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    subjectAdapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(getContext(), "Error loading subjects", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(subjectRequest);
    }


    public void getClasses(LinearLayout checkboxContainer){
    String classUrl = "http://10.0.2.2:80/school_backend/classes.php";

    JsonArrayRequest classRequest = new JsonArrayRequest(Request.Method.GET, classUrl, null,
            response -> {
                RadioGroup classRadioGroup = new RadioGroup(getContext());
                classRadioGroup.setOrientation(RadioGroup.VERTICAL);

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int classId = obj.getInt("id");
                        String className = obj.getString("name");

                        CheckBox rb = new CheckBox(getContext());
                        rb.setText(className);
                        rb.setTag(classId);
                        rb.setTextSize(18);
                        classRadioGroup.addView(rb);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                checkboxContainer.removeAllViews();
                checkboxContainer.addView(classRadioGroup);
            },
            error -> Toast.makeText(getContext(), "Error loading classes", Toast.LENGTH_SHORT).show()
    );

    Volley.newRequestQueue(requireContext()).add(classRequest);

}
    private void filterTeachers(String query) {
        ArrayList<Teacher> filteredList = new ArrayList<>();

        for (Teacher teacher : allTeachers) {
            if (teacher.getName().toLowerCase().contains(query.toLowerCase()) ||
                    teacher.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    teacher.getSubject().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(teacher);
            }
        }

        teacherAdapter.clear();
        teacherAdapter.addAll(filteredList);
        teacherAdapter.notifyDataSetChanged();
    }

    private void showTeacherDetailsDialog(Teacher teacher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.teacher_details, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView nameTv = dialogView.findViewById(R.id.dialogTeacherName);
        TextView emailTv = dialogView.findViewById(R.id.dialogTeacherEmail);
        TextView genderTv = dialogView.findViewById(R.id.dialogTeacherGender);
        TextView subjectTv = dialogView.findViewById(R.id.dialogTeacherSubject);
        TextView classesTv = dialogView.findViewById(R.id.dialogTeacherClass);
        Button deleteBtn = dialogView.findViewById(R.id.deleteTeach);


        nameTv.setText("Name: " + teacher.getName());
        emailTv.setText("Email: " + teacher.getEmail());
        genderTv.setText("Gender: " + teacher.getGender());
        subjectTv.setText("Subject: " + teacher.getSubject());

        fetchTeacherClasses(teacher.getId(), classesTv);

        deleteBtn.setOnClickListener(v -> {
            deleteTeacher(teacher.getId(), dialog);
        });


    }

    private void fetchTeacherClasses(int teacherId, TextView classesTv) {
        String url = "http://10.0.2.2:80/school_backend/teacherClasses.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<String> classNames = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            classNames.add(obj.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String display = "Classes: " + String.join(", ", classNames);
                    classesTv.setText(display);



                },
                error -> {
                    classesTv.setText("Error loading classes");
                    Log.e("FETCH_CLASSES", error.toString());
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }


    private void deleteTeacher(int teacherId, AlertDialog dialog) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this teacher? This action cannot be undone and will remove all related data (schedules, assignments, etc.).")
                .setPositiveButton("Delete", (confirmDialog, which) -> {
                    Toast.makeText(getContext(), "Deleting teacher...", Toast.LENGTH_SHORT).show();

                    String url = "http://10.0.2.2:80/school_backend/deleteTeacher.php?id=" + teacherId;
                    Log.d("DELETE_TEACHER", "Request URL: " + url);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                            response -> {
                                Log.d("DELETE_TEACHER", "Raw response: " + response.toString());

                                try {
                                    if (response.has("status")) {
                                        String status = response.getString("status");
                                        String message = response.optString("message", "No message provided");

                                        if (status.equals("success")) {

                                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                                            dialog.dismiss();

                                            removeTeacherFromList(teacherId);

                                            refreshTeacherList();

                                        } else {

                                            String debugInfo = response.optString("debug", "No debug info");
                                            Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_LONG).show();
                                            Log.e("DELETE_TEACHER", "Server returned error: " + message + ", Debug: " + debugInfo);
                                        }
                                    } else {
                                        // Response doesn't have expected format
                                        Log.e("DELETE_TEACHER", "Response missing 'status' field: " + response.toString());
                                        Toast.makeText(getContext(), "Invalid server response format", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), "Error parsing server response", Toast.LENGTH_SHORT).show();
                                    Log.e("DELETE_TEACHER", "JSON parsing error: " + e.getMessage());
                                    Log.e("DELETE_TEACHER", "Response was: " + response.toString());
                                }
                            },
                            error -> {
                                String errorMessage = "Delete failed";

                                Log.e("DELETE_TEACHER", "Volley error: " + error.toString());

                                if (error.networkResponse != null) {
                                    int statusCode = error.networkResponse.statusCode;
                                    errorMessage += " (HTTP " + statusCode + ")";

                                    if (error.networkResponse.data != null) {
                                        try {
                                            String responseBody = new String(error.networkResponse.data, "utf-8");
                                            Log.e("DELETE_TEACHER", "Error response body: " + responseBody);

                                            if (responseBody.contains("<html>") || responseBody.contains("<!DOCTYPE")) {
                                                errorMessage = "Server error (check logs)";
                                            } else if (responseBody.contains("Fatal error") || responseBody.contains("Parse error")) {
                                                errorMessage = "PHP script error";
                                            }
                                        } catch (Exception e) {
                                            Log.e("DELETE_TEACHER", "Could not parse error response: " + e.getMessage());
                                        }
                                    }
                                } else {
                                    Log.e("DELETE_TEACHER", "Network error - no response");
                                }

                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                            });

                    Volley.newRequestQueue(requireContext()).add(request);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeTeacherFromList(int teacherId) {
        if (teacherAdapter != null && allTeachers != null) {
            Teacher teacherToRemove = null;
            for (Teacher teacher : allTeachers) {
                if (teacher.getId() == teacherId) {
                    teacherToRemove = teacher;
                    break;
                }
            }

            if (teacherToRemove != null) {
                allTeachers.remove(teacherToRemove);

                teacherAdapter.remove(teacherToRemove);
                teacherAdapter.notifyDataSetChanged();

                Log.d("DELETE_TEACHER", "Removed teacher with ID " + teacherId + " from local list");
            }
        }
    }

    private void refreshTeacherList() {
        Log.d("DELETE_TEACHER", "Refreshing teacher list from server...");

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "http://10.0.2.2:80/school_backend/teacher.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("DELETE_TEACHER", "Received updated teacher list from server");

                    allTeachers.clear();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Teacher teacher = new Teacher(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("email"),
                                    obj.getString("gender"),
                                    obj.getString("subject_name")
                            );
                            allTeachers.add(teacher);
                        } catch (JSONException exception) {
                            Log.e("DELETE_TEACHER", "JSON parsing error: " + exception.getMessage());
                        }
                    }

                    // Update the adapter with fresh data
                    if (teacherAdapter != null) {
                        teacherAdapter.clear();
                        teacherAdapter.addAll(allTeachers);
                        teacherAdapter.notifyDataSetChanged();

                        Log.d("DELETE_TEACHER", "Teacher list updated successfully. Total teachers: " + allTeachers.size());
                    }

                    // Show updated count
                    if (allTeachers.isEmpty()) {
                        Toast.makeText(requireContext(), "No teachers found", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("DELETE_TEACHER", "Teacher list refreshed with " + allTeachers.size() + " teachers");
                    }

                }, error -> {
            Log.e("DELETE_TEACHER", "Error refreshing teacher list: " + error.toString());
            Toast.makeText(requireContext(), "Error refreshing teacher list", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }

}