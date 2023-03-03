package com.example.socialnetword.Model;

public class Posts {
    public String uid;
    public String postid;
    public String image;
    public String publisher;
    public String date;
    public String time;
    public String description;
    public String profileimage;
    public String country;
    public String title;
    public String like;

    public Posts() {
    }

    public Posts(String uid, String postid, String image, String publisher,
                 String date, String time, String description, String profileimage,String country,String title,String like) {
        this.uid = uid;
        this.postid = postid;
        this.image = image;
        this.publisher = publisher;
        this.date = date;
        this.time = time;
        this.description = description;
        this.profileimage = profileimage;
        this.country = country;
        this.title = title;
        this.like = like;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}