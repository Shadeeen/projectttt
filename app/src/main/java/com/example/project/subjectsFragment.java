package com.example.project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Objects;

public class subjectsFragment extends Fragment {

    RecyclerView recyclerView;
    SubjectAdapter adapter;
    public subjectsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subjects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.subjectRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        getSubjects();

        FloatingActionButton fab = view.findViewById(R.id.fabAddSubject);


        fab.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_subject, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            EditText nameInput = dialogView.findViewById(R.id.inputSubjectName);
            EditText link=dialogView.findViewById(R.id.bookLin);
            NumberPicker hoursPicker = dialogView.findViewById(R.id.inputSubjectHours);
            hoursPicker.setMinValue(1);
            hoursPicker.setMaxValue(8);
            hoursPicker.setWrapSelectorWheel(true);
            GridLayout iconPicker = dialogView.findViewById(R.id.iconPicker);
            Button btnAdd = dialogView.findViewById(R.id.addSub);

            final int[] selectedIconRes = {R.drawable.globe_book};

            for (int i = 0; i < iconPicker.getChildCount(); i++) {
                View iconView = iconPicker.getChildAt(i);
                iconView.setOnClickListener(icon -> {
                    for (int j = 0; j < iconPicker.getChildCount(); j++) {
                        iconPicker.getChildAt(j).setBackground(null);
                    }
                    iconView.setBackgroundResource(R.drawable.icon_selected_background);
                    String tag = (String) iconView.getTag();
                    selectedIconRes[0] = getResources().getIdentifier(tag, "drawable", requireContext().getPackageName());
                });
            }

            btnAdd.setOnClickListener(btn -> {
                String name = nameInput.getText().toString().trim();
                String linkk=link.getText().toString().trim();
                int hours = hoursPicker.getValue();

                if (selectedIconRes[0] == 0) {
                    Toast.makeText(getContext(), "Please select an icon", Toast.LENGTH_SHORT).show();
                    return;
                }
                String iconName = getResources().getResourceEntryName(selectedIconRes[0]);



                addSubject(name, hours, iconName,linkk, dialog);
            });


        });
}

    private void addSubject(String name, int timesPerWeek, String iconName,String link, AlertDialog dialog) {
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a subject name", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:80/school_backend/addSubject.php";

        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("times_per_week", String.valueOf(timesPerWeek));
        params.put("icon", iconName);
        params.put("link", link);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(getContext(), "Subject added successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getSubjects();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error sending request", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }


    public void getSubjects() {

        String subjectUrl = "http://10.0.2.2:80/school_backend/subjects.php";
        List<Subject> subjects = new ArrayList<>();

        JsonArrayRequest subjectRequest = new JsonArrayRequest(Request.Method.GET, subjectUrl, null,
                response -> {
                    subjects.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Subject s = new Subject();
                            s.setName(obj.getString("subject_name"));
                            s.setHoursPerWeek(obj.getInt("times_per_week"));
                            s.setId(obj.getInt("id"));

                            String iconName = obj.getString("icon");
                            int resId = getResources().getIdentifier(iconName, "drawable", requireContext().getPackageName());
                            if (resId == 0) resId = R.drawable.default_icon; // fallback
                            s.setIconResId(resId);

                            subjects.add(s);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter = new SubjectAdapter(subjects);
                    recyclerView.setAdapter(adapter);

                },
                error -> Toast.makeText(requireContext(), "Error loading subjects", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(subjectRequest);
    }



}