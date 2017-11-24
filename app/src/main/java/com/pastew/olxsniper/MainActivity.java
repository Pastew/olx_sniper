package com.pastew.olxsniper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.pastew.olxsniper.db.Search;
import com.pastew.olxsniper.db.SniperDatabaseManager;
import com.pastew.olxsniper.olx.OfferDownloaderManager;
import com.pastew.olxsniper.olx.OlxDownloader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String TAG = "OLXSniper";
    public static final String OLX_URL = "https://www.olx.pl/elektronika/telefony-komorkowe/q-iphone";
    public static final String OLX_URL_IPHONE = "https://www.olx.pl/oferty/q-iphone-5s/?search%5Bfilter_float_price%3Afrom%5D=400&search%5Bfilter_float_price%3Ato%5D=500";
    public static final String DATABASE_UPDATE_BROADCAST = "com.pastew.olxsniper.DATABASE_UPDATE";
    private int updaterDelayInSeconds = 60;

    private OffersAdapter adapter;
    private List<Offer> offerList;

    private SniperDatabaseManager sniperDatabaseManager;

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
        new SetSampleSearches().execute(
                "https://www.olx.pl/elektronika/telefony-komorkowe/iphone/q-iphone-5s/?search%5Bfilter_float_price%3Afrom%5D=400&search%5Bfilter_float_price%3Ato%5D=500",
                "https://www.olx.pl/elektronika/q-xbox-pad/?search%5Bfilter_float_price%3Ato%5D=120",
                "https://www.olx.pl/elektronika/q-pilne/?search%5Bfilter_float_price%3Ato%5D=500");
    }

    private void setupOfferDbManager() {
        this.sniperDatabaseManager = new SniperDatabaseManager(this);
    }

    @Override
    protected void onResume() {
        registerReceiver();
        adapter.notifyDataSetChanged();
        new DownloadOffersFromDatabaseTask().execute();
        super.onResume();
    }

    @Override
    protected void onPause() {
        new SharedPrefsManager(context).setLastTimeUserSawOffersToNow();
        unregisterReceiver();
        super.onPause();
    }

    private void registerReceiver() {
        databaseUpdateBroadcastReceiver = new DatabaseUpdateBroadcastReceiver();
        registerReceiver(databaseUpdateBroadcastReceiver, filter);
    }

    private void unregisterReceiver() {
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
                MyLogger.getInstance().showLogsInDebugWindow(context);
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        offerList = new ArrayList<>();
        adapter = new OffersAdapter(getApplicationContext(), offerList);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                        this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
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

        findViewById(R.id.removeAllOffersButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetRemovedFlagTaskTrueForOffers().execute(offerList);
            }
        });
    }

    private void notifyUserAboutNewOffers(List<Offer> onlyNewOffers) {
        int offersNotSeenByUser = 0;
        for (Offer offer : onlyNewOffers)
            if (!Utils.checkIfOfferWasSeenByUser(context, offer))
                ++offersNotSeenByUser;

        if (offersNotSeenByUser == 0)
            return;

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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof OffersAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = offerList.get(viewHolder.getAdapterPosition()).title;

            // backup of removed item for undo purpose
            final Offer deletedItem = offerList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            // set in database "removed" flag
            new SetRemovedFlagTaskTrue().execute(deletedItem);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.constrainLayout), "Usunięto " + name, Snackbar.LENGTH_LONG);
            snackbar.setAction("COFNIJ", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedItem, deletedIndex);
                    new SetRemovedFlagTaskFalse().execute(deletedItem);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private class DownloadOffersFromDatabaseTask extends AsyncTask<Void, Integer, List<Offer>> {
        protected List<Offer> doInBackground(Void... params) {
            List<Offer> newOfferList = sniperDatabaseManager.getAllNotRemovedOffers();
            List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);
            return onlyNewOffers;
        }

        protected void onPostExecute(List<Offer> onlyNewOffers) {
            if (onlyNewOffers.size() > 0) {
                offerList.addAll(0, onlyNewOffers);
                adapter.notifyDataSetChanged();
                //adapter.notifyItemRangeInserted(0, onlyNewOffers.size()); // TODO: check if it works

                notifyUserAboutNewOffers(onlyNewOffers);
            } else {
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

    private class SetSampleSearches extends AsyncTask<String, Integer, Void> {
        protected Void doInBackground(String... params) {
            for (String link : params) {
                Search search = new Search(link);
                sniperDatabaseManager.addSearch(search);
            }
            return null;
        }
    }

    private class DownloadNewOffersFromOlxTask extends AsyncTask<String, Integer, Void> {
        protected Void doInBackground(String... urls) {
            OfferDownloaderManager.getInstance().downloadNewOffers(context);
            return null;
        }

        protected void onPostExecute(Void param) {
            new DownloadOffersFromDatabaseTask().execute();
            ((Button) findViewById(R.id.refreshDatabaseButton)).setText("Odśwież");
            ((Button) findViewById(R.id.refreshDatabaseButton)).setEnabled(true);
        }
    }

    private class DeleteAllOffersFromDatabase extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... voids) {
            sniperDatabaseManager.deleteAllOffers();
            return null;
        }
    }

    private class SetRemovedFlagTaskTrueForOffers extends AsyncTask<List<Offer>, Void, Void> {
        protected Void doInBackground(List<Offer>... offers) {
            sniperDatabaseManager.setRemovedFlag(offers[0], true);
            return null;
        }

        protected void onPostExecute(Void param) {
            int size = offerList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    offerList.remove(0);
                }

                adapter.notifyItemRangeRemoved(0, size);
            }
        }
    }

    private class SetRemovedFlagTaskTrue extends AsyncTask<Offer, Void, Void> {
        protected Void doInBackground(Offer... offers) {
            sniperDatabaseManager.setRemovedFlag(offers[0], true);
            return null;
        }
    }

    private class SetRemovedFlagTaskFalse extends AsyncTask<Offer, Void, Void> {
        protected Void doInBackground(Offer... offers) {
            sniperDatabaseManager.setRemovedFlag(offers[0], false);
            return null;
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
