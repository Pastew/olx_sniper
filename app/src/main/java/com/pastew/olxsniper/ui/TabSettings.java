package com.pastew.olxsniper.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pastew.olxsniper.R;
import com.pastew.olxsniper.db.SniperDatabaseManager;

public class TabSettings extends Fragment {

    private Context context;
    private View view;
    private SniperDatabaseManager sniperDatabaseManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_settings, container, false);
        context = getContext();
        setupButtons();
        this.sniperDatabaseManager = new SniperDatabaseManager(context);
        return view;
    }

    private void setupButtons() {
        view.findViewById(R.id.clearOffersButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TabSettings.DeleteAllOffersFromDatabase().execute();
            }
        });

        view.findViewById(R.id.clearSearchesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TabSettings.DeleteAllSearchesFromDatabase().execute();
            }
        });
    }

    private class DeleteAllOffersFromDatabase extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... voids) {
            sniperDatabaseManager.deleteAllOffers();
            return null;
        }
    }

    private class DeleteAllSearchesFromDatabase extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... voids) {
            sniperDatabaseManager.deleteAllSearches();
            return null;
        }
    }
}