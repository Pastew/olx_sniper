package com.pastew.olxsniper.db;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

public class OfferDatabaseManager {
    private OfferDatabase offerDatabase;

    public OfferDatabaseManager(Context context) {
        offerDatabase = Room.databaseBuilder(context, OfferDatabase.class,
                OfferDatabase.DATABASE_NAME).fallbackToDestructiveMigration().build();
    }

    public List<Offer> getAllNotRemovedOffers() {
        return offerDatabase.getOfferDao().getAllNotRemovedByDate();
    }

    public void deleteAllOffers() {
        offerDatabase.getOfferDao().deleteAll();
    }

    public void setRemovedFlag(Offer offer, boolean flagValue) {
        offer.removed = flagValue;
        offerDatabase.getOfferDao().update(offer);
    }
}
