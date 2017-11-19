package com.pastew.olxsniper.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Search {

    @NonNull
    @PrimaryKey
    public String link;

    public Search(String link){
        this.link = link;
    }
}
