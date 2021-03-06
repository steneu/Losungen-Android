package de.schalter.losungen.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import de.schalter.losungen.R;

/**
 * Created by Smarti on 27.12.2015.
 */
public class DownloadNotificationHelper extends Service {

    private Context mContext;
    private int NOTIFICATION_ID = 145611124;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;

    private int icon;
    private CharSequence tickerText;
    private long when;

    public DownloadNotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Put the notification into the status bar
     */
    public void createNotification() {
        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //create the notification
        icon = android.R.drawable.stat_sys_download;
        tickerText = mContext.getString(R.string.download_ticker); //Initial text that appears in the status bar
        when = System.currentTimeMillis();

        //create the content which is shown in the notification pulldown
        mContentTitle = mContext.getString(R.string.content_title); //Full title of the notification in the pull down
        CharSequence contentText = "0" + mContext.getString(R.string.content_text); //Text of the notification in the pull down

        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        //Pending intent for cancel button
        Intent intent = new Intent(mContext, DownloadNotificationHelper.class);
        intent.setAction("CANCEL");
        PendingIntent pendingIntent =
                PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        mNotification = builder.setContentIntent(mContentIntent)
                .setSmallIcon(icon).setTicker(tickerText).setWhen(when)
                .setAutoCancel(true).setContentTitle(mContentTitle)
                .setContentText(contentText)
                /*.addAction(R.drawable.ic_action_cancel,
                        mContext.getResources().getString(R.string.cancel),
                        pendingIntent)*/
                .build();



        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     * @param percentageComplete
     */
    public void progressUpdate(int percentageComplete) {
        //build up the new status message
        CharSequence contentText = percentageComplete + mContext.getString(R.string.content_text);
        //publish it to the status bar
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        //Pending intent for cancel button
        Intent intent = new Intent(mContext, DownloadNotificationHelper.class);
        intent.setAction("CANCEL");
        PendingIntent pendingIntent =
                PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification = builder.setContentIntent(mContentIntent)
                .setSmallIcon(icon).setTicker(tickerText).setWhen(when)
                .setAutoCancel(true).setContentTitle(mContentTitle)
                .setContentText(contentText)
                /*.addAction(R.drawable.ic_action_cancel,
                        mContext.getResources().getString(R.string.cancel),
                        pendingIntent)*/
                .build();

        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new ‘task complete’ notification
     */
    public void completed()    {
        //remove the notification from the status bar
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    public void error() {
        tickerText = mContext.getString(R.string.download_error);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        mNotification = builder.setContentIntent(mContentIntent)
                .setSmallIcon(icon).setTicker(tickerText).setWhen(when)
                .setAutoCancel(true).setContentTitle(mContentTitle)
                .setContentText(mContext.getString(R.string.download_error)).build();

        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        //mNotificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context context = getApplicationContext();

        String action = intent.getAction();
        if (action == null)
            action = "";

        switch (action) {
            case "CANCEL":
                completed();
                break;
        }

        return START_NOT_STICKY;
    }
}