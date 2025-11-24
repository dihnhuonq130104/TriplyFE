package com.example.triply;

import android.app.Application;
import com.example.triply.data.remote.RetrofitClient;

public class TriplyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
}

