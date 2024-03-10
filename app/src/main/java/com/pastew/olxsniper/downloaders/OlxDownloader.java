package com.pastew.olxsniper.downloaders;

import android.util.Log;

import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlPage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.Utils;

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

        try (WebClient webClient = new WebClient()) {

            webClient.getOptions().setThrowExceptionOnScriptError(false);
            // Enable JavaScript support
            //webClient.getOptions().setJavaScriptEnabled(true);

            // Download the page using HtmlUnit
            HtmlPage page = webClient.getPage(url);

            // Wait for asynchronous content to load
            webClient.waitForBackgroundJavaScript(5000);

            // Parse the page content with JSoup
            Document doc = Jsoup.parse(page.asXml());

            // Continue with your existing JSoup-based code
            Elements elements = doc.getElementsByAttributeValue("data-cy", "l-card");

            if (elements == null) {
                Log.e(TAG, "elements is null. ");
                return result;
            }

            for (Element offerElement : elements) {
                if (offerElement == null) {
                    Log.e(TAG, "offerElement is null. ");
                    continue;
                }

                Element priceElement = offerElement.getElementsByAttributeValue("data-testid", "ad-price").first();
                if (priceElement == null) {
                    Log.e(TAG, "priceElement is null. ");
                    continue;
                }

                String priceString = priceElement.text();

                String title = offerElement.getElementsByClass("css-16v5mdi").text();

                String link = offerElement.getElementsByAttribute("href").first().attr("href");
                String city = "loll";
//                String city = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(0).getElementsByTag("span").first().text();

                Offer o = new Offer(title, Utils.parsePrice(priceString), link, city);

                if (o.promoted && IGNORE_PROMOTED_OFFERS) {
                    Log.d(TAG, String.format("Ignored promoted offer: %s", o.link));
                } else {
                    result.add(o);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "IOException, maybe SocketTimeoutException");
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e(TAG, "Exception occured" + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    boolean canHandleLink(String url) {
        return url.contains("olx.pl");
    }
}
