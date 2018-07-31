package com.example.yensontam.mirrortest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class RegisterActivity extends MirrorActivity {

    private EditText name;
    private EditText password;
    private Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.regTextViewUserName);
        password = (EditText) findViewById(R.id.regEditTextPassword);
        register = (Button) findViewById(R.id.regButtonCreateAccount);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRestService != null) {
                    try {
                        mRestService.create(RegisterActivity.this.hashCode(), name.getText().toString(), password.getText().toString());
                        enableNameAndPassword(false);
                    } catch (RemoteException e) {
                        Log.e("Mirror","mRestService exception: " + e.getMessage());
                        Log.e("Mirror","mRestService stacktrace: " + Log.getStackTraceString(e));
                        enableNameAndPassword(true);
                    }
                }
            }
        });
    }

    protected void response(String value) {
        Log.i("Mirror", "Register Activity response: " + value);
        Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonObject = new JSONObject(value);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("name", jsonObject.getString("username"));
            // should this be stored as string?! or at all?!
            editor.putString("password", password.getText().toString());
            editor.putString("id", jsonObject.getString("id"));
            editor.apply();
        } catch (JSONException ex) {
            Log.e("Mirror", ex.getMessage());
        }
        enableNameAndPassword(true);
    }
    protected void error(String message) {
        Log.i("Mirror", "Register Activity error: " + message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        enableNameAndPassword(false);
    }


    private void enableNameAndPassword(boolean enabled) {
        name.setEnabled(enabled);
        password.setEnabled(enabled);
    }

    /**
    private IRestServiceCallback mCallback = new IRestServiceCallback.Stub() {

        public void valueChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0));
        }
    };

    private static final int BUMP_MSG = 1;

    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case BUMP_MSG:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };
     */

}
