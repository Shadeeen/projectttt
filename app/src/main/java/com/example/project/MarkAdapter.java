package com.example.project;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarkAdapter extends BaseAdapter {

    private final List<MarkItem> data;
    private final Context context;
    private final Set<Integer> expandedPositions = new HashSet<>();

    private final int[] backgroundColors = {
            0xFFE3F2FD,
            0xFFFFFFFF
    };

    public MarkAdapter(Context context, List<MarkItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.mark_item, parent, false);
        TextView txtSubject = view.findViewById(R.id.txtSubject);
        TextView txtDetails = view.findViewById(R.id.txtDetails);
        LinearLayout detailsLayout = view.findViewById(R.id.detailsLayout);
        LinearLayout itemLayout = view.findViewById(R.id.itemLayout);
        int bgColor = backgroundColors[position % backgroundColors.length];
        itemLayout.setBackgroundColor(bgColor);


        MarkItem item = data.get(position);
        txtSubject.setText(item.subject + " - " + item.totalMark + "/100");
        txtDetails.setText(item.details);

        view.setOnClickListener(v -> {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position);
                detailsLayout.setVisibility(View.GONE);
            } else {
                expandedPositions.add(position);
                detailsLayout.setVisibility(View.VISIBLE);
            }
        });

        if (expandedPositions.contains(position)) {
            detailsLayout.setVisibility(View.VISIBLE);
        } else {
            detailsLayout.setVisibility(View.GONE);
        }

        return view;
    }
}