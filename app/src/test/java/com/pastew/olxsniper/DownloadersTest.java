package com.pastew.olxsniper;

import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.olx.AllegroDownloader;
import com.pastew.olxsniper.olx.GumtreeDownloader;
import com.pastew.olxsniper.olx.OfferDownloaderManager;
import com.pastew.olxsniper.olx.OlxDownloader;

import org.junit.Test;

import java.util.List;

public class DownloadersTest {

    @Test
    public void olxDownloaderTest() {
        final String URL = "https://www.olx.pl/elektronika/komputery/akcesoria-i-czesci/q-gtx/";
        final String URL_PRACA = "https://www.olx.pl/praca/krakow/";
        final String URL_AUDI = "https://www.olx.pl/motoryzacja/samochody/audi/a3/krakow/?search%5Bfilter_float_enginesize%3Afrom%5D=1500&search%5Bfilter_float_milage%3Ato%5D=200000";
        final String URL_EXCHANGE = "https://www.olx.pl/zamienie/";
        String URL_OLX = "https://www.olx.pl/elektronika/telefony-komorkowe/q-iphone";

        OlxDownloader olxDownloader = new OlxDownloader();
        List<Offer> offerList = olxDownloader.downloadOffersFromWeb(URL_OLX);

        for (Offer o : offerList) {
            System.out.println(String.format("%s, %s, %s, %s", o.title, o.price, o.link, o.city));
        }
    }

    @Test
    public void gumtreeDownloaderTest() {
        String URL_GUMTREE_IPHONE = "https://www.gumtree.pl/s-elektronika/iphone/v1c9237q0p1";

        GumtreeDownloader gumtreeDownloader = new GumtreeDownloader();
        List<Offer> offerList = gumtreeDownloader.downloadOffersFromWeb(URL_GUMTREE_IPHONE);

        for (Offer o : offerList) {
            System.out.println(String.format("%s, %s, %s, %s", o.title, o.price, o.link, o.city));
        }
    }

    @Test
    public void allegroDownloaderTest() {
        String URL_ALLEGRO_GTX = "https://allegro.pl/kategoria/obraz-i-grafika-karty-graficzne-257236?string=gtx%201060&order=n&stan=używane";

        AllegroDownloader allegroDownloader = new AllegroDownloader();
        List<Offer> offerList = allegroDownloader.downloadOffersFromWeb(URL_ALLEGRO_GTX);

        for (Offer o : offerList) {
            System.out.println(String.format("%s, %s, %s, %s", o.title, o.price, o.link, o.city));
        }
    }

}