package com.pastew.olxsniper;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OlxDownloader {


    private static final boolean IGNORE_PROMOTED_OFFERS = true;
    //TODO: move this to user prefs.

    public List<Offer> downloadOffers(String url) {
        List<Offer> result = new ArrayList<>();

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }

        Elements elements = doc.getElementsByClass("offer");

        for (Element offerElement : elements) {
            String priceString = offerElement.getElementsByClass("price").first().getElementsByTag("strong").first().html();

            Element h3 = offerElement.getElementsByTag("h3").first();
            String title = h3.getElementsByTag("strong").first().html();

            String link = h3.getElementsByTag("a").first().attr("href");
            String city = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(0).getElementsByTag("span").first().html();

            String dateString = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(1).html();

            Offer o = new Offer(title, Utils.parsePrice(priceString), link, city, dateString);

            if (o.isPromotedOffer && IGNORE_PROMOTED_OFFERS) {
                Log.d(MainActivity.TAG, String.format("Ignored promoted offer: %s", o.link));
            }
            else{
                result.add(o);
            }
        }

        return result;
    }
}
