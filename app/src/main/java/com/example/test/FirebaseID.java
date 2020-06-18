package com.example.test;

public class FirebaseID {
    public static String user = "user";
    public static String post = "post";





    public static String documentId = "documentId";
    public static String neckname ="neckname";
    public static String photoUrl = "photoUrl";

    public static String postid = "postid";
    public static String gaesiUrl = "gaesiUrl";
    public static String camera="camara";
    public static String camerafilter="camarafilter";
    public static String contents="contents" ;

    public static String timestamp = "timestamp";

    public FirebaseID(){}

    public FirebaseID(String neckname, String photoUrl){
        this.neckname = neckname;
        this.photoUrl = photoUrl;

    }
    public static String getNeckname() {
        return neckname;
    }

    public static void setNeckname(String neckname) {
        FirebaseID.neckname = neckname;
    }
    public static String getPhotoUrl() {
        return photoUrl;
    }

    public static void setPhotoUrl(String photoUrl) {
        FirebaseID.photoUrl = photoUrl;
    }
    @Override
    public String toString() {
        return "FirebaseID{" +
                ", neckname='" +  neckname + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
