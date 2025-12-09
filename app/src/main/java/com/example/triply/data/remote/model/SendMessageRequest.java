package com.example.triply.data.remote.model;

public class SendMessageRequest {
    private Integer threadId;
    private String message;
    private String jsonSchema;

    public SendMessageRequest(Integer threadId, String message) {
        this.threadId = threadId;
        this.message = message;
    }

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
    }
}

