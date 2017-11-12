package com.pastew.olxsniper;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class UpdaterJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.i(MainActivity.TAG, "--- Start");
        Toast.makeText(this, "start job", Toast.LENGTH_SHORT).show();
        MediaPlayer notificationMediaPlayer = MediaPlayer.create(this, R.raw.notification1);
        notificationMediaPlayer.start();

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i(MainActivity.TAG, "--- Stop");
        Toast.makeText(this, "stop job", Toast.LENGTH_SHORT).show();

        return true; // Answers the question: "Should this job be retried?"
    }
}