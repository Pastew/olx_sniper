package com.pastew.olxsniper;

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
        offerDatabase = new OfferDatabase();
        offerList = offerDatabase.downloadOffers();
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i(MainActivity.TAG, "--- Start");
        Toast.makeText(this, "start job", Toast.LENGTH_SHORT).show();

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
        List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);

        if (onlyNewOffers.size() > 0) {
            offerList.addAll(0, onlyNewOffers);

            MediaPlayer notificationMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification1);
            notificationMediaPlayer.start();

            Log.i(MainActivity.TAG, String.format("New offers found: %d", onlyNewOffers.size()));
            for (int i = 0; i < onlyNewOffers.size(); ++i) {
                Offer o = onlyNewOffers.get(i);
                Log.i(MainActivity.TAG, String.format("%d. %s, %s %s", i + 1, o.title, o.addedDate, o.link));
            }
        } else {
            Log.i(MainActivity.TAG, "Checked OLX for new offers, but nothing new found");
            Toast.makeText(getApplicationContext(), "Nie ma nowych ofert", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i(MainActivity.TAG, "--- Stop");
        Toast.makeText(this, "stop job", Toast.LENGTH_SHORT).show();

        return true; // Answers the question: "Should this job be retried?"
    }
}