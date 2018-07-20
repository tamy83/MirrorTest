package com.example.yensontam.mirrortest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

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

        Intent intent = new Intent(LoginActivity.this, RestService.class);
        intent.setAction(IRestService.class.getName());
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

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
                        mRestService.create(name.getText().toString(), password.getText().toString());
                    } catch (RemoteException e) {
                        Log.e("Mirror","mRestService exception: " + e.getMessage());
                        Log.e("Mirror","mRestService stacktrace: " + Log.getStackTraceString(e));
                    }
                }
            }
        });
    }

    IRestService mRestService;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            mRestService = IRestService.Stub.asInterface(service);
            Log.d("Mirror", "Service connected");
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.d("Mirror", "Service has unexpectedly disconnected");
            mRestService = null;
        }
    };

}
