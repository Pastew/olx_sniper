package com.pastew.olxsniper.db;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Offer{

    @NonNull
    @PrimaryKey
    public String link;

    public String title;
    public String price;
    public String city;
    public long date;
    public boolean promoted;
    public boolean removed;

    public Offer(String title, String price, String link, String city) {
        this.title = title;
        this.price = price;
        this.link = link;
        this.city = city;
        this.date = System.currentTimeMillis();
        this.promoted = link.contains("promoted");
        this.removed = false;
    }

    public boolean isTheSameOffer(@NonNull Offer o) {
        String thisItem = new StringBuilder(title).append(price).append(link).append(city).toString();
        String otherItem = new StringBuilder(o.title).append(o.price).append(o.link).append(o.city).toString();
        return thisItem.equals(otherItem);
    }
}
