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
        Element offerElement = elements.get(0);
        String priceString = offerElement.getElementsByClass("price").first().getElementsByTag("strong").first().html();

        Element h3 = offerElement.getElementsByTag("h3").first();
        String title = h3.getElementsByTag("strong").first().html();

        String link = h3.getElementsByTag("a").first().attr("href");

        String city = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(0).getElementsByTag("span").first().html();

        String dateString = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(1).html();

        System.out.println(title);
        System.out.println(priceString);
        System.out.println(link);
        System.out.println(city);
        System.out.println(dateString);

    }

}