package com.example.project;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SubjectStudentAdapter extends ArrayAdapter<SubjectItem> {

    private Context context;
    private List<SubjectItem> subjects;

    public SubjectStudentAdapter(Context context, List<SubjectItem> subjects) {
        super(context, 0, subjects);
        this.context = context;
        this.subjects = subjects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubjectItem subject = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.subject_item, parent, false);
        }

        TextView txtSubject = convertView.findViewById(R.id.txtSubject);
        TextView viewBookLink = convertView.findViewById(R.id.view_book_link);

        txtSubject.setText(subject.getName());

        viewBookLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(subject.getBookUrl()));
            context.startActivity(intent);
        });

        return convertView;
    }
}