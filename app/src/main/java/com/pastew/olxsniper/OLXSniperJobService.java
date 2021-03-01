package com.pastew.olxsniper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.db.SniperDatabaseManager;
import com.pastew.olxsniper.olx.OfferDownloaderManager;
import com.pastew.olxsniper.ui.MainActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class OLXSniperJobService extends JobService {

    private final static String TAG = "OLXSniperService";
    String CHANNEL_ID = "my_channel_01";

    private Context context;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm:ss");

    public OLXSniperJobService() {
        context = this;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        String logMessage = "onStartJob: " + sdf.format(new Date(System.currentTimeMillis())) + "\n";
        MyLogger.i(logMessage);
        //Toast.makeText(this, "start job", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Offer> offersFromWeb = OfferDownloaderManager.getInstance(context).downloadNewOffersAndSaveToDatabase();

                if (offersFromWeb.size() > 0) {
                    Intent i = new Intent(Globals.DATABASE_UPDATE_BROADCAST);
                    //i.putExtra("url", "bleble");
                    context.sendBroadcast(i);

                    createNotificationChannel();
                    createNotification();
                }

            }
        }).start();

        return false; // Answers the question: "Is there still work going on?"
    }

    private void createNotification() {
        MyLogger.i("createNotification");
        int notificationId = 0;

        List<Offer> offersNotSeenByUser = new SniperDatabaseManager(context).getOffersNotSeenByUserAndNotRemoved();
        MyLogger.i("offersNotSeenByUser count = " + offersNotSeenByUser.size());
        // The id of the channel.
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launch_black_24dp)
                        .setContentTitle("Nowe oferty")
                        .setContentText(String.format("%d", offersNotSeenByUser.size()))
                        //.setOngoing(true)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{500, 100, 100, 100, 100, 100})
                        //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification2))
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        MyLogger.i("onStopJob");
        Toast.makeText(this, "stop job", Toast.LENGTH_SHORT).show();
        return true; // Answers the question: "Should this job be retried?"
    }
}