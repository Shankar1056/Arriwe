package com.arriwe.wayndr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.sancsvision.arriwe.R;

/**
 * Created by Abhi1 on 26/08/15.
 */
public class GCMListener extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "Bundle data : " + data);
        Intent notificationIntent = new Intent(this, Eight.class);
        notificationIntent.putExtra("setfragment","three");
        if(message!=null && !message.equalsIgnoreCase("user city status changed successfully"))
        generateNotification(this,message,notificationIntent);
        if (from.startsWith("/topics/")) {
        } else {
        }
    }
    private static void generateNotification(Context context, String message, Intent notificationIntent) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = context.getString(R.string.app_name);
        Uri rnotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, rnotification);
        r.play();
        PendingIntent intent =PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Notification  notification = builder.setContentIntent(intent)
                .setSmallIcon(GCMListener.getNotificationIcon()).setTicker(title).setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentTitle(title)
                .setContentText(message).build();
        notificationManager.notify((int)System.currentTimeMillis(), notification);
    }
    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.launcher : R.mipmap.launcher;
    }
}