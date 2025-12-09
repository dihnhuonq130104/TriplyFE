package com.example.triply.data.remote.model;

public class CreateThreadRequest {
    private String title;
    private String systemPrompt;

    public CreateThreadRequest(String title) {
        this.title = title;
    }

    public CreateThreadRequest(String title, String systemPrompt) {
        this.title = title;
        this.systemPrompt = systemPrompt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}

