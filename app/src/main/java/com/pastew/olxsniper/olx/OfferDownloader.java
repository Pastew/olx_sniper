package com.pastew.olxsniper.olx;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.pastew.olxsniper.MainActivity;
import com.pastew.olxsniper.Utils;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.db.OfferDatabase;

import java.util.List;

public abstract class OfferDownloader {

    private static final String TAG = MainActivity.TAG;

    public List<Offer> downloadNewOffers(Context context, String url) {
        OfferDatabase offerDatabase = Room.databaseBuilder(context, OfferDatabase.class, OfferDatabase.DATABASE_NAME).build();
        List<Offer> newOfferList = this.downloadOffersFromWeb(url);
        List<Offer> offerList = offerDatabase.getOfferDao().getAll();

        List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);

        if (onlyNewOffers.size() > 0) {
            offerDatabase.getOfferDao().insertAll(onlyNewOffers);

        } else {
            Log.i(TAG, "Checked Web for new offers, but nothing new found");
        }

        offerDatabase.close();

        return onlyNewOffers;
    }

    public abstract List<Offer> downloadOffersFromWeb(String url);

}
