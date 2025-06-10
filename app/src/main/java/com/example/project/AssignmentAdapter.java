package com.example.project;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private final Context context;
    private final List<AssignmentItem> assignments;
    private static final int FILE_PICKER_REQUEST = 101;

    public AssignmentAdapter(Context context, List<AssignmentItem> assignments) {
        this.context = context;
        this.assignments = assignments;
    }

    private final int[] backgroundColors = {
            0xFFA8D0E6,
            0xFFE8D1B3,
            0xFFD2E8E5,
            0xFF7DA6B4,
            0xFFB7D1DF,
            0xFFA3D9B1,
            0xFFD3D3D3,
            0xFF3B4C64
    };

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ass_item, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentItem item = assignments.get(position);
        holder.txtSubject.setText(item.subject);
        holder.txtDate.setText(item.dueDate);
        int bgColor = backgroundColors[position % backgroundColors.length];
        holder.cardView.setCardBackgroundColor(bgColor);

        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(item.title);
            builder.setMessage("Due: " + item.dueDate + "\n\nDescription: " + item.description);
            builder.setPositiveButton("Upload File", (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                ((AssignmentActivity) context).startActivityForResult(intent, FILE_PICKER_REQUEST);
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView txtSubject, txtDate;
        CardView cardView;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtDate = itemView.findViewById(R.id.txtDate);
            cardView = (CardView) itemView;
        }
    }
}