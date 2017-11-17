package com.pastew.olxsniper.db;


import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {Offer.class}, version = 2)
public abstract class OfferDatabase extends RoomDatabase {
    public abstract OfferDao getOfferDao();


    public static final String DATABASE_NAME = OfferDatabase.class.getSimpleName();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
