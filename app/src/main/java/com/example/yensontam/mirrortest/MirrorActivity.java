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

public abstract class MirrorActivity extends AppCompatActivity {


    protected IRestService mRestService;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            mRestService = IRestService.Stub.asInterface(service);
            Log.d("Mirror", "Service connected");
            try {
                mRestService.registerCallback(mCallback);
            } catch (RemoteException e) {
                Log.e("Mirror", "Unable to register callback: " + e.getMessage());
            }
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.d("Mirror", "Service has unexpectedly disconnected");
            mRestService = null;
            try {
                mRestService.unRegisterCallback();
            } catch (RemoteException e) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MirrorActivity.this, RestService.class);
        intent.setAction(IRestService.class.getName());
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    void unbindService() {
        if (mRestService != null) {
            // Detach our existing connection.
            try {
                mRestService.unRegisterCallback();
            } catch (RemoteException e) {

            }unbindService(mConnection);
            mRestService = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }
    private IRestServiceCallback mCallback = new IRestServiceCallback.Stub() {

        public void response(String value) {
            MirrorActivity.this.response(value);
        }
        public void error(String message) {
            MirrorActivity.this.error(message);
        }
    };

    protected abstract void response(String value);
    protected abstract void error(String value);
}
