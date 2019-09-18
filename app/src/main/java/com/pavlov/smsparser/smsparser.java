package com.pavlov.smsparser;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.pavlov.smsparser.SMSService.ShowToast;

public class smsparser extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

            ClipboardManager cb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            SessionManager sessionManager = new SessionManager(context);
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                String msg_from;

                if (bundle != null) {
                    //---retrieve the SMS message received---
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            String msgBody = msgs[i].getMessageBody();
                            if (msgBody != null) {
                                String regex = sessionManager.getParam("regexp_pattern"); //"[0-9]{4,}";
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(msgBody);
                                if (matcher.find()) {
                                    String msg_txt = matcher.group(0);
                                    ClipData clip = ClipData.newPlainText("smsparser", msg_txt);
                                    cb.setPrimaryClip(clip);
                                    ShowToast(msg_txt, msg_from);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Exception caught", e.getMessage());
                    }
                }
            }
        }

}