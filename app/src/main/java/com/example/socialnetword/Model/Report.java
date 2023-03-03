package com.example.socialnetword.Model;

public class Report {
    private String id;
    private String uid;
    private String reason;
    private String postid;
    private String date;
    private String time;

    public Report() {
    }

    public Report(String id, String uid, String reason, String postid, String date, String time) {
        this.id = id;
        this.uid = uid;
        this.reason = reason;
        this.postid = postid;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
