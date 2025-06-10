package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    private Context context;
    private List<ExamItem> examList;

    public ExamAdapter(Context context, List<ExamItem> examList) {
        this.context = context;
        this.examList = examList;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exam_item, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        ExamItem exam = examList.get(position);
        holder.subject.setText(exam.getSubject());
        holder.type.setText("Type: " + exam.getType());
        holder.date.setText("Date: " + exam.getDate());
        holder.material.setText("Material: " + exam.getMaterial());
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView subject, type, date, material;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.txtSubject);
            type = itemView.findViewById(R.id.txtType);
            date = itemView.findViewById(R.id.txtDate);
            material = itemView.findViewById(R.id.txtMaterial);
        }
    }
}
