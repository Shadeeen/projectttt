package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TeacherAdapter extends ArrayAdapter<Teacher> {

    public TeacherAdapter(@NonNull Context context, ArrayList<Teacher> teachers) {
        super(context, 0, teachers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Teacher currentTeacher = getItem(position);

        TextView title = listItem.findViewById(R.id.itemTitle);
        TextView subtitle = listItem.findViewById(R.id.itemSubtitle);

        title.setText(currentTeacher.getName());
        subtitle.setText("("+currentTeacher.getSubject()+") Teacher");

        return listItem;
    }
}
