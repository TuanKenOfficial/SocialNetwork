package com.example.socialnetword.Model;

public class Users {
    private String uid;
    private String email;
    private String password;
    private String username;
    private String fullname;
    private String country;
    private String relationshipstatus;
    private String status;
    private String profileimage;
    boolean isBlocked = false;


    public Users() {
    }

    public Users(String uid, String email, String password, String username, String fullname, String country, String relationshipstatus, String status, String profileimage, boolean isBlocked) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.username = username;
        this.fullname = fullname;
        this.country = country;
        this.relationshipstatus = relationshipstatus;
        this.status = status;
        this.profileimage = profileimage;
        this.isBlocked = isBlocked;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRelationshipstatus() {
        return relationshipstatus;
    }

    public void setRelationshipstatus(String relationshipstatus) {
        this.relationshipstatus = relationshipstatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
