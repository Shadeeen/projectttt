package com.example.project;

import android.app.AlertDialog;

import android.os.Bundle;
;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StudentListFragment extends Fragment {
    private ListView studentListView;
    private Button selectAllButton, deleteSelectedButton;
    private ArrayAdapter<String> adapter;
    private List<Student> studentList = new ArrayList<>();
    private int classId;
    private String classNme;
private TextView emptyTextView;

    Button clearAllButton;
    TextView classTitle;
    public StudentListFragment(int classId,String className) {
        this.classId = classId;
        this.classNme=className;
        Log.d("" ,"class Name: "+className);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_list, container, false);
        classTitle=view.findViewById(R.id.classTitle);
        classTitle.setText("Students in "+classNme);
        studentListView = view.findViewById(R.id.studentListView);
        selectAllButton = view.findViewById(R.id.selectAllButton);
        deleteSelectedButton = view.findViewById(R.id.deleteSelectedButton);
         clearAllButton = view.findViewById(R.id.clearAllButton);
         emptyTextView = view.findViewById(R.id.emptyTextView);


        fetchStudents();


        clearAllButton.setOnClickListener(v -> {
            for (int i = 0; i < studentListView.getCount(); i++) {
                studentListView.setItemChecked(i, false);
            }
        });
        selectAllButton.setOnClickListener(v -> {
            for (int i = 0; i < studentList.size(); i++) {
                studentListView.setItemChecked(i, true);
            }
        });

      deleteSelectedButton.setOnClickListener(v -> {
                            deleteSelectedStudents();
      });

        FloatingActionButton addStudentFab = view.findViewById(R.id.addStudentFab);
        addStudentFab.setOnClickListener(v -> showAddStudentDialog());


        return view;
    }

    private void fetchStudents() {
        String url = "http://10.0.2.2:80/school_backend/getStudents.php?class_id=" + classId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> studentNames = new ArrayList<>();
                    studentList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            if (i == 0) {
                                classTitle.setText("Students in " + classNme);
                            }

                            Student student = new Student(
                                    obj.getInt("id"),
                                    obj.getString("name")
                            );
                            studentList.add(student);
                            studentNames.add(student.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_list_item_multiple_choice,
                            studentNames);
                    studentListView.setAdapter(adapter);

                    if (studentList.isEmpty()) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        studentListView.setVisibility(View.GONE);
                        deleteSelectedButton.setVisibility(View.GONE);
                        clearAllButton.setVisibility(View.GONE);
                        selectAllButton.setVisibility(View.GONE);


                    } else {
                        emptyTextView.setVisibility(View.GONE);
                        studentListView.setVisibility(View.VISIBLE);
                        deleteSelectedButton.setVisibility(View.VISIBLE);
                        clearAllButton.setVisibility(View.VISIBLE);
                        selectAllButton.setVisibility(View.VISIBLE);
                        ArrayAdapter<Student> adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_list_item_multiple_choice, studentList);
                        studentListView.setAdapter(adapter);
                        studentListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    }

                },
                error -> Toast.makeText(getContext(), "Failed to load students", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }


    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_student, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText nameEditText = dialogView.findViewById(R.id.studentName);
        EditText emailEditText = dialogView.findViewById(R.id.studentEmail);
        RadioGroup genderGroup = dialogView.findViewById(R.id.genderGroup2);
        Button addButton = dialogView.findViewById(R.id.addstu);

        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            int selectedGenderId = genderGroup.getCheckedRadioButtonId();

            if (name.isEmpty() || email.isEmpty() || selectedGenderId == -1) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = ((RadioButton) dialogView.findViewById(selectedGenderId)).getText().toString();

            String url = "http://10.0.2.2:80/school_backend/addStudent.php";

            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("email", email);
            params.put("gender", gender);
            params.put("class_id", String.valueOf(classId));

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            if ("success".equals(status)) {
                                dialog.dismiss();
                                fetchStudents();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Unexpected response format", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        });

        dialog.show();
    }

    private void deleteSelectedStudents() {
        List<Integer> toDelete = new ArrayList<>();

        SparseBooleanArray checkedItems = studentListView.getCheckedItemPositions();
        for (int i = 0; i < studentList.size(); i++) {
            if (checkedItems.get(i)) {
                toDelete.add(studentList.get(i).getId());
            }
        }

        if (toDelete.isEmpty()) {
            Toast.makeText(getContext(), "No students selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simple confirmation
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Students")
                .setMessage("Delete " + toDelete.size() + " selected student(s)?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteStudents(toDelete);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteStudents(List<Integer> studentIds) {
        Toast.makeText(getContext(), "Deleting " + studentIds.size() + " students...", Toast.LENGTH_SHORT).show();

        AtomicInteger completedRequests = new AtomicInteger(0);
        final int totalRequests = studentIds.size();

        for (int studentId : studentIds) {
            String url = "http://10.0.2.2:80/school_backend/deleteStudent.php?id=" + studentId+"&class_id="+classId;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        int completed = completedRequests.incrementAndGet();
                        Log.d("DELETE", "Completed " + completed + " of " + totalRequests + " deletions");

                        if (completed == totalRequests) {
                            Log.d("DELETE", "All deletions completed, refreshing list");

                            // Clear selections and refresh
                            studentListView.clearChoices();
                            fetchStudents();
                            Toast.makeText(getContext(), "Successfully deleted " + totalRequests + " student(s)", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("DELETE", "Failed to delete student " + studentId + ": " + error.toString());

                        // Still count failed requests to avoid hanging
                        int completed = completedRequests.incrementAndGet();
                        Log.d("DELETE", "Request completed (failed) " + completed + " of " + totalRequests);

                        if (completed == totalRequests) {
                            Log.d("DELETE", "All requests completed, refreshing list");
                            studentListView.clearChoices();
                            fetchStudents();
                            Toast.makeText(getContext(), "Deletion completed (some may have failed)", Toast.LENGTH_SHORT).show();
                        }
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        }
    }
}
