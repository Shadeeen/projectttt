package com.example.project;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class SubjectFragmentStudent extends Fragment {
    public SubjectFragmentStudent() {}

    private ListView listView;
    private RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_subject_student, container, false);
        listView = view.findViewById(R.id.subjectsList);
        queue = Volley.newRequestQueue(getContext());
        subjects();
        return view;
    }

    private void subjects(){
        String url = "http://10.0.2.2:80/school_backend/subject.php?user_id=" + MainActivityStudent.user_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<SubjectItem> subject = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String subjectName = obj.getString("name");
                        String url = obj.getString("book_link");
                        SubjectItem item = new SubjectItem(subjectName, url);
                        subject.add(item);
                    }catch(JSONException exception){
                        Log.d("volley_error", exception.toString());
                    }
                }
                SubjectStudentAdapter adapter = new SubjectStudentAdapter(getContext(), subject);
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