package com.pastew.olxsniper;

import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.olx.GumtreeDownloader;
import com.pastew.olxsniper.olx.OfferDownloaderManager;
import com.pastew.olxsniper.olx.OlxDownloader;

import org.junit.Test;

import java.util.List;

public class DownloadersTest {

    static final String URL = "https://www.olx.pl/elektronika/komputery/akcesoria-i-czesci/q-gtx/";
    static final String URL_PRACA = "https://www.olx.pl/praca/krakow/";
    static final String URL_AUDI = "https://www.olx.pl/motoryzacja/samochody/audi/a3/krakow/?search%5Bfilter_float_enginesize%3Afrom%5D=1500&search%5Bfilter_float_milage%3Ato%5D=200000";
    static final String URL_EXCHANGE = "https://www.olx.pl/zamienie/";
    static final String URL_TMP = "https://www.olx.pl/elektronika/telefony-komorkowe/q-iphone";

    static final String URL_GUMTREE_IPHONE = "https://www.gumtree.pl/s-elektronika/iphone/v1c9237q0p1";
    @Test
    public void olxDownloaderTest() {

        OfferDownloaderManager olxDownloader = new OlxDownloader();
        List<Offer> offerList = olxDownloader.downloadOffersFromWeb(URL_TMP);

        for(Offer o : offerList){
            System.out.println(String.format("%s, %s, %s, %s", o.title, o.price, o.link, o.city));
        }
    }

    @Test
    public void gumtreeDownloaderTest() {

        OfferDownloaderManager gumtreeDownloader = new GumtreeDownloader();
        List<Offer> offerList = gumtreeDownloader.downloadOffersFromWeb(URL_GUMTREE_IPHONE);

        for(Offer o : offerList){
            System.out.println(String.format("%s, %s, %s, %s", o.title, o.price, o.link, o.city));
        }
    }

}