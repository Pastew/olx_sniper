package com.pastew.olxsniper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class SoupUnitTest {
    static final String URL = "https://www.olx.pl/oferty/q-gtx-1060/";

    @Test
    public void oneOfferExperiments() {
        Document doc;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Elements elements = doc.getElementsByClass("offer");

        //System.out.println(elements.get(0));
        Element el = elements.get(0);
        String priceString = el.getElementsByClass("price").first().getElementsByTag("strong").first().html();

        System.out.println(priceString);
    }

}