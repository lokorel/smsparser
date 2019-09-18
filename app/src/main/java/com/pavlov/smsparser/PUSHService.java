package com.pavlov.smsparser;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class PUSHService extends NotificationListenerService {
    public static String TAG = "NotificationListenerTesting";
    //private StatusBarNotification[] mStatusBarNotification;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "Inside on create");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

    }

    
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        TAG = "onNotificationPosted";
        Log.d(TAG, "id = " + sbn.getId() + "Package Name" + sbn.getPackageName() +
                "Post time = " + sbn.getPostTime() + "Tag = " + sbn.getTag());
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        TAG = "onNotificationRemoved";
        Log.d(TAG, "id = " + sbn.getId() + "Package Name" + sbn.getPackageName() +
                "Post time = " + sbn.getPostTime() + "Tag = " + sbn.getTag());

    }

}
