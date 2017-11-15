package com.pastew.olxsniper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.db.OfferDatabaseManager;
import com.pastew.olxsniper.olx.OlxDownloader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "OLXSniper";
    public static final String OLX_URL = "https://www.olx.pl/elektronika/telefony-komorkowe/q-iphone";
    public static final String DATABASE_UPDATE_BROADCAST = "com.pastew.olxsniper.DATABASE_UPDATE";
    private int updaterDelayInSeconds = 600;

    private RecyclerView.Adapter adapter;
    private List<Offer> offerList;

    private OfferDatabaseManager offerDatabaseManager;

    private IntentFilter filter = new IntentFilter(DATABASE_UPDATE_BROADCAST);
    private DatabaseUpdateBroadcastReceiver databaseUpdateBroadcastReceiver;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        context = this;

        setupStrictMode();
        setupFab();
        setupRecyclerView();
        setupButtons();
        setupService();
        setupOfferDbManager();
    }

    private void setupOfferDbManager() {
        this.offerDatabaseManager = new OfferDatabaseManager(this);
    }

    @Override
    protected void onResume() {
        databaseUpdateBroadcastReceiver = new DatabaseUpdateBroadcastReceiver();
        registerReceiver(databaseUpdateBroadcastReceiver, filter);
        new DownloadOffersFromDatabaseTask().execute();
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(databaseUpdateBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. This is exactly what is desired
                Log.w(TAG, "Tried to unregister the reciver when it's not registered");
            } else {
                // unexpected, re-throw
                throw e;
            }
        }
        super.onPause();
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

    private void setupService() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        dispatcher.cancelAll();
        Job myJob = dispatcher.newJobBuilder()
                .setService(UpdaterJobService.class)
                .setTag(UpdaterJobService.class.getSimpleName())
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(updaterDelayInSeconds, updaterDelayInSeconds + 1))
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(true)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    private void setupStrictMode() {
        //TODO: Find out why it is needed
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: here should open window with added new OLX Listner
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        offerList = new ArrayList<>();
        adapter = new MyAdapter(getApplicationContext(), offerList);
        recyclerView.setAdapter(adapter);
    }

    private void setupButtons() {
        findViewById(R.id.clearDatabaseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteAllOffersFromDatabase().execute();
                offerList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.refreshDatabaseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) v).setText("Sprawdzam...");
                ((Button) v).setEnabled(false);
                new DownloadNewOffersFromOlxTask().execute();
            }
        });
    }

    private void changeColorsOfOldOffers() {
        for (Offer o : offerList)
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
    }

    private class DownloadOffersFromDatabaseTask extends AsyncTask<Void, Integer, List<Offer>> {
        protected List<Offer> doInBackground(Void... params) {
            List<Offer> newOfferList = offerDatabaseManager.getAllOffers();
            List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);
            return onlyNewOffers;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(List<Offer> onlyNewOffers) {
            if (onlyNewOffers.size() > 0) {
                changeColorsOfOldOffers();

                offerList.addAll(0, onlyNewOffers);
                adapter.notifyDataSetChanged();

                notifyUserAboutNewOffers(onlyNewOffers);

                Log.i(TAG, String.format("New offers found: %d", onlyNewOffers.size()));
                for (int i = 0; i < onlyNewOffers.size(); ++i) {
                    Offer o = onlyNewOffers.get(i);
                    Log.i(TAG, String.format("%d. %s, %s %s", i + 1, o.title, o.addedDate, o.link));
                }
            } else {
                Log.i(TAG, "Checked OLX for new offers in db, but nothing new found");

                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.constrainLayout), String.format("Nie ma nowych ofert."), Snackbar.LENGTH_LONG)
                        .setAction("Nie klikaj!", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(MainActivity.this, "Miałeś nie klikać!", Toast.LENGTH_SHORT).show();
                            }
                        });
                snackbar.show();
            }
        }
    }

    private class DownloadNewOffersFromOlxTask extends AsyncTask<String, Integer, Void> {

        protected void onPreExecute() {
        }

        protected Void doInBackground(String... urls) {
            new OlxDownloader().downloadNewOffers(context, MainActivity.OLX_URL);
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Void param) {
            new DownloadOffersFromDatabaseTask().execute();
            ((Button) findViewById(R.id.refreshDatabaseButton)).setText("Odśwież");
            ((Button) findViewById(R.id.refreshDatabaseButton)).setEnabled(true);
        }
    }

    private class DeleteAllOffersFromDatabase extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... voids) {
            offerDatabaseManager.deleteAllOffers();
            return null;
        }

        protected void onProgressUpdate(Void... voids) {
        }

        protected void onPostExecute(Integer result) {
        }
    }

    private class DatabaseUpdateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new DownloadOffersFromDatabaseTask().execute();
            Log.i(MainActivity.TAG, "broadcast received");
        }
    }
}
