package com.pastew.olxsniper;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "OLXSniper";
    public static final String OLX_URL = "https://www.olx.pl/elektronika/telefony-komorkowe/";
    // public static final String OLX_URL = "https://www.olx.pl/oferty/q-iphone/"; //TODO: FIx this bug

    private RecyclerView.Adapter adapter;

    private OlxDownloader olxDownloader;
    List<Offer> offerList;

    private boolean updaterIsRunning;
    private int updaterDelayInSeconds = 10;
    private Handler updaterHandler = new Handler();

    private MediaPlayer notificationMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: here should open window with added new OLX Listner
            }
        });

        // Recycler view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        offerList = new ArrayList<>();
        adapter = new MyAdapter(getApplicationContext(), offerList);
        recyclerView.setAdapter(adapter);

        // OLX
        olxDownloader = new OlxDownloader();

        // Sound
        notificationMediaPlayer = MediaPlayer.create(this, R.raw.notification1);

        // Updater runnable
//        updaterIsRunning = true;
//        updaterRunnable.run();

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        dispatcher.cancelAll();
        Job myJob = dispatcher.newJobBuilder()
                .setService(UpdaterJobService.class)
                .setTag(UpdaterJobService.class.getSimpleName())
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(updaterDelayInSeconds, updaterDelayInSeconds + 1))
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(false)
                .build();

        dispatcher.mustSchedule(myJob);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable updaterRunnable = new Runnable() {
        @Override
        public void run() {
            if(updaterIsRunning) {
                start();
            }
        }
    };

    public void stop() {
        updaterIsRunning = false;
        updaterHandler.removeCallbacks(updaterRunnable);
    }

    public void start() {
        Log.d(TAG, "Runnable: start()");
        updaterIsRunning = true;
        new DownloadOffersTask().execute(OLX_URL);
        updaterHandler.postDelayed(updaterRunnable, updaterDelayInSeconds * 1000);
    }

    private void changeColorsOfOldOffers() {
        for(Offer o : offerList)
            o.wasSeenByUser = true;
    }


    private void notifyUserAboutNewOffers(List<Offer> onlyNewOffers) {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.constrainLayout), String.format("%d nowe oferty!", onlyNewOffers.size()), Snackbar.LENGTH_LONG)
                .setAction("Nie klikaj!", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Miałeś nie klikać!", Toast.LENGTH_SHORT).show();
                    }
                });
        snackbar.show();

        if(notificationMediaPlayer != null)
            notificationMediaPlayer.start();
    }

    private class DownloadOffersTask extends AsyncTask<String, Integer, List<Offer>> {
        protected List<Offer> doInBackground(String... urls) {
            String url = urls[0];

            //publishProgress(i);
            List<Offer> newOfferList = olxDownloader.downloadOffers(url);
            List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);
            return onlyNewOffers;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(List<Offer> onlyNewOffers) {
            if(onlyNewOffers.size() > 0) {
                changeColorsOfOldOffers();

                offerList.addAll(0, onlyNewOffers);
                adapter.notifyDataSetChanged();

                notifyUserAboutNewOffers(onlyNewOffers);

                Log.i(TAG, String.format("New offers found: %d", onlyNewOffers.size()));
                for (int i = 0 ; i < onlyNewOffers.size() ; ++i) {
                    Offer o = onlyNewOffers.get(i);
                    Log.i(TAG, String.format("%d. %s, %s %s", i + 1, o.title, o.addedDate, o.link));
                }
            }
            else {
                Log.i(TAG, String.format("Checked OLX for new offers, but nothing new found, " +
                        "I will try afer %d seconds.", updaterDelayInSeconds));

                Toast.makeText(MainActivity.this, "Nie ma nowych ofert", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
