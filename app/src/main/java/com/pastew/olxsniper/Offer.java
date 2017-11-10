package com.pastew.olxsniper;


import java.math.BigDecimal;
import java.util.Date;

public class Offer {
    public String title;
    public BigDecimal price;
    public String link;
    public String city;
    public Date addedDate;

    public Offer(String title, BigDecimal price, String link, String city, Date addedDate) {
        this.title = title;
        this.price = price;
        this.link = link;
        this.city = city;
        this.addedDate = addedDate;
    }
}
