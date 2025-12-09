package com.example.triply.data.remote.model;

import java.util.List;

public class SendMessageResponse {
    private List<Choice> choices;
    private boolean usedRAG;

    public static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    public static class Message {
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public boolean isUsedRAG() {
        return usedRAG;
    }

    public void setUsedRAG(boolean usedRAG) {
        this.usedRAG = usedRAG;
    }

    public String getAssistantMessage() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }
}

