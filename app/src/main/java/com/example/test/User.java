package com.example.test;

public class User {


    String Uid;
    String neckname;
    String photoUrl;



    public User(String Uid,String neckname, String photoUrl) {
        this.Uid= Uid;
        this.neckname = neckname;
        this.photoUrl = photoUrl;
    }
    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getNeckname() {
        return neckname;
    }

    public void setNeckname(String neckname) {
        this.neckname = neckname;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


}

