package com.example.project;


public class AssignmentItem {
    public String title;
    public String dueDate;
    public String description;
    public String subject;


    public AssignmentItem(String title, String dueDate, String description, String subject) {
        this.title = title;
        this.dueDate = dueDate;
        this.description = description;
        this.subject = subject;
    }


    public AssignmentItem(String title, String dueDate, String subject) {
        this.title = title;
        this.dueDate = dueDate;
        this.subject = subject;
    }


    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public String getSubject() {
        return subject;
    }
}