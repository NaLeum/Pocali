package com.example.test.data.model;

public class NotificationModel {

    public String to;

    public Notification notification =new Notification();
    public Data data = new Data();

    public static class Notification {

        public String text;

        public String title;

    }
    public static class Data{
        public String text;
        public String title;

    }
}
