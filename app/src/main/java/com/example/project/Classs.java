package com.example.project;

public class Classs {
    private int id;
    private String name;
    private int numberOfStudents;

    private String homeTeacher;

    public Classs(int id,String name,int numberOfStudents) {
        this.id = id;
        this.name=name;
        this.numberOfStudents=numberOfStudents;
    }

    public Classs(int id,String name,int numberOfStudents,String homeTeacher) {
        this.id = id;
        this.name=name;
        this.numberOfStudents=numberOfStudents;
        this.homeTeacher=homeTeacher;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public String getHomeTeacher() {
        return homeTeacher;
    }

    public void setHomeTeacher(String homeTeacher) {
        this.homeTeacher = homeTeacher;
    }
}
