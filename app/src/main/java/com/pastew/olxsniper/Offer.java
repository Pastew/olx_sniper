package com.pastew.olxsniper;


import android.support.annotation.NonNull;

import java.math.BigDecimal;

public class Offer{
    public String title;
    public BigDecimal price;
    public String link;
    public String city;
    public String addedDate;

    public Offer(String title, BigDecimal price, String link, String city, String addedDate) {
        this.title = title;
        this.price = price;
        this.link = link;
        this.city = city;
        this.addedDate = addedDate;
    }

    public boolean isTheSameOffer(@NonNull Offer o) {
        String thisItem = new StringBuilder(title).append(price).append(link).append(city).toString();
        String otherItem = new StringBuilder(o.title).append(o.price).append(o.link).append(o.city).toString();
        return thisItem.equals(otherItem);
    }
}
