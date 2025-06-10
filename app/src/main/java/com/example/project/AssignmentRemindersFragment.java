package com.example.project;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssignmentRemindersFragment extends Fragment {

    private ListView listView;
    private RequestQueue queue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_assignment_reminders_fragment, container, false);

        listView = view.findViewById(R.id.assignmentReminderList);
        queue = Volley.newRequestQueue(getContext());
        assignmentReminder();
        return view;
    }

    private void assignmentReminder(){
        String url = "http://10.0.2.2:80/school_backend/assignmentReminder.php?user_id=" + MainActivityStudent.user_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<AssignmentItem> ass = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String subjectName = obj.getString("subject_name");
                        String title = obj.getString("title");
                        String date = obj.getString("due_date");
                        AssignmentItem item = new AssignmentItem(title, date, subjectName);
                        ass.add(item);
                    }catch(JSONException exception){
                        Log.d("volley_error", exception.toString());
                    }
                }
                AssignmentReminderAdapter adapter = new AssignmentReminderAdapter(getContext(), ass);
                listView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley_error", error.toString());
            }
        });

        queue.add(request);
    }
}