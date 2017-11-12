package com.pastew.olxsniper;

import android.arch.persistence.room.Room;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.List;


public class UpdaterJobService extends JobService {

    OlxDownloader olxDownloader;
    OfferDatabase offerDatabase;
    private List<Offer> offerList;

    public UpdaterJobService() {
        this.olxDownloader = new OlxDownloader();
        offerDatabase = Room.databaseBuilder(this, OfferDatabase.class, OfferDatabase.DATABASE_NAME).build();
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i(MainActivity.TAG, "--- Start");
        //Toast.makeText(this, "start job", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadNewOffers(MainActivity.OLX_URL);
            }
        }).start();

        return false; // Answers the question: "Is there still work going on?"
    }

    private void downloadNewOffers(String url) {
        List<Offer> newOfferList = olxDownloader.downloadOffers(url);
        offerList = offerDatabase.getOfferDao().getAll();
        Log.i(MainActivity.TAG, String.format("Offers from databaase(%d)", offerList.size()));
        for (int i = 0; i < offerList.size(); ++i) {
            Offer o = offerList.get(i);
            Log.i(MainActivity.TAG, String.format("    %d. %s, %s %s", i + 1, o.title, o.addedDate, o.link));
        }

        List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);

        if (onlyNewOffers.size() > 0) {
            Log.i(MainActivity.TAG, String.format("New offers found, those will be added to database: %d", onlyNewOffers.size()));
            for (int i = 0; i < onlyNewOffers.size(); ++i) {
                Offer o = onlyNewOffers.get(i);
                Log.i(MainActivity.TAG, String.format("%d. %s, %s %s", i + 1, o.title, o.addedDate, o.link));
            }

            offerDatabase.getOfferDao().insertAll(onlyNewOffers);
            MediaPlayer notificationMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification1);
            notificationMediaPlayer.start();

        } else {
            Log.i(MainActivity.TAG, "Checked OLX for new offers, but nothing new found");
        }

        offerDatabase.close();
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i(MainActivity.TAG, "--- Stop");
        Toast.makeText(this, "stop job", Toast.LENGTH_SHORT).show();

        return true; // Answers the question: "Should this job be retried?"
    }
}