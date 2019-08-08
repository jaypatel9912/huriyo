package com.huriyo.Utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.huriyo.R;
import com.huriyo.Ui.SplashActivity;

import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null) {
            Log.e(TAG, "From: " + remoteMessage.getFrom().toString());
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
                try {
                    handleDataMessage(getApplicationContext(), new JSONObject(remoteMessage.getData()).getString("title"), new JSONObject(remoteMessage.getData()).getString("body"));
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }else{
                handleDataMessage(getApplicationContext(), getString(R.string.app_name), remoteMessage.getNotification().getBody());
            }
        }
    }

    private void handleDataMessage(Context context, String title, String message) {
        try {
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Intent resultIntent = new Intent(context, SplashActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setSmallIcon(getNotificationIcon());
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
            notificationBuilder.setContentText(message);
            notificationBuilder.setContentIntent(resultPendingIntent);
            notificationBuilder.setStyle(new BigTextStyle().bigText(message));
            notificationBuilder.setAutoCancel(true);
            Notification notification = notificationBuilder.build();
            notification.defaults |= 1;
            notification.defaults |= 2;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(new Random().nextInt(1000), notification);
        } catch (Exception e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_white_notification : R.mipmap.ic_launcher;
    }
}
