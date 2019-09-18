package com.pavlov.smsparser;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Telephony;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static android.content.ContentValues.TAG;

/**
 * Created by Tutlane on 02-08-2017.
 */

public class SMSService extends Service {

    private static Context appContext;
    private smsparser receiver;
    private IntentFilter filter;
    private ServiceCallbacks serviceCallbacks;
    private final IBinder binder = new LocalBinder();
    NotificationCompat.Builder mBuilder;
    String NOTIFICATION_CHANNEL_ID = "19880405";

    public class LocalBinder extends Binder {
        SMSService getService() {
            // Return this instance of MyService so clients can call public methods
            return SMSService.this;
        }
    }

    static void ShowToast(String txt, String from) {
        LayoutInflater inflater = (LayoutInflater) appContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView txtView = layout.findViewById(R.id.SMSText);
        TextView fromView = layout.findViewById(R.id.SMSFrom);
        txtView.setText(txt);
        fromView.setText(from);
        Toast toast = new Toast(appContext.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        appContext = getApplicationContext();
        receiver=new smsparser();
        // register sms receiver
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(receiver, filter);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder
                .create(this)
                .addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "MyChannle", NotificationManager.IMPORTANCE_NONE);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(notificationChannel);

        mBuilder = new NotificationCompat.Builder(this, null);
        mBuilder.setContentTitle("SMS Parser")
                .setContentText("Фонововая служба")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(resultPendingIntent)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false);

        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        startForeground(2, mBuilder.build());

        //ShowToast("2345","Rostelecom");

        return START_STICKY;
    }

//    @Override
//    public void onNotificationPosted(StatusBarNotification sbn) {
//
//        Log.i(TAG,"**********  onNotificationPosted");
//        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
        stopForeground(true);
        if(receiver != null) {
            unregisterReceiver(receiver);
        };

    }

}