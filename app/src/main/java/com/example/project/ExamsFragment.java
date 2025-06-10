package com.example.project;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class ExamsFragment extends Fragment {
    public ExamsFragment() {}

    private RecyclerView recyclerView;
    private RequestQueue queue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exams_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerExam);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        queue = Volley.newRequestQueue(getContext());
        exams();
        return view;
    }

    private void exams(){
        String url = "http://10.0.2.2:80/school_backend/exam.php?user_id=" + MainActivityStudent.user_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<ExamItem> exam = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String type = obj.getString("type");
                        String date = obj.getString("date");
                        String material = obj.getString("material");
                        String subjectName = obj.getString("subject_name");
                        ExamItem item = new ExamItem(subjectName, type, date, material);
                        exam.add(item);
                    }catch(JSONException exception){
                        Log.d("volley_error", exception.toString());
                    }
                }
                ExamAdapter adapter = new ExamAdapter(getContext(), exam);
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
}