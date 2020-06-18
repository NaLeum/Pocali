package com.example.test.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CafeItem {
    private String cafeimg;
    private String cafename;
    private String cafelocation;

    private String userid;


    boolean iscafelike;


    private String cafeId;
    @ServerTimestamp
    private Date date;
    public CafeItem(String userid,String cafeimg, String cafename, String cafelocation , String cafeId,boolean iscafelike) {
        this.userid  = userid;
        this.cafeimg = cafeimg;
        this.cafename = cafename;
        this.cafelocation = cafelocation;
        this.cafeId = cafeId;
        this.iscafelike = iscafelike;
    }


    public CafeItem(String cafeimg, String cafename, String cafelocation , String cafeId) {
        this.cafeimg = cafeimg;
        this.cafename = cafename;
        this.cafelocation = cafelocation;
        this.cafeId = cafeId;
    }
    public boolean isIscafelike() {
        return iscafelike;
    }

    public void setIscafelike(boolean iscafelike) {
        this.iscafelike = iscafelike;
    }

    public String getCafeimg() {
        return cafeimg;
    }

    public void setCafeimg(String cafeimg) {
        this.cafeimg = cafeimg;
    }

    public String getCafename() {
        return cafename;
    }

    public void setCafename(String cafename) {
        this.cafename = cafename;
    }

    public String getCafelocation() {
        return cafelocation;
    }

    public void setCafelocation(String cafelocation) {
        this.cafelocation = cafelocation;
    }
    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}


