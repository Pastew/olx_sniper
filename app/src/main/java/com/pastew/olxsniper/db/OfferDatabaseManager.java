package com.pastew.olxsniper.db;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.pastew.olxsniper.SharedPrefsManager;

import java.util.List;

public class OfferDatabaseManager {

    private OfferDatabase offerDatabase;
    private Context context;

    public OfferDatabaseManager(Context context) {
        this.context = context;

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

    public void setRemovedFlag(List<Offer> offers, boolean removed) {
        for (Offer offer : offers) {
            offer.removed = removed;
            offerDatabase.getOfferDao().update(offer);
        }
    }

    public List<Offer> getOffersNotSeenByUser() {
        long lastTimeUserSeenOffers = new SharedPrefsManager(context).getLastTimeUserSawOffers();

        return offerDatabase.getOfferDao().getOffersNewerThan(lastTimeUserSeenOffers);
    }
}
