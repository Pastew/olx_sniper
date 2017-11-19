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

    public List<Offer> downloadNewOffers(Context context) {
        List<Search> searches = new SniperDatabaseManager(context).getAllSearches();
        if (searches.size() == 0) {
            Log.i(TAG, "No searches. Nothing to do...");
            return new ArrayList<>();
        }

        SniperDatabase sniperDatabase = Room.databaseBuilder(context, SniperDatabase.class, SniperDatabase.DATABASE_NAME).build();

        List<Offer> newOfferList = new ArrayList<>();
        for (Search search : searches)
            newOfferList.addAll(this.downloadOffersFromWeb(search.link));

        List<Offer> offerList = sniperDatabase.getOfferDao().getAll();

        List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);

        if (onlyNewOffers.size() > 0) {
            sniperDatabase.getOfferDao().insertAll(onlyNewOffers);
        } else {
            Log.i(TAG, "Checked Web for new offers, but nothing new found");
        }

        sniperDatabase.close();

        return onlyNewOffers;
    }

    public abstract List<Offer> downloadOffersFromWeb(String url);

}
