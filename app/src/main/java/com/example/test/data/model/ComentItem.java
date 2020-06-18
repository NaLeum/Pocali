package com.example.test.data.model;

import java.util.Date;

public class ComentItem {




    String comentid;

    String username;
    String coment;
    String userid;
    String postid;
    private Date date;

    public ComentItem(){}

    public ComentItem(String username, String coment, String userid, String postid, String comentid) {

        this.username = username;
        this.coment = coment;
        this.userid = userid;
        this.postid = postid;
        this.comentid = comentid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComent() {
        return coment;
    }

    public void setComent(String coment) {
        this.coment = coment;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
    public String getComentid() {
        return comentid;
    }

    public void setComentid(String comentid) {
        this.comentid = comentid;
    }
}
