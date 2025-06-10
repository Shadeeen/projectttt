package com.example.project;

public class ExamItem {
    private String subject;
    private String type;
    private String date;
    private String material;

    public ExamItem(String subject, String type, String date, String material) {
        this.subject = subject;
        this.type = type;
        this.date = date;
        this.material = material;
    }

    public ExamItem(String subject, String type, String date) {
        this.subject = subject;
        this.type = type;
        this.date = date;
    }



    public String getSubject() {
        return subject;
    }
    public String getType() {
        return type;
    }
    public String getDate() {
        return date;
    }
    public String getMaterial() {
        return material;
    }
}