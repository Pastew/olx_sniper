package com.pastew.olxsniper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OlxDownloader {


    public List<Offer> downloadOffers(String url) {
        List<Offer> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date d = null;

        try {
            d = sdf.parse("12/02/2017");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        result.add(new Offer(
                "Kurteczka zimowa",
                new BigDecimal("12.32"),
                "https://www.olx.pl/oferta/kurteczka-zimowa-CID88-IDpzhrt.html",
                "Gdańsk",
                d
        ));

        result.add(new Offer(
                "UŻYWANE spodnie Myszka Minnie rozm 110/116",
                new BigDecimal("10"),
                "https://www.olx.pl/oferta/uzywane-spodnie-myszka-minnie-rozm-110-116-CID88-IDq0I3T.html#7bf985e501",
                "Gdańsk",
                d
        ));



        // TODO: implement this
        return result;
    }
}
