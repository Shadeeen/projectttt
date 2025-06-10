package com.example.project;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ScheduleItem> scheduleList;

    public ScheduleAdapter(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
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
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item_student, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);
        holder.txtSubject.setText(item.subject);
        holder.txtTime.setText(item.time);
        int bgColor = backgroundColors[position % backgroundColors.length];
        holder.cardView.setCardBackgroundColor(bgColor);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView txtSubject, txtTime;
        CardView cardView;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtTime = itemView.findViewById(R.id.txtTime);
            cardView = (CardView) itemView;
        }
    }

    public void updateData(ArrayList<ScheduleItem> newData) {
        this.scheduleList = newData;
        notifyDataSetChanged();
    }
}
