package com.pastew.olxsniper.db;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"url"})
public class Search {

    private static final String BASE_LINK = "https://www.olx.pl/";

    @NonNull
    public String url;

    public Search(String url) {
        this.url = url;

        getUrl();
    }

    public Search() {
        url = "";
    }

    public String getUrl() {
        return url;
    }
}