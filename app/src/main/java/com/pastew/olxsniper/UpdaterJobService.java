package com.pastew.olxsniper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.db.OfferDatabase;
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

                    createNotification();
                    MediaPlayer notificationMediaPlayer = MediaPlayer.create(context, R.raw.notification1);
                    notificationMediaPlayer.start();
                }
            }
        }).start();

        return false; // Answers the question: "Is there still work going on?"
    }

    private void createNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyId = 1;
        String channelId = "some_channel_id";

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Some Message")
                .setContentText("You've received new messages!")
                .setSmallIcon(R.drawable.ic_launch_black_24dp)
                .build();

        notificationManager.notify(notifyId, notification);
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i(TAG, "onStopJob");
        Toast.makeText(this, "stop job", Toast.LENGTH_SHORT).show();
        return true; // Answers the question: "Should this job be retried?"
    }


}