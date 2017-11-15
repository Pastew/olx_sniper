package com.pastew.olxsniper;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class OlxDownloader {
    private static final boolean IGNORE_PROMOTED_OFFERS = true;
    //TODO: move this to user prefs.

    private static final String TAG = MainActivity.TAG;


    public void downloadNewOffers(Context context, String url) {
        OfferDatabase offerDatabase = Room.databaseBuilder(context, OfferDatabase.class, OfferDatabase.DATABASE_NAME).build();

        List<Offer> newOfferList = new OlxDownloader().downloadOffersFromOlx(url);
        List<Offer> offerList = offerDatabase.getOfferDao().getAll();
        //Log.i(TAG, String.format("Offers from databaase(%d)", offerList.size()));
        for (int i = 0; i < offerList.size(); ++i) {
            Offer o = offerList.get(i);
            //Log.i(TAG, String.format("    %d. %s, %s %s", i + 1, o.title, o.addedDate, o.link));
        }

        List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);

        if (onlyNewOffers.size() > 0) {
            //Log.i(TAG, String.format("New offers found, those will be added to database: %d", onlyNewOffers.size()));
            for (int i = 0; i < onlyNewOffers.size(); ++i) {
                Offer o = onlyNewOffers.get(i);
                //Log.i(TAG, String.format("%d. %s, %s %s", i + 1, o.title, o.addedDate, o.link));
            }

            offerDatabase.getOfferDao().insertAll(onlyNewOffers);
            MediaPlayer notificationMediaPlayer = MediaPlayer.create(context, R.raw.notification1);
            notificationMediaPlayer.start();

            Intent i = new Intent(MainActivity.DATABASE_UPDATE_BROADCAST);
            //i.putExtra("url", "bleble");
            context.sendBroadcast(i);

        } else {
            Log.i(TAG, "Checked OLX for new offers in OLX, but nothing new found");
        }

        offerDatabase.close();
    }


    List<Offer> downloadOffersFromOlx(String url) {
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

            Offer o = new Offer(title, Utils.parsePrice(priceString), link, city, dateString);

            if (o.isPromotedOffer && IGNORE_PROMOTED_OFFERS) {
                Log.d(MainActivity.TAG, String.format("Ignored promoted offer: %s", o.link));
            } else {
                result.add(o);
            }
        }

        return result;
    }
}
