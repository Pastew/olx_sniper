package com.pastew.olxsniper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.olx.OlxDownloader;

import java.util.List;


public class UpdaterJobService extends JobService {

    private final static String TAG = "OLXSniperService";

    private Context context;

    public UpdaterJobService() {
        context = this;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i(TAG, "onStartJob");
        //Toast.makeText(this, "start job", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Offer> newOffers = new OlxDownloader().downloadNewOffers(context, MainActivity.OLX_URL);

                if (newOffers.size() > 0) {
                    Intent i = new Intent(MainActivity.DATABASE_UPDATE_BROADCAST);
                    //i.putExtra("url", "bleble");
                    context.sendBroadcast(i);

                    createNotification(newOffers);
                    MediaPlayer notificationMediaPlayer = MediaPlayer.create(context, R.raw.notification1);
                    notificationMediaPlayer.start();
                }
            }
        }).start();

        return false; // Answers the question: "Is there still work going on?"
    }

    private void createNotification(List<Offer> newOffers) {
        int notificationId = 0;

        // The id of the channel.
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launch_black_24dp)
                        .setContentTitle("Nowe oferty")
                        .setContentText(String.format("%d", newOffers.size()))
                        .setOngoing(true)
                        .setVibrate(new long[]{500, 100, 100, 100, 100, 100})
                        //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification1))
                        .setColor(Color.BLUE);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(notificationId, builder.build());
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i(TAG, "onStopJob");
        Toast.makeText(this, "stop job", Toast.LENGTH_SHORT).show();
        return true; // Answers the question: "Should this job be retried?"
    }


}