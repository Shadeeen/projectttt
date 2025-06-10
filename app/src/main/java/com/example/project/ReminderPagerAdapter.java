package com.example.project;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReminderPagerAdapter extends FragmentStateAdapter {

    public ReminderPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new AssignmentRemindersFragment() : new ExamRemindersFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}