package com.android.ebook.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by andyli on 2015/6/21.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public static final int NOTIFICATION_ID = 0;
    public static final String LOGTAG = "gcmBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
           Bundle bd = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
           String messageType = gcm.getMessageType(intent);

        if(!bd.isEmpty()){
            if(messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)){

            }else{
                Log.i(LOGTAG, messageType + " : " + bd.toString());
            }
        }
    }
}
