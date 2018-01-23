package com.pastew.olxsniper.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"text", "priceMin", "priceMax", "category", "city"})
public class Search {

    private static final String BASE_LINK = "https://www.olx.pl/";

    @NonNull
    public String text;
    @NonNull
    public int priceMin;
    @NonNull
    public int priceMax;
    @NonNull
    public int category;
    @NonNull
    public String city;

    public Search(String text, int priceMin, int priceMax, int category, String city) {
        this.text = text;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.category = category;
        this.city = city;

        getLink();
    }

    public Search() {
        city = "";
        text = "";
        priceMax = 0;
        priceMin = 0;
        category = 0;
    }

    public String getLink() {
            return BASE_LINK + "oferty/q-" + text.replace(" ", "-");
    }
}
