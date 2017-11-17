package com.pastew.olxsniper.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import java.util.List;

public class OfferDatabaseManager {
    private OfferDatabase offerDatabase;

    public OfferDatabaseManager(Context context) {
        offerDatabase = Room.databaseBuilder(context, OfferDatabase.class,
                OfferDatabase.DATABASE_NAME).fallbackToDestructiveMigration().build();

    }


    public List<Offer> getAllOffers() {
        return offerDatabase.getOfferDao().getAll();
    }

    public void deleteAllOffers() {
        offerDatabase.getOfferDao().deleteAll();
    }
}
