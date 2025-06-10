package com.example.project;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ReminderFragment extends Fragment {

    public ReminderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reminder_fragment, container, false);

        TabLayout tabLayout = view.findViewById(R.id.reminderTabs);
        ViewPager2 viewPager = view.findViewById(R.id.reminderViewPager);

        ReminderPagerAdapter adapter = new ReminderPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Assignments");
                    else tab.setText("Exams");
                }).attach();

        return view;
    }
}