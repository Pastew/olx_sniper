package com.pastew.olxsniper.olx;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.pastew.olxsniper.MainActivity;
import com.pastew.olxsniper.Utils;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.db.Search;
import com.pastew.olxsniper.db.SniperDatabase;
import com.pastew.olxsniper.db.SniperDatabaseManager;

import java.util.ArrayList;
import java.util.List;

public abstract class OfferDownloader {

    private static final String TAG = MainActivity.TAG;
    private static OfferDownloader instance = null;
    SniperDatabase sniperDatabase;

    protected OfferDownloader(){}

    public static OfferDownloader getInstance(){
        if (instance == null)
            instance = new OlxDownloader();

        return instance;
    }

    public List<Offer> downloadNewOffers(Context context) {
        List<Search> searches = new SniperDatabaseManager(context).getAllSearches();
        if (searches.size() == 0) {
            Log.i(TAG, "No searches. Nothing to do...");
            return new ArrayList<>();
        }


        List<Offer> newOfferList = new ArrayList<>();
        for (Search search : searches) {
            Log.i(TAG, String.format("Downloading from: %s", search.link));
            newOfferList.addAll(this.downloadOffersFromWeb(search.link));
        }
        Log.i(TAG, String.format("Downloaded: %d", newOfferList.size()));

        sniperDatabase = Room.databaseBuilder(context, SniperDatabase.class, SniperDatabase.DATABASE_NAME).build();
        List<Offer> offerList = sniperDatabase.getOfferDao().getAll();
        Log.i(TAG, String.format("From database: %d", offerList.size()));
        List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);
        Log.i(TAG, String.format("Only new: %d", onlyNewOffers.size()));

        if (onlyNewOffers.size() > 0) {
            Log.i(TAG, "Only new > 0");
            sniperDatabase.getOfferDao().insertAll(onlyNewOffers);
            Log.i(TAG, "Only new > 0 -> Afer inserting to DB");
        } else {
            Log.i(TAG, "Checked Web for new offers, but nothing new found");
        }

        Log.i(TAG, "sniperDatabase.close()");
        sniperDatabase.close();

        return onlyNewOffers;
    }

    public abstract List<Offer> downloadOffersFromWeb(String url);

}
