package com.pastew.olxsniper;

import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.downloaders.VintedDownloader;

import org.junit.Test;

import java.util.List;

public class DownloadersVintedTest {

    @Test
    public void vintedDownloaderTest() {
        String URL_VINTED_WARCRAFT = "https://www.vinted.pl/ubrania?search_text=warcraft";

        VintedDownloader vintedDownloader = new VintedDownloader();
        List<Offer> offerList = vintedDownloader.getOffersFromUrl(URL_VINTED_WARCRAFT);

        assert !offerList.isEmpty();
        for (Offer o : offerList) {
            System.out.println(String.format("%s, %s, %s, %s", o.title, o.price, o.link, o.city));
        }
    }
}