package com.pavlov.smsparser;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements ServiceCallbacks {
    private MyService myService;
    private Intent serviceIntent;
    private int serviceState = SMSPARSER_SERVICE_DISCONNECT;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        serviceIntent = new Intent(this, MyService.class);
        sessionManager = new SessionManager(this);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            serviceState = SMSPARSER_SERVICE_CONNECTED;
            myService.setCallbacks(MainActivity.this);
            OnServiceStateChange(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceState = SMSPARSER_SERVICE_DISCONNECT;
            OnServiceStateChange(false);
            myService.setCallbacks(null);
        }
    };

    @Override
    public void onStart()
    {
        super.onStart();
        serviceState=SMSPARSER_SERVICE_AVAITING;
        bindService(serviceIntent, serviceConnection, 0);
    }

    @Override
    public void onStop()
    {
        //unbindService(serviceConnection);
        super.onStop();
    }

    // Start the service
    public void startService(View view) {
        if (this.checkSelfPermission( Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 0);
        }
        if (this.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions( new String[]{Manifest.permission.FOREGROUND_SERVICE}, 0);
        }
        sessionManager.setParam("regexp_pattern", getRegExpPattern());
        if(null == myService || serviceState==SMSPARSER_SERVICE_DISCONNECT)
        {
            startForegroundService(serviceIntent);
        }
        if (serviceState==SMSPARSER_SERVICE_DISCONNECT) {
            bindService(serviceIntent, serviceConnection, 0);
        }

    }
    // Stop the service
    public void stopService(View view) {

        if (serviceState == SMSPARSER_SERVICE_CONNECTED) {
            unbindService(serviceConnection);
            serviceState = SMSPARSER_SERVICE_DISCONNECT;
            myService.setCallbacks(null);
        }

        if (null != myService) {
            OnServiceStateChange(false);
            stopService(serviceIntent);
        }
    }

    @Override
    public void OnServiceStateChange(Boolean state) {
        TextView fld = this.findViewById(R.id.service_state);
        if(state) {
            fld.setText("Service: ON");
        } else {
            fld.setText("Service: OFF");
        }
    }

    private String getRegExpPattern() {
        TextView fld = this.findViewById(R.id.txtRegExpPattern);
        return fld.getText().toString();
    }

}