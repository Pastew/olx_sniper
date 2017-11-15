package com.pastew.olxsniper;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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

                    MediaPlayer notificationMediaPlayer = MediaPlayer.create(context, R.raw.notification1);
                    notificationMediaPlayer.start();
                }
            }
        }).start();

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i(TAG, "onStopJob");
        Toast.makeText(this, "stop job", Toast.LENGTH_SHORT).show();
        return true; // Answers the question: "Should this job be retried?"
    }
}