package com.pushnotification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.peoplemovers.app.ActivityHome;
import com.peoplemovers.app.R;
import com.util.Util;

import org.json.JSONObject;

import java.util.List;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private String title = "", body = "", text = "", modelText = "";
    private static final String CHANNEL_ID = "PeopleMovers";
    private static final String CHANNEL_DESCRIPTION = "Peoplemovers";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // message, here is where that should be initiated.
        try {
            Log.e(TAG, "From: " + remoteMessage.getData().get("notification"));
            if (remoteMessage.getData() != null) {
                String notification = remoteMessage.getData().get("notification");
                JSONObject jsonObject = new JSONObject(notification);
                if (!jsonObject.isNull("title")) {
                    title = jsonObject.getString("title");
                }
                if (!jsonObject.isNull("body")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("body");
                    text = jsonObject1.getString("text");
                }
            }
            if (!text.contains("Deleted user")) {
                //  Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody()+"");
                if (!Util.checkApp(getApplicationContext())) {
                    Intent intent = new Intent(this, ActivityHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.notification)
                            .setContentTitle(Html.fromHtml(title, Build.VERSION.SDK_INT) + "")
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(title, Build.VERSION.SDK_INT)))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(text, Build.VERSION.SDK_INT)))
                            .setContentText(Html.fromHtml(text, Build.VERSION.SDK_INT))
                            .setAutoCancel(true)
                            .setSound(notificationSoundURI)
                            .setContentIntent(resultIntent);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                title,
                                NotificationManager.IMPORTANCE_DEFAULT);
                        channel.setDescription(text);
                        notificationManager.createNotificationChannel(channel);
                    }
                    //   ShortcutBadger.applyNotification(getApplicationContext(), notification, 4);
                    notificationManager.notify(0, mNotificationBuilder.build());


                } else {
                    // ShortcutBadger.applyCount(getApplicationContext(), 4); //for 1.1.4+
                    sendNotification(title + "", text + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, ActivityHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(title, Build.VERSION.SDK_INT)))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(messageBody, Build.VERSION.SDK_INT)))
                .setContentText(Html.fromHtml(messageBody, Build.VERSION.SDK_INT))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(messageBody);
            notificationManager.createNotificationChannel(channel);
        }

        //   ShortcutBadger.applyNotification(getApplicationContext(), notification, 4);
        notificationManager.notify(0, notificationBuilder.build());
    }
}

