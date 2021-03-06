package com.pastew.olxsniper.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface OfferDao {

    @Query("SELECT * FROM offer WHERE removed = 0 ORDER BY date DESC")
    List<Offer> getAllNotRemovedByDate();

    @Query("SELECT * FROM offer")
    List<Offer> getAll();

    @Query("SELECT * FROM offer WHERE link LIKE :link LIMIT 1")
    Offer findByLink(String link);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Offer> offerList);

    @Update
    void update(Offer offer);

    @Delete
    void delete(Offer offer);

    @Query("DELETE FROM offer")
    void deleteAll();

    @Query("SELECT * FROM offer WHERE date > :lastTimeUserSeenOffers AND removed != 1 ORDER BY date DESC")
    List<Offer> getOffersNewerThanAndNotRemoved(long lastTimeUserSeenOffers);
}