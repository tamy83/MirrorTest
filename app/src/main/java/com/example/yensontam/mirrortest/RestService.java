package com.example.yensontam.mirrortest;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestService extends Service {

    public final String URL = "https://mirror-android-test.herokuapp.com/";
    public final String USERS = "users";
    public final String AUTH = "auth";

    private Map<Integer, IRestServiceCallback> callbacks;

    public RestService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        callbacks = new HashMap<Integer, IRestServiceCallback>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IRestService.Stub mBinder = new IRestService.Stub() {
        public void create(int id, String username, String password) {
            Log.i("Mirror", "create w/username: " + username + ", password: " + password);

            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            params.put("password", password);

            queueNetworkRequest(id, Request.Method.POST, URL+USERS, params);
        }
        public void login(int id, String username, String password) {
            Log.i("Mirror", "login w/username: "+username+", password: "+password);

            Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            params.put("password", password);

            queueNetworkRequest(id, Request.Method.POST, URL+AUTH, params);
        }

        public void get(int id, String userId, String access_token) {
            Log.i("Mirror", "get w/id: "+userId+", access_token: "+access_token);

            Map<String, String>  headers = new HashMap<String, String>();
            headers.put("Authorization", "JWT " + access_token);

            queueNetworkRequest(id, Request.Method.GET, URL+USERS+"/"+userId, null, headers);
        }

        public void update() {
            Log.i("Mirror", "update");
        }

        public void registerCallback(int id, IRestServiceCallback callback) {
            callbacks.put(id, callback);
        }

        public void unRegisterCallback(int id) {
            callbacks.remove(id);
        }

        private void queueNetworkRequest(final int id, int reqMethod, String url, Map<String, String> params)
        {
            queueNetworkRequest(id, reqMethod, url, params, null);
        }
        private void queueNetworkRequest(final int id, int reqMethod, String url, Map<String, String> params, final Map<String, String> extraHeaders) {
            Networking network = Networking.getInstance(RestService.this);

            if (params == null)
                params = Collections.emptyMap();
            JSONObject jsonObject = new JSONObject(params);
            JsonObjectRequest request = new JsonObjectRequest(reqMethod, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Mirror", "Got response: " + response.toString());
                            try {
                                if (callbacks.get(id) != null)
                                    callbacks.get(id).response(response.toString());
                                else
                                    Log.e("Mirror", "callback from id: " + id + " is null!");
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
                            final String statusCode = String.valueOf(error.networkResponse.statusCode);
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.e("Mirror", "Got error: " + body);
                                if (callbacks.get(id) != null) {
                                    try {
                                        callbacks.get(id).error(body);
                                    } catch (RemoteException e) {
                                        Log.e("Mirror", "Remote exception: " + e.getMessage());
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                // exception
                            }
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (extraHeaders == null)
                        return Collections.emptyMap();
                    return extraHeaders;
                }
            };
            network.addToRequestQueue(request);
        }
    };
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