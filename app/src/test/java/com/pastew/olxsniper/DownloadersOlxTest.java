package com.pastew.olxsniper;

import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.downloaders.OlxDownloader;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class DownloadersOlxTest {

    @Test
    public void olxDownloadFromUrlTest() {
        final String URL = "https://www.olx.pl/elektronika/komputery/akcesoria-i-czesci/q-gtx/";
        final String URL_PRACA = "https://www.olx.pl/praca/krakow/";
        final String URL_AUDI = "https://www.olx.pl/motoryzacja/samochody/audi/a3/krakow/?search%5Bfilter_float_enginesize%3Afrom%5D=1500&search%5Bfilter_float_milage%3Ato%5D=200000";
        final String URL_EXCHANGE = "https://www.olx.pl/zamienie/";
        String URL_OLX = "https://www.olx.pl/elektronika/telefony-komorkowe/q-iphone";

        OlxDownloader olxDownloader = new OlxDownloader();
        List<Offer> offerList = olxDownloader.getOffersFromUrl(URL_OLX);

        assert !offerList.isEmpty();
        for (Offer o : offerList) {
            System.out.println(o);
        }
    }

    @Test
    public void olxDownloadFromHtmlTest() throws IOException {
        String fileName = "olxExampleIphone.html";
        String html = Helpers.readFromFile(fileName);

        OlxDownloader olxDownloader = new OlxDownloader();
        List<Offer> offerList = olxDownloader.getOffersFromHtml(html);

        assert !offerList.isEmpty();
        for (int i = 0; i < offerList.size(); i++) {
            Offer o = offerList.get(i);
            System.out.println(i + 1 + ") " + o);
        }
    }
}