package com.example.yensontam.mirrortest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends MirrorActivity {

    private EditText name;
    private EditText password;
    private Button login;
    private Button register;
    private Button profile;

    // on create
    // start service
    // check for JWT stored locally, on shared preferences?, and validate with server
    // when received auth response go to profile activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = (EditText) findViewById(R.id.loginEditTextName);
        password = (EditText) findViewById(R.id.loginEditTextPassword);
        login = (Button) findViewById(R.id.loginButtonLogin);
        register = (Button) findViewById(R.id.loginButtonRegister);
        profile = (Button) findViewById(R.id.loginButtonProfile);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // login
                if (mRestService != null) {
                    try {
                        mRestService.login(LoginActivity.this.hashCode(), name.getText().toString(), password.getText().toString());
                        enableNameAndPassword(false);
                    } catch (RemoteException e) {
                        Log.e("Mirror","mRestService exception: " + e.getMessage());
                        Log.e("Mirror","mRestService stacktrace: " + Log.getStackTraceString(e));
                        enableNameAndPassword(true);
                    }
                }
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String access_token = sharedPref.getString("access_token", null);
        if (access_token != null) {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    protected void response(String value) {
        Log.i("Mirror", "Login Activity response: " + value);
        Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();

        try {
            JSONObject jsonObject = new JSONObject(value);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            String access_token = jsonObject.getString("access_token");
            editor.putString("access_token", access_token);
            editor.apply();

            Intent profileIntent = new Intent(LoginActivity.this, ProfileActivity.class);
            profileIntent.putExtra("access_token", access_token);

            startActivity(profileIntent);

        } catch (JSONException ex) {
            Log.e("Mirror", ex.getMessage());
        }
        enableNameAndPassword(true);
    }
    protected void error(String message) {
        Log.i("Mirror", "Login Activity error: " + message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        enableNameAndPassword(true);
    }

    private void enableNameAndPassword(boolean enabled) {
        name.setEnabled(enabled);
        password.setEnabled(enabled);
    }
}
/**
 *
 * 07-30 19:01:04.462: I/Mirror(9670): Got response:
 * {"access_token":
 * "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1MzI5OTE5NjQsImlhdCI6MTUzMjk5MTY2NCwibmJmIjoxNTMyOTkxNjY0LCJpZGVudGl0eSI6MTA0fQ._kU8gwZze_3B2a5yseBWjAbLSYouZbsmOxCep-deR7o"}
 */
