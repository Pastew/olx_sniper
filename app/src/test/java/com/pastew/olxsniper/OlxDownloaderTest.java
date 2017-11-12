package com.pastew.olxsniper;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class OlxDownloaderTest {

    static final String URL = "https://www.olx.pl/elektronika/komputery/akcesoria-i-czesci/q-gtx/";
    static final String URL_PRACA = "https://www.olx.pl/praca/krakow/";
    static final String URL_AUDI = "https://www.olx.pl/motoryzacja/samochody/audi/a3/krakow/?search%5Bfilter_float_enginesize%3Afrom%5D=1500&search%5Bfilter_float_milage%3Ato%5D=200000";
    static final String URL_EXCHANGE = "https://www.olx.pl/zamienie/";
    static final String URL_TMP = "https://www.olx.pl/elektronika/telefony-komorkowe/q-iphone";

    @Test
    public void olrDownloaderTest() {

        OlxDownloader olxDownloader = new OlxDownloader();
        List<Offer> offerList = olxDownloader.downloadOffers(URL_TMP);

        for(Offer o : offerList){
            System.out.println(String.format("%s, %s, %s, %s", o.title, o.price, o.link, o.city));
        }
    }

}