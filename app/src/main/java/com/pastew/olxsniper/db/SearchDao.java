package com.pastew.olxsniper.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface SearchDao {

    @Query("SELECT * FROM search")
    List<Search> getAll();

    @Insert(onConflict = REPLACE)
    void insert(Search search);

    @Query("DELETE FROM search")
    void deleteAll();

    @Delete
    void delete(Search search);
}
