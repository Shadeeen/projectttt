package com.example.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class classesFragment extends Fragment implements OnClassActionListener {

    RecyclerView recyclerView;
    classAdapter adapter;
    List<Classs> classList;

    public classesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.classRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        classList = new ArrayList<>();
        adapter = new classAdapter(classList);


        adapter.setOnClassActionListener(this);

        recyclerView.setAdapter(adapter);

        loadClasses();
    }

    private void loadClasses() {
        String url = "http://10.0.2.2/school_backend/allClasse.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    classList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            int numStudents = obj.getInt("number_of_students");
                            String teacher = !obj.getString("teacher_name").equals("null") ?
                                    obj.getString("teacher_name") : "Not Assigned";

                            classList.add(new Classs(id, name, numStudents, teacher));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error loading classes", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }


    @Override
    public void onScheduleClick(int classId, String className) {
        ScheduleFragment scheduleFragment = ScheduleFragment.newInstance(classId,className);

        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.container, scheduleFragment);
        ft.addToBackStack("schedule");
        ft.commit();

        Toast.makeText(getContext(), "Opening schedule for " + className, Toast.LENGTH_SHORT).show();
    }

    public void onClearClick(int classId, String className) {
        new AlertDialog.Builder(getContext())
                .setTitle("Clear Class")
                .setMessage("Are you sure you want to clear ALL schedules and students for " + className + "?\n\nThis action cannot be undone!")
                .setPositiveButton("Yes, Clear All", (dialog, which) -> {
                    clearClassData(classId, className);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearClassData(int classId, String className) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Clearing class data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://10.0.2.2:80/school_backend/clearClass.php?class_id=" + classId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(getContext(), "Successfully cleared " + className, Toast.LENGTH_LONG).show();

                            loadClasses();


                        } else {
                            Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(getContext()).add(request);
    }

    @Override
    public void onClassClick(int classId, String className) {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.container, new StudentListFragment(classId,className));
        ft.addToBackStack(null);
        ft.commit();
    }
}