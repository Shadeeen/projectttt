package com.example.project;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String name;
    private int hoursPerWeek;
    private int iconResId;
    private int id;
    public Subject(int id,String name, int hoursPerWeek, int iconResId) {
        this.name = name;
        this.hoursPerWeek = hoursPerWeek;
        this.iconResId = iconResId;
        this.id= id;
    }

    Subject(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public int getIconResId() { return iconResId; }

    public void setName(String name) {
        this.name = name;
    }

    public void setHoursPerWeek(int hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
