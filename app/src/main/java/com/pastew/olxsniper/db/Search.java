package com.pastew.olxsniper.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Search {

    private static final String BASE_LINK = "https://www.olx.pl/";
    @NonNull
    @PrimaryKey
    public String link;

    public String text;
    public int priceMin;
    public int priceMax;
    public int category;
    public String city;

    public Search(String text, int priceMin, int priceMax, int category, String city) {
        this.text = text;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.category = category;
        this.city = city;

        generateLink();
    }

    private void generateLink() {
        link = BASE_LINK + "q-" + text.replace(" ", "-");
    }
}
