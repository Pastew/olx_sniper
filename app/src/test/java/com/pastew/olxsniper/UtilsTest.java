package com.pastew.olxsniper;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void getOnlyNewOffersTest() {
        Offer o1, o2, o3;

        o1 = new Offer("Miód lipowy",
                new BigDecimal(25),
                "https://www.olx.pl/oferta/miod-lipowy-CID757-IDnRdc0.html",
                "Świerklany",
                "dzisiaj 07:55");

        o2 = new Offer("Zbior 2017 miód lipowy rzepakowyy mniszkowy gryczany spadziowy wiel",
                new BigDecimal(26),
                "https://www.olx.pl/oferta/zbior-2017-miod-lipowy-rzepakowyy-mniszkowy-gryczany-spadziowy-wiel-CID757-IDklDjc.html#6a6e36936f;promoted",
                "Świerklany",
                "dzisiaj 07:55");

        o3 = new Offer("Miód lipowy",
                new BigDecimal(25),
                "https://www.olx.pl/oferta/miod-lipowy-CID757-IDnRdc0.html",
                "Świerklany",
                "dzisiaj 07:55");

        ArrayList<Offer> offers = new ArrayList<>();

        // Add o1 - one offer should be added
        offers.addAll(Utils.getOnlyNewOffers(offers, new ArrayList<>(Collections.singletonList(o1))));
        assertEquals(offers.size(), 1);

        // Add the same object - nothing should be added
        offers.addAll(Utils.getOnlyNewOffers(offers, new ArrayList<>(Collections.singletonList(o1))));
        assertEquals(offers.size(), 1);

        // Add o2 - other object with other data - 2 object should be in a list
        offers.addAll(Utils.getOnlyNewOffers(offers, new ArrayList<>(Collections.singletonList(o2))));
        assertEquals(offers.size(), 2);

        // Add o3 - other object with the same data - nothing should be added
        offers.addAll(Utils.getOnlyNewOffers(offers, new ArrayList<>(Collections.singletonList(o3))));
        assertEquals(offers.size(), 2);
    }
}