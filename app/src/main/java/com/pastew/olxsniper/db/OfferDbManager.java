package com.pastew.olxsniper.db;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

public class OfferDbManager {
    private OfferDatabase offerDatabase;

    public OfferDbManager(Context context) {
        offerDatabase = Room.databaseBuilder(context, OfferDatabase.class, OfferDatabase.DATABASE_NAME).build();

    }

    public List<Offer> getAllOffers() {
        return offerDatabase.getOfferDao().getAll();
    }

    public void deleteAllOffers() {
        offerDatabase.getOfferDao().deleteAll();
    }
}
