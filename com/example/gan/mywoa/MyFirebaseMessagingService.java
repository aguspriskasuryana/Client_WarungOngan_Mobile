package com.example.gan.mywoa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by GUSWIK on 6/27/2018.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    int notifyId = 0;
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d("Service", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("Service", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("Service", "Message notification: " + remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData() != null){
            // remoteMessage.getData().get("message").toString();
            String n = remoteMessage.getNotification().getBody();
            String xxx =n;
            if (remoteMessage.getData().get("message") != null){
                String x=remoteMessage.getData().get("message");
                sendNotification(x,"","");
            } else {
                sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getTag());
            }

        }

    }

    public void sendNotification(String body, String title, String tag){
        Intent intent = new Intent(this, Pembayaran.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(body.equals("sukses")){
        intent = new Intent(this, MainActivity.class);
        }
        Bundle b = new Bundle();
        b.putString("kode", body);
        b.putString("title", title);
        b.putString("tag", tag);
        intent.putExtras(b);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri notifificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_logo).setContentText(title)
                .setContentTitle(title)
                .setContentText(body).setAutoCancel(true)
                .setSound(notifificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }
}
