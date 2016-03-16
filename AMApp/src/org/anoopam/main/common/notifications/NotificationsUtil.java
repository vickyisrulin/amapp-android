package org.anoopam.main.common.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.anoopam.main.AMApplication;
import org.anoopam.main.R;

/**
 * Created by dadesai on 3/14/16.
 */
public class NotificationsUtil {

    /**
     * Generates and raises the notification - which will be cleared on-Click
     *
     * @param text1
     * @param text2
     * @param notificationOnClickIntent - the class Intent that should be invoked on click
     */
    public static void raiseOnetimeNewNotification(String text1, String text2, Intent notificationOnClickIntent) {
        Context applicationContext = AMApplication.getInstance().getApplicationContext();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, notificationOnClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(applicationContext)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(text1)
                .setContentText(text2)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(applicationContext.NOTIFICATION_SERVICE);

        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
