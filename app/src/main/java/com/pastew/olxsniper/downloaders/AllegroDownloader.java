package com.pastew.olxsniper.downloaders;

import android.util.Log;

import com.pastew.olxsniper.Globals;
import com.pastew.olxsniper.Utils;
import com.pastew.olxsniper.db.Offer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class AllegroDownloader extends AbstractDownloader {
    @Override
    public List<Offer> downloadOffersFromWeb(String url) {
        if (!canHandleLink(url)) {
            throw new InputMismatchException();
        }

        List<Offer> result = new ArrayList<>();
        String html;
        try {
            html = WebDownloader.downloadHtml(url);
        } catch (IOException e) {
            Log.e(TAG, "Can't download with okHttpRequest");
            e.printStackTrace();
            return result;
        }

        Document doc = Jsoup.parse(html);


        Elements elements = doc.getElementsByTag("article");

        if (elements == null) {
            Log.e(Globals.TAG, "elemens is null. ");
            return result;
        }

        for (Element offerElement : elements) {
            if (offerElement == null) {
                Log.e(Globals.TAG, "offerElement is null. ");
                continue;
            }
            String title = offerElement.getElementsByTag("a").first().getElementsByTag("img").attr("alt");
            String link = offerElement.getElementsByTag("a").first().attr("href");

            //Element e = offerElement.getElementsByClass("e82f23a").first();
            //String priceString = e.html();
            String priceString = "unknown";


            String city = "unknown";

            Offer o = new Offer(title, Utils.parsePrice(priceString), link, city);

            if (o.promoted && IGNORE_PROMOTED_OFFERS) {
                Log.d(Globals.TAG, String.format("Ignored promoted offer: %s", o.link));
            } else {
                result.add(o);
            }
        }

        return result;
    }

    @Override
    boolean canHandleLink(String url) {
        return url.contains("allegro.pl");
    }
}
