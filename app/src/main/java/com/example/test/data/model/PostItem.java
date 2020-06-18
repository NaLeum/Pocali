package com.example.test.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public  class PostItem {
    boolean isUserLike;
    boolean isUserFind;



    String documentId;
    int PostLikeCount;
    String username;
    String userImgUrl;
    String postImgUrl;
    String postText;



    String likecount;

    String likeId;



    String cafeId;
    String cameraModel;
    String camerafilter;
    String postid;
    @ServerTimestamp
    private Date date;

    public PostItem(){

    }

    public PostItem(boolean isUserLike, boolean isUserFind,String documentId) {
        this.isUserLike = isUserLike;
        this.isUserFind = isUserFind;
        this.documentId = documentId;
    }

    public PostItem(String documentId, String username, String userImgUrl, String postImgUrl, String postText, String cameraModel, String camerafilter, String postid,String likecount) {
        this.likecount = likecount;
        this.documentId = documentId;
        this.username = username;
        this.userImgUrl = userImgUrl;
        this.postImgUrl = postImgUrl;
        this.postText = postText;
        this.cameraModel = cameraModel;
        this.camerafilter = camerafilter;
        this.postid = postid;
    }


    public PostItem(String documentId, String username, String userImgUrl, String postImgUrl, String postText, String cameraModel, String camerafilter, String postid,String cafeId, String likecount) {

        this.documentId = documentId;
        this.username = username;
        this.userImgUrl = userImgUrl;
        this.postImgUrl = postImgUrl;
        this.postText = postText;
        this.cameraModel = cameraModel;
        this.camerafilter = camerafilter;
        this.postid = postid;
        this.cafeId = cafeId;
        this.likecount= likecount;
    }



    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }
    public String getPostid() {
        return postid;
    }
    public boolean isUserLike() { return isUserLike; }

    public int getPostLikeCount() {
        return PostLikeCount;
    }

    public String getUsername() {
        return username;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public String getpostImgUrl() {
        return postImgUrl;
    }

    public String getPostText() {
        return postText;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public String getCamerafilter() {
        return camerafilter;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public void setUserLike(boolean userLike) {
        isUserLike = userLike;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setPostLikeCount(int postLikeCount) {
        PostLikeCount = postLikeCount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public void setCamerafilter(String camerafilter) {
        this.camerafilter = camerafilter;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }
    public boolean isUserFind() {
        return isUserFind;
    }

    public void setUserFind(boolean userFind) {
        isUserFind = userFind;
    }
    public String getLikecount() {
        return likecount;
    }

    public void setLikecount(String likecount) {
        this.likecount = likecount;
    }

    @Override
    public String toString() {
        return "PostItem{" +
                "isUserLike=" + isUserLike +
                ", PostLikeCount=" + PostLikeCount +
                ", username='" + username + '\'' +
                ", userImgUrl='" + userImgUrl + '\'' +
                ", postImgUrl='" + postImgUrl + '\'' +
                ", postText='" + postText + '\'' +
                ", camaraModel='" + cameraModel + '\'' +
                ", camarafilter='" + camerafilter + '\'' +
                ", postid='" + postid + '\'' +
                '}';
    }
}
