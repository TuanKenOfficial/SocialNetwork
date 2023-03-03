package com.example.socialnetword.Model;

public class Chats {
    String id;
    String message, receiver, sender, time,date,type,image;
    private boolean isseen;

    public Chats() {
    }

    public Chats(String id, String message, String receiver, String sender, String time, String date, String type, String image, boolean isseen) {
        this.id = id;
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.time = time;
        this.date = date;
        this.type = type;
        this.image = image;
        this.isseen = isseen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
