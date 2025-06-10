package com.example.project;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ExamReminderAdapter extends ArrayAdapter<ExamItem> {
    private Context context;
    private List<ExamItem> examList;

    public ExamReminderAdapter(Context context, List<ExamItem> exams) {
        super(context, 0, exams);
        this.context = context;
        this.examList = exams;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.reminder_item, parent, false);
        }

        TextView subjectName = convertView.findViewById(R.id.subjectName);
        TextView type = convertView.findViewById(R.id.txtTitle);
        TextView date = convertView.findViewById(R.id.dueDate);

        ExamItem item = examList.get(position);
        subjectName.setText(item.getSubject());
        type.setText(item.getType());
        date.setText(item.getDate());

        return convertView;
    }
}
