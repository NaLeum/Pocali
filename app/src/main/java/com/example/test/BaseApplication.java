package com.example.test;

import android.app.Application;

import com.bumptech.glide.Glide;

public class BaseApplication extends Application {


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }




}
