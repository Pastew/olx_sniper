package com.pastew.olxsniper;

import android.media.MediaPlayer;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "OLXSniper";
    public static final String URL = "https://www.olx.pl/elektronika/telefony-komorkowe/";

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
        updaterIsRunning = true;
        updaterRunnable.run();
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

        List<Offer> newOfferList = olxDownloader.downloadOffers(URL);

        List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);

        if(onlyNewOffers.size() > 0) {
            offerList.addAll(0, onlyNewOffers);
            adapter.notifyDataSetChanged();
            notifyAboutNewOffers(onlyNewOffers);
            Log.i(TAG, String.format("New offers found: %d", onlyNewOffers.size()));
            for (int i = 0 ; i < onlyNewOffers.size() ; ++i) {
                Offer o = newOfferList.get(i);
                Log.i(TAG, String.format("%d. %s, %s %s", i + 1, o.title, o.addedDate, o.link));
            }
        }
        else {
            Log.i(TAG, String.format("Checked OLX for new offers, but nothing new found, " +
                    "I will try afer %d seconds.", updaterDelayInSeconds));

            Toast.makeText(this, "Brak nowych ofert.", Toast.LENGTH_SHORT).show();
        }

        updaterHandler.postDelayed(updaterRunnable, updaterDelayInSeconds * 1000);
    }



    private void notifyAboutNewOffers(List<Offer> onlyNewOffers) {
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
}
