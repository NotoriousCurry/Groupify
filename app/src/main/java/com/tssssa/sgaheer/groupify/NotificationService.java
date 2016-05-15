package com.tssssa.sgaheer.groupify;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Meant to be used to issue notifications for the application, Have to finish implementation
 */
public class NotificationService extends IntentService {
    public final static String EXTRA_ID = "com.tssssa.groupify.ID";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String data = intent.getDataString();
    }

    private void createNotification(String title, String detail, String eId) {
        int notificationId = 001;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_event_48dp)
                .setContentTitle(title)
                .setContentText(detail);


        Intent intent = new Intent(this, ViewEvent.class);
        intent.putExtra(EXTRA_ID, eId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ViewEvent.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent rPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent rPendingIntent = PendingIntent.getActivity(this, 0, intent,
        //        PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(rPendingIntent);
        NotificationManager  mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, mBuilder.build());

    }
}
