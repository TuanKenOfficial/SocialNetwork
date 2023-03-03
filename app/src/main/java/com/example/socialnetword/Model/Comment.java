package com.example.socialnetword.Model;

public class Comment {
    private String id;
    private String comment;
    private String publisher;
    private String country;

    public Comment(String id, String comment, String publisher, String country) {
        this.id = id;
        this.comment = comment;
        this.publisher = publisher;
        this.country = country;

    }
    public Comment(){
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
