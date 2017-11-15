package com.pastew.olxsniper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


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
                new OlxDownloader().downloadNewOffers(context, MainActivity.OLX_URL);
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