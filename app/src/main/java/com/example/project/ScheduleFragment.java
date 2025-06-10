package com.example.project;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {
    private TableLayout scheduleTable;
    private LinearLayout teacherSubjectList;
    private EditText notesEditText;
    private int classId;
    private RequestQueue requestQueue;

    private TextView[][] scheduleSlots;
    private String[] days = {"mon", "tue", "wed", "thu", "sat"};
    private int totalTimeSlots = 8;

    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(int classId) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt("class_id", classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classId = getArguments().getInt("class_id");
        }
        requestQueue = Volley.newRequestQueue(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        initializeViews(view);
        initializeScheduleSlots(view);
        loadScheduleData();

        return view;
    }

    private void initializeViews(View view) {
        scheduleTable = view.findViewById(R.id.scheduleTable);
        teacherSubjectList = view.findViewById(R.id.teacherSubjectList);
    }

    private void initializeScheduleSlots(View view) {
        scheduleSlots = new TextView[days.length][totalTimeSlots];

        for (int dayIndex = 0; dayIndex < days.length; dayIndex++) {
            for (int timeSlot = 0; timeSlot < totalTimeSlots; timeSlot++) {
                String viewId = days[dayIndex] + "_" + timeSlot;
                int id = getResources().getIdentifier(viewId, "id", getContext().getPackageName());
                scheduleSlots[dayIndex][timeSlot] = view.findViewById(id);

                if (scheduleSlots[dayIndex][timeSlot] != null) {
                    int finalDayIndex = dayIndex;
                    int finalTimeSlot = timeSlot;
                    scheduleSlots[dayIndex][timeSlot].setOnClickListener(v -> showSubjectDialog(finalDayIndex, finalTimeSlot));
                } else {
                    Log.e("ScheduleFragment", "TextView is null for ID: " + viewId);
                }
            }
        }
    }

    private void showSubjectDialog(int dayIndex, int timeSlot) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.add_to_schedule, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        TextView time = dialogView.findViewById(R.id.time);
        Spinner subjectSpinner = dialogView.findViewById(R.id.subjectSpinnersch);

        String[] timeRange = getTimeFromSlot(timeSlot);
        String startTime = timeRange[0];
        String endTime = timeRange[1];

        String fullDayName = getDayName(dayIndex);
        String shortDayName = days[dayIndex];

        TextView currentCell = scheduleSlots[dayIndex][timeSlot];
        boolean isEditing = !currentCell.getText().toString().trim().isEmpty();
        String currentSubject = isEditing ? currentCell.getText().toString() : "";


        String dialogTitle = isEditing ?
                shortDayName + " (" + startTime + "_" + endTime + ") - Currently: " + currentSubject :
                shortDayName + " (" + startTime + "_" + endTime + ")";
        time.setText(dialogTitle);
        loadClassSubjects(subjectSpinner, fullDayName, startTime, endTime, dayIndex, timeSlot, dialog,isEditing);



        dialog.show();
    }

    private void loadClassSubjects(Spinner subjectSpinner, String day, String startTime, String endTime,
                                   int dayIndex, int timeSlot, AlertDialog dialog, boolean isEditing) {
        String url = "http://10.0.2.2/school_backend/subjectAvalabile.php";

        JSONObject params = new JSONObject();
        try {
            params.put("class_id", classId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray subjects = response.getJSONArray("subjects");
                            setupSubjectSpinner(subjects, subjectSpinner, day, startTime, endTime, dayIndex, timeSlot, dialog,isEditing);
                        } else {
                            showError("No subjects available for this class");
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        showError("Error loading subjects");
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                },
                error -> {
                    showError("Network error loading subjects");
                    Log.e("ScheduleFragment", "Network error", error);
                    dialog.dismiss();
                });

        requestQueue.add(request);
    }

    private void setupSubjectSpinner(JSONArray subjects, Spinner subjectSpinner, String day, String startTime,
                                     String endTime, int dayIndex, int timeSlot, AlertDialog dialog, boolean isEditing) {
        try {
            List<String> subjectNames = new ArrayList<>();
            List<JSONObject> subjectObjects = new ArrayList<>();


            subjectNames.add("Select Subject");
            subjectObjects.add(null);

            for (int i = 0; i < subjects.length(); i++) {
                JSONObject subject = subjects.getJSONObject(i);
                String subjectName = subject.getString("subject_name");
                String subjectCode = subject.getString("subject_code");
                int timesPerWeek = subject.getInt("times_per_week");

                String displayName = subjectName + " (" + subjectCode + ") - " + timesPerWeek + " Days/Week";
                subjectNames.add(displayName);
                subjectObjects.add(subject);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, subjectNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(adapter);

            Button addButton = dialog.findViewById(R.id.addsubtosch);
            if (addButton != null) {
                addButton.setText(isEditing ? "Update" : "Add");
                addButton.setOnClickListener(v -> {
                    int selectedPosition = subjectSpinner.getSelectedItemPosition();

                    if (selectedPosition == 0) {
                        Toast.makeText(getContext(), "Please select a subject", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject selectedSubject = subjectObjects.get(selectedPosition);
                    if (selectedSubject != null) {
                        if (isEditing) {

                            updateScheduleEntry(selectedSubject, day, startTime, endTime, dayIndex, timeSlot);
                        } else {
                            checkAndAssignTeacher(selectedSubject, day, startTime, endTime, dayIndex, timeSlot);
                        }
                        dialog.dismiss();
                    }
                });
            }

            if (isEditing) {
                Button deleteButton = dialog.findViewById(R.id.deleteButton);
                if (deleteButton != null) {
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setOnClickListener(v -> {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Delete Schedule")
                                .setMessage("Are you sure you want to remove this subject from the schedule?")
                                .setPositiveButton("Delete", (confirmDialog, which) -> {
                                    deleteScheduleEntry(day, startTime, dayIndex, timeSlot);
                                    dialog.dismiss();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    });
                }
            }

        } catch (JSONException e) {
            showError("Error processing subjects data");
            e.printStackTrace();
            dialog.dismiss();
        }

    }

    private void updateScheduleEntry(JSONObject subject, String day, String startTime, String endTime,
                                     int dayIndex, int timeSlot) {
        String url = "http://10.0.2.2/school_backend/updateScheduleEntry.php";

        try {
            JSONObject params = new JSONObject();
            params.put("class_id", classId);
            params.put("subject_id", subject.getInt("subject_id"));
            params.put("day", day);
            params.put("start_time", startTime);
            params.put("end_time", endTime);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {

                                String subjectCode = subject.getString("subject_code");

                                scheduleSlots[dayIndex][timeSlot].setText(subjectCode);
                                scheduleSlots[dayIndex][timeSlot].setBackgroundColor(
                                        ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));

                                String teacherName = response.getString("teacher_name");
                                Toast.makeText(getContext(), "Updated to " + subject.getString("subject_name") +
                                        " with " + teacherName, Toast.LENGTH_SHORT).show();


                                loadScheduleData();
                            } else {

                                String message = response.getString("message");
                                Toast.makeText(getContext(), "Cannot update: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            showError("Error processing update response");
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        showError("Network error updating schedule");
                        Log.e("ScheduleFragment", "Network error", error);
                    });

            requestQueue.add(request);

        } catch (JSONException e) {
            showError("Error preparing update request");
            e.printStackTrace();
        }
    }

    private void deleteScheduleEntry(String day, String startTime, int dayIndex, int timeSlot) {
        String url = "http://10.0.2.2/school_backend/deleteScheduleEntry.php";

        try {
            JSONObject params = new JSONObject();
            params.put("class_id", classId);
            params.put("day", day);
            params.put("start_time", startTime);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {

                                scheduleSlots[dayIndex][timeSlot].setText("");
                                scheduleSlots[dayIndex][timeSlot].setBackgroundColor(
                                        ContextCompat.getColor(getContext(), android.R.color.transparent));

                                Toast.makeText(getContext(), "Schedule entry deleted", Toast.LENGTH_SHORT).show();


                                loadScheduleData();
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), "Cannot delete: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            showError("Error processing delete response");
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        showError("Network error deleting schedule");
                        Log.e("ScheduleFragment", "Network error", error);
                    });

            requestQueue.add(request);

        } catch (JSONException e) {
            showError("Error preparing delete request");
            e.printStackTrace();
        }
    }
    private void checkAndAssignTeacher(JSONObject subject, String day, String startTime, String endTime,
                                       int dayIndex, int timeSlot) {
        String url = "http://10.0.2.2/school_backend/checkAndAssignTeacher.php";

        try {
            JSONObject params = new JSONObject();
            params.put("class_id", classId);
            params.put("subject_id", subject.getInt("subject_id"));
            params.put("day", day);  // This now contains full day name like "Monday"
            params.put("start_time", startTime);
            params.put("end_time", endTime);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                String subjectCode = subject.getString("subject_code");

                                scheduleSlots[dayIndex][timeSlot].setText(subjectCode);
                                scheduleSlots[dayIndex][timeSlot].setBackgroundColor(
                                        ContextCompat.getColor(getContext(), android.R.color.holo_green_light));

                                String teacherName = response.getString("teacher_name");
                                Toast.makeText(getContext(), "Added " + subject.getString("subject_name") +
                                        " with " + teacherName, Toast.LENGTH_SHORT).show();


                                loadScheduleData();
                            } else {

                                String message = response.getString("message");
                                Toast.makeText(getContext(), "Cannot add: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            showError("Error processing response");
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        showError("Network error checking teacher availability");
                        Log.e("ScheduleFragment", "Network error", error);
                    });

            requestQueue.add(request);

        } catch (JSONException e) {
            showError("Error preparing request");
            e.printStackTrace();
        }
    }

    private String getDayName(int dayIndex) {

        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Saturday"};
        return dayNames[dayIndex];
    }

    private String[] getTimeFromSlot(int timeSlot) {
        switch (timeSlot) {
            case 0: return new String[]{"08:00", "08:45"};
            case 1: return new String[]{"08:45", "09:30"};
            case 2: return new String[]{"09:30", "10:15"};
            case 3: return new String[]{"10:15", "11:00"};
            case 4: return new String[]{"11:00", "11:45"};
            case 5: return new String[]{"11:45", "12:30"};
            case 6: return new String[]{"12:30", "13:15"};
            case 7: return new String[]{"13:15", "14:00"};
            default: return new String[]{"", ""};
        }
    }
    private void loadScheduleData() {
        String url = "http://10.0.2.2/school_backend/getSchedule.php";

        JSONObject params = new JSONObject();
        try {
            params.put("class_id", classId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray scheduleData = response.getJSONArray("schedule");
                            JSONArray teacherSubjects = response.getJSONArray("teacher_subjects");

                            displaySchedule(scheduleData);
                            displayTeacherSubjects(teacherSubjects);
                        } else {
                            showError("Failed to load schedule: " + response.getString("message"));
                        }
                    } catch (JSONException e) {
                        showError("Error parsing schedule data");
                        e.printStackTrace();
                    }
                },
                error -> {
                    showError("Network error: " + error.getMessage());
                    error.printStackTrace();
                });

        requestQueue.add(request);
    }

    private void displaySchedule(JSONArray scheduleData) {
        try {
            clearScheduleTable();

            Log.d("ScheduleFragment", "displaySchedule called with " + scheduleData.length() + " items");

            for (int i = 0; i < scheduleData.length(); i++) {
                JSONObject schedule = scheduleData.getJSONObject(i);

                String day = schedule.getString("day").toLowerCase();
                String startTime = schedule.getString("start_time");
                String subjectName = schedule.getString("subject_name");

                Log.d("ScheduleFragment", "Processing: Day=" + day + ", Time=" + startTime + ", Subject=" + subjectName);

                int dayIndex = getDayIndex(day);
                int timeSlotIndex = getTimeSlotIndex(startTime);

                Log.d("ScheduleFragment", "Indices: dayIndex=" + dayIndex + ", timeSlotIndex=" + timeSlotIndex);

                if (dayIndex != -1 && timeSlotIndex != -1) {
                    // Get first 3 letters of subject name
                    String shortSubject = subjectName.length() >= 3 ?
                            subjectName.substring(0, 3).toUpperCase() :
                            subjectName.toUpperCase();

                    Log.d("ScheduleFragment", "Short subject: " + shortSubject);

                    if (scheduleSlots[dayIndex][timeSlotIndex] != null) {
                        scheduleSlots[dayIndex][timeSlotIndex].setText(shortSubject);
                        scheduleSlots[dayIndex][timeSlotIndex].setBackgroundColor(
                                ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                        Log.d("ScheduleFragment", "SUCCESS: Set " + shortSubject + " at [" + dayIndex + "][" + timeSlotIndex + "]");
                    } else {
                        Log.e("ScheduleFragment", "ERROR: TextView is null at [" + dayIndex + "][" + timeSlotIndex + "]");
                    }
                } else {
                    Log.w("ScheduleFragment", "SKIPPED: Invalid indices for " + day + " " + startTime);
                }
            }

            Log.d("ScheduleFragment", "displaySchedule completed");

        } catch (JSONException e) {
            Log.e("ScheduleFragment", "JSON Error in displaySchedule", e);
            e.printStackTrace();
        }
    }

    private void displayTeacherSubjects(JSONArray teacherSubjects) {
        try {
            teacherSubjectList.removeAllViews();

            for (int i = 0; i < teacherSubjects.length(); i++) {
                JSONObject item = teacherSubjects.getJSONObject(i);

                String teacherName = item.getString("teacher_name");
                String subjectName = item.getString("subject_name");
                String subjectCode = item.getString("subject_code");

                View teacherSubjectView = createTeacherSubjectView(teacherName, subjectName, subjectCode);
                teacherSubjectList.addView(teacherSubjectView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearScheduleTable() {
        for (int dayIndex = 0; dayIndex < days.length; dayIndex++) {
            for (int timeSlot = 0; timeSlot < totalTimeSlots; timeSlot++) {
                if (scheduleSlots[dayIndex][timeSlot] != null) {
                    scheduleSlots[dayIndex][timeSlot].setText("");
                    scheduleSlots[dayIndex][timeSlot].setBackgroundColor(
                            ContextCompat.getColor(getContext(), android.R.color.transparent));
                }
            }
        }
    }

    private int getDayIndex(String day) {
        switch (day.toLowerCase()) {
            case "monday": return 0;
            case "tuesday": return 1;
            case "wednesday": return 2;
            case "thursday": return 3;
            case "saturday": return 4;
            case "sunday":
                Log.w("ScheduleFragment", "Sunday not supported in current table layout");
                return -1;
            case "friday":
                Log.w("ScheduleFragment", "Friday not supported in current table layout");
                return -1;
            default:
                Log.w("ScheduleFragment", "Unknown day: " + day);
                return -1;
        }
    }

    private int getTimeSlotIndex(String startTime) {

        switch (startTime) {
            case "08:00": return 0;
            case "08:45": return 1;
            case "09:30": return 2;
            case "10:15": return 3;
            case "11:00": return 4;
            case "11:45": return 5;
            case "12:30": return 6;
            case "13:15": return 7;
            default:
                Log.w("ScheduleFragment", "Unknown start time: " + startTime);
                return -1;
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    public View createTeacherSubjectView(String teacherName, String subjectName, String subjectCode) {
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 8, 16, 8);
        container.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.table_row));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 8);
        container.setLayoutParams(params);

        LinearLayout headerLayout = new LinearLayout(getContext());
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout teacherInfoLayout = new LinearLayout(getContext());
        teacherInfoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        teacherInfoLayout.setLayoutParams(infoParams);

        TextView teacherTextView = new TextView(getContext());
        teacherTextView.setText("Teacher: " + teacherName);
        teacherTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        teacherTextView.setTextSize(14);

        TextView subjectTextView = new TextView(getContext());
        subjectTextView.setText(subjectName + " (" + subjectCode + ")");
        subjectTextView.setTextSize(15);
        subjectTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text));

        teacherInfoLayout.addView(teacherTextView);
        teacherInfoLayout.addView(subjectTextView);


        Button viewScheduleButton = new Button(getContext());
        viewScheduleButton.setBackgroundColor(Color.blue(10));
        viewScheduleButton.setText("View Schedule");
        viewScheduleButton.setTextSize(13);
        viewScheduleButton.setPadding(12, 8, 12, 8);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.gravity = android.view.Gravity.CENTER_VERTICAL;
        viewScheduleButton.setLayoutParams(buttonParams);


        viewScheduleButton.setOnClickListener(v -> showTeacherScheduleDialog(teacherName));


        headerLayout.addView(teacherInfoLayout);
        headerLayout.addView(viewScheduleButton);


        container.addView(headerLayout);

        return container;
    }

    private void showTeacherScheduleDialog(String teacherName) {
        loadTeacherSchedule(teacherName);
    }

    private void loadTeacherSchedule(String teacherName) {
        String url = "http://10.0.2.2/school_backend/getTeacherSchedule.php";

        JSONObject params = new JSONObject();
        try {
            params.put("teacher_name", teacherName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray scheduleData = response.getJSONArray("schedule");
                            JSONObject teacherInfo = response.getJSONObject("teacher_info");
                            displayTeacherScheduleDialog(teacherName, scheduleData, teacherInfo);
                        } else {
                            showError("Failed to load teacher schedule: " + response.getString("message"));
                        }
                    } catch (JSONException e) {
                        showError("Error parsing teacher schedule data");
                        e.printStackTrace();
                    }
                },
                error -> {
                    showError("Network error loading teacher schedule");
                    Log.e("ScheduleFragment", "Network error", error);
                });

        requestQueue.add(request);
    }

    private void displayTeacherScheduleDialog(String teacherName, JSONArray scheduleData, JSONObject teacherInfo) {
        try {

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.teacher_schedule_dialog, null);


            TextView teacherNameTitle = dialogView.findViewById(R.id.teacherNameTitle);
            TextView teacherInfoText = dialogView.findViewById(R.id.teacherInfoText);
            TextView scheduleTitle = dialogView.findViewById(R.id.scheduleTitle);
            LinearLayout scheduleListContainer = dialogView.findViewById(R.id.scheduleListContainer);
            TextView noScheduleText = dialogView.findViewById(R.id.noScheduleText);
            Button closeButton = dialogView.findViewById(R.id.closeButton);


            teacherNameTitle.setText(teacherName + "'s Schedule");


            String subjectName = teacherInfo.getString("subject_name");
            int totalClasses = teacherInfo.getInt("total_classes");
            int totalHours = scheduleData.length();

            String infoText = "Subject: " + subjectName + "\n" +
                    "Teaching " + totalClasses + " classes\n" +
                    "Total weekly hours: " + totalHours;
            teacherInfoText.setText(infoText);


            scheduleListContainer.removeAllViews();


            if (scheduleData.length() > 0) {
                scheduleTitle.setVisibility(View.VISIBLE);
                noScheduleText.setVisibility(View.GONE);

                for (int i = 0; i < scheduleData.length(); i++) {
                    JSONObject schedule = scheduleData.getJSONObject(i);

                    String day = schedule.getString("day");
                    String startTime = schedule.getString("start_time");
                    String endTime = schedule.getString("end_time");
                    String className = schedule.getString("class_name");
                    String subjectCode = schedule.getString("subject_code");

                    View scheduleItemView = LayoutInflater.from(getContext())
                            .inflate(R.layout.schedule_item, scheduleListContainer, false);


                    TextView dayText = scheduleItemView.findViewById(R.id.dayText);
                    TextView timeText = scheduleItemView.findViewById(R.id.timeText);
                    TextView classNameText = scheduleItemView.findViewById(R.id.classNameText);
                    TextView subjectCodeText = scheduleItemView.findViewById(R.id.subjectCodeText);


                    dayText.setText(day);
                    timeText.setText(startTime + "-" + endTime);
                    classNameText.setText(className);
                    subjectCodeText.setText(subjectCode);


                    scheduleListContainer.addView(scheduleItemView);
                }
            } else {
                scheduleTitle.setVisibility(View.GONE);
                noScheduleText.setVisibility(View.VISIBLE);
            }

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            closeButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();


        } catch (JSONException e) {
            showError("Error displaying teacher schedule");
            e.printStackTrace();
        }
    }}