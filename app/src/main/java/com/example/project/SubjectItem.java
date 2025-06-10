package com.example.project;


public class SubjectItem {
    private String name;
    private String bookUrl;

    public SubjectItem(String name, String bookUrl) {
        this.name = name;
        this.bookUrl = bookUrl;
    }

    public String getName() {
        return name;
    }

    public String getBookUrl() {
        return bookUrl;
    }
}