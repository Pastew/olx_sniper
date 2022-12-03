package com.pastew.olxsniper.downloaders;

import android.util.Log;

import com.pastew.olxsniper.Globals;
import com.pastew.olxsniper.Utils;
import com.pastew.olxsniper.db.Offer;

import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class GumtreeDownloader extends AbstractDownloader {
    @Override
    public List<Offer> downloadOffersFromWeb(String url) {
        if (!canHandleLink(url)) {
            throw new InputMismatchException();
        }

        List<Offer> result = new ArrayList<>();

        Document doc;
        try {
            doc = WebDownloader.downloadDocumentJsoup(url);
        } catch (IOException e) {
            Log.e(Globals.TAG, "IOException, maybe SocketTimeoutException");
            e.printStackTrace();
            return result;
        } catch (UncheckedIOException e){
            Log.e(Globals.TAG, "UncheckedIOException");
            e.printStackTrace();
            return result;
        }

        Elements elements = doc.getElementsByClass("tileV1");

        if (elements == null) {
            Log.e(Globals.TAG, "elemens is null. ");
            return result;
        }

        for (Element offerElement : elements) {
            if (offerElement == null) {
                Log.e(Globals.TAG, "offerElement is null. ");
                continue;
            }
            Element titleAnchorElement =
                    offerElement
                    .getElementsByClass("title").first()
                    .getElementsByTag("a").first();

            String title = titleAnchorElement.html();
            String link = "https://www.gumtree.pl" + titleAnchorElement.attr("href");

            String priceString = getPriceString(offerElement);
            if (priceString == null){
                Log.w(TAG, String.format("Can't get priceString for %s", link));
            }

            String city =
                    offerElement
                    .getElementsByClass("category-location").first()
                    .getElementsByTag("span").first().html();

            city = city.replace("&nbsp;"," ");

            Offer o = new Offer(title, Utils.parsePrice(priceString), link, city);

            if (o.promoted && IGNORE_PROMOTED_OFFERS) {
                Log.d(Globals.TAG, String.format("Ignored promoted offer: %s", o.link));
            } else {
                result.add(o);
            }
        }

        return result;
    }

    private String getPriceString(Element offerElement) {
        String priceString;
        try{
            priceString = offerElement.getElementsByClass("ad-price").first().html();
        } catch(Exception e){
            priceString = null;
        }

        if (priceString != null)
            return priceString;

        try{
            priceString = offerElement.getElementsByClass("value").first().html();
        } catch(Exception e){
            priceString = null;
        }

        return priceString;
    }

    @Override
    boolean canHandleLink(String url) {
        return url.contains("gumtree.pl");
    }
}
