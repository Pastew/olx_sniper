package com.pastew.olxsniper.downloaders;

import android.util.Log;

import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class OlxDownloader extends AbstractDownloader {

    @Override
    public List<Offer> getOffersFromUrl(String url) {
        if (!canHandleLink(url)) {
            throw new InputMismatchException();
        }

        String html = WebDownloader.downloadHtml(url);
        return getOffersFromHtml(html);
    }

    @Override
    public List<Offer> getOffersFromHtml(String html) {
        if (html == null || html.isEmpty()){
            Log.e(TAG, "html is null or empty, can't parse it. Returning empty list");
            return new ArrayList<>();
        }

        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByAttributeValue("data-cy", "l-card");

        List<Offer> result = new ArrayList<>();

        if (elements == null) {
            Log.e(TAG, "elements is null. ");
            return result;
        }

        for (Element offerElement : elements) {
            if (offerElement == null) {
                Log.e(TAG, "offerElement is null. ");
                continue;
            }

            String priceString = offerElement.getElementsByAttributeValue("data-testid", "ad-price").text();
            String title = offerElement.getElementsByClass("css-16v5mdi").text();
            String link = offerElement.getElementsByAttribute("href").first().attr("href");
            String locationDate = offerElement.getElementsByAttributeValue("data-testid", "location-date").text(); // Myszków<!-- --> - <!-- -->Odświeżono dnia 13 marca 2024
            String city = locationDate.split("-")[0].trim();

            Offer o = new Offer(title, Utils.parsePrice(priceString), link, city);

            if (o.promoted && IGNORE_PROMOTED_OFFERS) {
                Log.d(TAG, String.format("Ignored promoted offer: %s", o.link));
            } else {
                result.add(o);
            }
        }

        return result;
    }

    @Override
    boolean canHandleLink(String url) {
        return url.contains("olx.pl");
    }
}
