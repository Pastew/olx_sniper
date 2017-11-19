package com.pastew.olxsniper.db;

import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.pastew.olxsniper.SharedPrefsManager;

import java.util.List;

public class SniperDatabaseManager {

    private SniperDatabase sniperDatabase;
    private Context context;

    public SniperDatabaseManager(Context context) {
        this.context = context;

        sniperDatabase = Room.databaseBuilder(context, SniperDatabase.class,
                SniperDatabase.DATABASE_NAME).fallbackToDestructiveMigration().build();
    }

    public List<Offer> getAllNotRemovedOffers() {
        return sniperDatabase.getOfferDao().getAllNotRemovedByDate();
    }

    public void deleteAllOffers() {
        sniperDatabase.getOfferDao().deleteAll();
    }

    public void setRemovedFlag(Offer offer, boolean flagValue) {
        offer.removed = flagValue;
        sniperDatabase.getOfferDao().update(offer);
    }

    public void setRemovedFlag(List<Offer> offers, boolean removed) {
        for (Offer offer : offers) {
            offer.removed = removed;
            sniperDatabase.getOfferDao().update(offer);
        }
    }

    public List<Offer> getOffersNotSeenByUserAndNotRemoved() {
        long lastTimeUserSeenOffers = new SharedPrefsManager(context).getLastTimeUserSawOffers();

        return sniperDatabase.getOfferDao().getOffersNewerThanAndNotRemoved(lastTimeUserSeenOffers);
    }

    // === SEARCHES ===
    public List<Search> getAllSearches() {
        return sniperDatabase.getSearchDao().getAll();
    }

    @OnConflictStrategy
    public void addSearch(Search search) {
        sniperDatabase.getSearchDao().insert(search);
    }
}
