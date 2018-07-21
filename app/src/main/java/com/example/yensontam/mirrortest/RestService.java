package com.example.yensontam.mirrortest;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RestService extends Service {

    public final String URL = "https://mirror-android-test.herokuapp.com/";
    public final String USERS = "users";
    public final String AUTH = "auth";

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
        public void create(String username, String password) {
            Log.i("Mirror", "create w/username: " + username + ", password: " + password);
            Networking network = Networking.getInstance(RestService.this);

            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            params.put("password", password);

            JSONObject jsonObject = new JSONObject(params);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL + USERS, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Mirror", "Got response: " + response.toString());
                            try {
                                if (mCallback != null)
                                    mCallback.response(response.toString());
                            } catch (RemoteException e) {
                                Log.e("Mirror", "Remote exception: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError error) {
                             if (error == null || error.networkResponse == null) {
                                 return;
                             }

                             String body;
                             //get status code here
                             final String statusCode = String.valueOf(error.networkResponse.statusCode);
                             //get response body and parse with appropriate encoding
                             try {
                                 body = new String(error.networkResponse.data, "UTF-8");
                                 Log.e("Mirror", "Got error: " + body);
                                 if (mCallback != null) {
                                    try {
                                        mCallback.error(body);
                                    } catch (RemoteException e) {
                                        Log.e("Mirror", "Remote exception: " + e.getMessage());
                                    }
                                 }
                             } catch (UnsupportedEncodingException e) {
                                 // exception
                             }
                         }
                     });
            network.addToRequestQueue(request);
        }
        public void login(String username, String password) {
            Log.i("Mirror", "login w/username: "+username+", password: "+password);
        }
        public void update() {
            Log.i("Mirror", "update");
        }

        public void registerCallback(IRestServiceCallback callback) {
            mCallback = callback;
        }

        public void unRegisterCallback() {
            mCallback = null;
        }
    };
    private IRestServiceCallback mCallback;
}
/**
 * 07-20 18:17:18.759: I/Mirror(10966): Got response:
 * {"id":98,
 * "username":"yyesy",
 * "age":null,
 * "height":null,
 * "likes_javascript":false,
 * "magic_number":8535,
 * "magic_hash":"WKJCDEP2WPUWEZM6UO74"}

 */