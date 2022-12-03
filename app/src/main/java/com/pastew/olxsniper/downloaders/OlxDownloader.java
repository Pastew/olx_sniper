package com.pastew.olxsniper.downloaders;

import android.util.Log;

import com.pastew.olxsniper.Globals;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class OlxDownloader extends AbstractDownloader {

    @Override
    public List<Offer> downloadOffersFromWeb(String url) {
        if (!canHandleLink(url)) {
            throw new InputMismatchException();
        }

        List<Offer> result = new ArrayList<>();

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            Log.e(TAG, "IOException, maybe SocketTimeoutException");
            e.printStackTrace();
            return result;
        }

        Elements elements = doc.getElementsByClass("offer");

        if (elements == null) {
            Log.e(TAG, "elemens is null. ");
            return result;
        }

        for (Element offerElement : elements) {
            if (offerElement == null) {
                Log.e(TAG, "offerElement is null. ");
                continue;
            }

            Element priceElement = offerElement.getElementsByClass("price").first();
            if (priceElement == null) {
                Log.e(TAG, "priceElement is null. ");
                continue;
            }

            String priceString = priceElement.getElementsByTag("strong").first().html();

            Element h3 = offerElement.getElementsByTag("h3").first();
            String title = h3.getElementsByTag("strong").first().html();

            String link = h3.getElementsByTag("a").first().attr("href");
            String city = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(0).getElementsByTag("span").first().text();

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
