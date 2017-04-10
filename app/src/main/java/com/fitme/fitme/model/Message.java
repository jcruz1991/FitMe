package com.fitme.fitme.model;

public class Message {

    private String textMessage;
    private String name;

    public Message() {
    }

    public Message(String name, String textMessage) {
        this.textMessage = textMessage;
        this.name = name;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
