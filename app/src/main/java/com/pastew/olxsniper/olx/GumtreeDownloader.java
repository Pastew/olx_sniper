package com.pastew.olxsniper.olx;

import android.util.Log;

import com.pastew.olxsniper.MainActivity;
import com.pastew.olxsniper.Utils;
import com.pastew.olxsniper.db.Offer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GumtreeDownloader extends OfferDownloader{
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

        Elements elements = doc.getElementsByClass("result-link");

        if (elements == null) {
            Log.e(MainActivity.TAG, "elemens is null. ");
            return result;
        }

        for (Element offerElement : elements) {
            if (offerElement == null) {
                Log.e(MainActivity.TAG, "offerElement is null. ");
                continue;
            }
            Element titleAnchorElement =
                    offerElement
                    .getElementsByClass("title").first()
                    .getElementsByTag("a").first();

            String title = titleAnchorElement.html();
            String link = "https://www.gumtree.pl" + titleAnchorElement.attr("href");

            String priceString;
            try{
                priceString = offerElement.getElementsByClass("amount").first().html();
            } catch(Exception e){
                priceString = offerElement.getElementsByClass("value").first().html();
            }

            String city =
                    offerElement
                    .getElementsByClass("category-location").first()
                    .getElementsByTag("span").first().html();



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
