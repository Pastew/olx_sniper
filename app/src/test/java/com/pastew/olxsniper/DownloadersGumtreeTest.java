package com.pastew.olxsniper;

import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.downloaders.AllegroDownloader;
import com.pastew.olxsniper.downloaders.GumtreeDownloader;
import com.pastew.olxsniper.downloaders.OlxDownloader;
import com.pastew.olxsniper.downloaders.VintedDownloader;

import org.junit.Test;

import java.util.List;

public class DownloadersGumtreeTest {

    @Test
    public void gumtreeDownloaderTest() {
        String URL_GUMTREE_IPHONE = "https://www.gumtree.pl/s-elektronika/iphone/v1c9237q0p1";
        String URL_GUMTREE_FLAT = "https://www.gumtree.pl/s-mieszkania-i-domy-do-wynajecia/krakow/v1c9008l3200208p1?pr=%2C2100&pf=1&priceType=FIXED";

        GumtreeDownloader gumtreeDownloader = new GumtreeDownloader();
        List<Offer> offerList = gumtreeDownloader.downloadOffersFromWeb(URL_GUMTREE_FLAT);

        assert !offerList.isEmpty();
        for (Offer o : offerList) {
            System.out.println(o);
        }
    }
}