package com.pastew.olxsniper.db;


import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

import java.util.List;


@Database(entities = {Offer.class, Search.class}, version = 5)
public abstract class SniperDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = SniperDatabase.class.getSimpleName();

    public abstract OfferDao getOfferDao();
    public abstract SearchDao getSearchDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

}
