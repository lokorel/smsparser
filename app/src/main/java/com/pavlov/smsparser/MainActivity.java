package com.pavlov.smsparser;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements ServiceCallbacks {
    private SMSService SMSService;
    private Intent SMSserviceIntent;
    private Intent PUSHserviceIntent;
    private int serviceState = SMSPARSER_SERVICE_DISCONNECT;
    private SessionManager sessionManager;
    private String[] permissions = new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SMSserviceIntent = new Intent(this, SMSService.class);
        PUSHserviceIntent = new Intent(this, PUSHService.class);
        sessionManager = new SessionManager(this);

    }


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get SMSService instance
            SMSService.LocalBinder binder = (SMSService.LocalBinder) service;
            SMSService = binder.getService();
            serviceState = SMSPARSER_SERVICE_CONNECTED;
            SMSService.setCallbacks(MainActivity.this);
            if (null != SMSService)  OnServiceStateChange(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceState = SMSPARSER_SERVICE_DISCONNECT;
            OnServiceStateChange(false);
            SMSService.setCallbacks(null);
        }
    };

    @Override
    public void onStart()
    {
        super.onStart();
        serviceState=SMSPARSER_SERVICE_AVAITING;
        bindService(SMSserviceIntent, serviceConnection, 0);
    }

    @Override
    public void onStop()
    {
        super.onStop();

    }

    // Start the service
    public void startService(View view) {

        this.requestPermissions(permissions, 0);

        sessionManager.setParam("regexp_pattern", getRegExpPattern());
        if(null == SMSService || serviceState==SMSPARSER_SERVICE_DISCONNECT)
        {
            startForegroundService(SMSserviceIntent);
        }
        if (serviceState==SMSPARSER_SERVICE_DISCONNECT) {
            bindService(SMSserviceIntent, serviceConnection, 0);
        }
    }

    // Stop the service
    public void stopService(View view) {

        if (serviceState == SMSPARSER_SERVICE_CONNECTED) {
            unbindService(serviceConnection);
            serviceState = SMSPARSER_SERVICE_DISCONNECT;
            SMSService.setCallbacks(null);
        }

        if (null != SMSService) {
            OnServiceStateChange(false);
            stopService(SMSserviceIntent);
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