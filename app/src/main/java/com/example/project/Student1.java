package com.example.project;

import androidx.annotation.NonNull;


public class Student1 {
    private int id;
    private String name;
    private String email;
    private String gender;

    private String class_name;

    private int classId;
    private boolean present;

    public Student1(int id, String name, int classId) {
        this.id = id;
        this.name = name;
        this.classId = classId;
        this.present = false;
    }

    public Student1(int id,String name) {
        this.name = name;
        this.id=id;
    }

    public Student1(int id, String name, String email, String gender,String class_name) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.class_name=class_name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public int getClassId() { return classId; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getClass_name() {
        return class_name;
    }

    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
}