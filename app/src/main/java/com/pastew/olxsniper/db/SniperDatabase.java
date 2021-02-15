package com.pastew.olxsniper.db;


import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Offer.class, Search.class}, version = 11)
public abstract class SniperDatabase extends RoomDatabase {

    private static SniperDatabase INSTANCE;

    public static final String DATABASE_NAME = SniperDatabase.class.getSimpleName();

    public abstract OfferDao getOfferDao();

    public abstract SearchDao getSearchDao();

    public static SniperDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, SniperDatabase.class, SniperDatabase.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

}
