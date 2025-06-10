package com.example.project;

import androidx.annotation.NonNull;


public class Student {
    private int id;
    private String name;
    private String email;
    private String gender;

    private String class_name;



    public Student(int id,String name) {
        this.name = name;
        this.id=id;
    }

    public Student(int id, String name, String email, String gender,String class_name) {
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

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
}
