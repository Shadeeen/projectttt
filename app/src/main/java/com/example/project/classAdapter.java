package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Interface for handling class actions
interface OnClassActionListener {
    void onScheduleClick(int classId, String className);
    void onClearClick(int classId, String className);
    void onClassClick(int classId, String className);
}

public class classAdapter extends RecyclerView.Adapter<classAdapter.ClassViewHolder> {

    private final List<Classs> classList;
    private OnClassActionListener listener;

    public classAdapter(List<Classs> classList) {
        this.classList = classList;
    }

    public void setOnClassActionListener(OnClassActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_card, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Classs classItem = classList.get(position);
        holder.className.setText(classItem.getName());
        holder.numberOfStudents.setText("Students: " + classItem.getNumberOfStudents());
        holder.homeTeacher.setText("Teacher: " + classItem.getHomeTeacher());

        // Handle item click to show student list
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClassClick(classItem.getId(), classItem.getName());
            }
        });

        // Handle Schedule button click
        holder.scheduleButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onScheduleClick(classItem.getId(), classItem.getName());
            }
        });

        // Handle Clear button click
        holder.clearButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClearClick(classItem.getId(), classItem.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView className, numberOfStudents, homeTeacher;
        Button scheduleButton, clearButton;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.className);
            numberOfStudents = itemView.findViewById(R.id.numberOfStudents);
            homeTeacher = itemView.findViewById(R.id.homeTeacher);
            scheduleButton = itemView.findViewById(R.id.btnschedule);
            clearButton = itemView.findViewById(R.id.btnclear);
        }
    }
}