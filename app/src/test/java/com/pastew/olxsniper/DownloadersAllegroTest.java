package com.pastew.olxsniper;

import com.pastew.olxsniper.db.Offer;

import org.junit.Test;

import java.util.List;

public class DownloadersAllegroTest {

    @Test
    public void allegroDownloaderTest() {
        String URL_ALLEGRO_GTX = "https://allegro.pl/kategoria/obraz-i-grafika-karty-graficzne-257236?string=gtx%201060&order=n&stan=używane";

        AllegroDownloader allegroDownloader = new AllegroDownloader();
        List<Offer> offerList = allegroDownloader.downloadOffersFromWeb(URL_ALLEGRO_GTX);

        assert !offerList.isEmpty();
        for (Offer o : offerList) {
            System.out.println(String.format("%s, %s, %s, %s", o.title, o.price, o.link, o.city));
        }
    }
}