package com.krux.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.brent.helloworld.R;
import com.google.android.gms.gcm.GcmListenerService;


/**
 * Created by Belal on 4/15/2016.
 */

//Class is extending GcmListenerService
public class GCMPushReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String message = data.getString("message");
        System.out.println("MESSAGE FOUND: " + message + " !!!!!!!!!!!!!!!");
    }


}