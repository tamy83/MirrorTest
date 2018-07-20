package com.example.yensontam.mirrortest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RestService extends Service {
    public RestService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IRestService.Stub mBinder = new IRestService.Stub() {
        public void create(String username, String password){
            Log.i("Mirror", "create w/username: "+username+", password: "+password);
        }
        public void login(String username, String password) {
            Log.i("Mirror", "login w/username: "+username+", password: "+password);
        }
    };
}