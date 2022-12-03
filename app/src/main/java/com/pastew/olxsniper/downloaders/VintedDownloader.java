package com.pastew.olxsniper.downloaders;

import android.util.Log;

import com.pastew.olxsniper.db.Offer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VintedDownloader extends AbstractDownloader {

    @Override
    public List<Offer> downloadOffersFromWeb(String url) {
        if (!canHandleLink(url)) {
            throw new InputMismatchException();
        }

        List<Offer> result = new ArrayList<>();
        String html;
        try {
            html = okHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Can't download with okHttpRequest");
            e.printStackTrace();
            return result;
        }

        Document doc = Jsoup.parse(html);

        String jsonStr = doc.getElementsByAttribute("data-js-react-on-rails-store").last().html();
        JSONObject reader = null;
        try {
            reader = new JSONObject(jsonStr);
            JSONArray byId = reader.getJSONObject("catalogItems").getJSONArray("byId"); // tu sie cos sypie TODO
            for(int i = 0; i < byId.length(); ++i) {
                JSONObject offerItem = byId.getJSONObject(i);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }


//        String priceString = priceElement.getElementsByClass("Text_bold__1scEZ").first().html();
//        String link = offerElement.getElementsByClass("ItemBox_overlay__1kNfX").first().attr("href");
//        String title = convertLinkToTitle(link);
        String city = "?";

//        Offer o = new Offer(title, Utils.parsePrice(priceString), link, city);

//        if (o.promoted && IGNORE_PROMOTED_OFFERS) {
//            Log.d(TAG, String.format("Ignored promoted offer: %s", o.link));
//        } else {
//            result.add(o);
//        }

        return result;
    }

    String okHttpRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    // Example link: "https://vinted.pl/kobiety/akcesoria/inne-akcesoria-i-bizuteria/200444610-warcraft-ostatni-straznik"
    private String convertLinkToTitle(String link) {
        String afterLastSlash = link.substring(link.lastIndexOf(',') + 1).trim();
        String titleWithDashes = afterLastSlash.substring(afterLastSlash.indexOf(',') + 1).trim();
        String titleWithSpaces = titleWithDashes.replace("-", " ");
        String titleWithCapitalizedFirstLetter = titleWithSpaces.substring(0, 1).toUpperCase() + titleWithSpaces.substring(1);
        return titleWithCapitalizedFirstLetter;
    }

    @Override
    boolean canHandleLink(String url) {
        return url.contains("vinted.pl");
    }
}
