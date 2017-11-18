package com.pastew.olxsniper.olx;

import android.util.Log;

import com.pastew.olxsniper.MainActivity;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OlxDownloader extends OfferDownloader{
    private static final boolean IGNORE_PROMOTED_OFFERS = true;
    //TODO: move this to user prefs.

    private static final String TAG = MainActivity.TAG;

    @Override
    public List<Offer> downloadOffersFromWeb(String url) {
        List<Offer> result = new ArrayList<>();

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            Log.e(MainActivity.TAG, "IOException, maybe SocketTimeoutException");
            e.printStackTrace();
            return result;
        }

        Elements elements = doc.getElementsByClass("offer");

        if (elements == null) {
            Log.e(MainActivity.TAG, "elemens is null. ");
            return result;
        }

        for (Element offerElement : elements) {
            if (offerElement == null) {
                Log.e(MainActivity.TAG, "offerElement is null. ");
                continue;
            }

            Element priceElement = offerElement.getElementsByClass("price").first();
            if (priceElement == null) {
                Log.e(MainActivity.TAG, "priceElement is null. ");
                continue;
            }

            String priceString = priceElement.getElementsByTag("strong").first().html();

            Element h3 = offerElement.getElementsByTag("h3").first();
            String title = h3.getElementsByTag("strong").first().html();

            String link = h3.getElementsByTag("a").first().attr("href");
            String city = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(0).getElementsByTag("span").first().html();

            String dateString = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(1).html();

            Offer o = new Offer(title, Utils.parsePrice(priceString), link, city);

            if (o.promoted && IGNORE_PROMOTED_OFFERS) {
                Log.d(MainActivity.TAG, String.format("Ignored promoted offer: %s", o.link));
            } else {
                result.add(o);
            }
        }

        return result;
    }
}
