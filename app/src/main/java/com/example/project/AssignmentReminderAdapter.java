package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AssignmentReminderAdapter extends BaseAdapter {

    private Context context;
    private List<AssignmentItem> assignmentList;

    public AssignmentReminderAdapter(Context context, List<AssignmentItem> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
    }

    @Override
    public int getCount() {
        return assignmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return assignmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView subjectName, txtTitle, dueDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.reminder_item, parent, false);
            holder = new ViewHolder();
            holder.subjectName = convertView.findViewById(R.id.subjectName);
            holder.txtTitle = convertView.findViewById(R.id.txtTitle);
            holder.dueDate = convertView.findViewById(R.id.dueDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AssignmentItem item = assignmentList.get(position);
        holder.subjectName.setText(item.getSubject());
        holder.txtTitle.setText(item.getTitle());
        holder.dueDate.setText("Due: " + item.getDueDate());

        return convertView;
    }
}
