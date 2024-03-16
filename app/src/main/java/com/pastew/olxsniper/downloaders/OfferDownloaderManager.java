package com.pastew.olxsniper.downloaders;


import android.content.Context;

import com.pastew.olxsniper.MyLogger;
import com.pastew.olxsniper.Utils;
import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.db.Search;
import com.pastew.olxsniper.db.SniperDatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class OfferDownloaderManager {

    private static OfferDownloaderManager instance = null;
    private SniperDatabaseManager sniperDatabaseManager; // TODO: Refactor, move this to outer scope

    List<AbstractDownloader> webDownloaders;

    private OfferDownloaderManager(Context context) {
        webDownloaders = new ArrayList<>(); // TODO: Refactor, This is stupid
        webDownloaders.add(new OlxDownloader());
        webDownloaders.add(new VintedDownloader());
        webDownloaders.add(new AllegroDownloader());

        sniperDatabaseManager = new SniperDatabaseManager(context);
    }

    public static OfferDownloaderManager getInstance(Context context) {
        if (instance == null)
            instance = new OfferDownloaderManager(context);

        return instance;
    }

    public List<Offer> downloadNewOffersAndSaveToDatabase() {
        List<Search> searches = sniperDatabaseManager.getAllSearches();
        if (searches.size() == 0) {
            MyLogger.i("No searches. Nothing to do...");
            return new ArrayList<>();
        }

        List<Offer> newOfferList = new ArrayList<>();
        for (Search search : searches) {
            String url = search.getUrl();
            MyLogger.i(String.format("Downloading from: %s", url));
            for (AbstractDownloader webDownloader : webDownloaders) {
                if (webDownloader.canHandleLink(url)) {
                    newOfferList.addAll(webDownloader.downloadOffersFromWeb(url));
                }
            }
        }

        MyLogger.i(String.format("Downloaded: %d", newOfferList.size()));

        List<Offer> offerList = sniperDatabaseManager.getAllOffers();
        MyLogger.i(String.format("From database: %d", offerList.size()));

        List<Offer> onlyNewOffers = Utils.getOnlyNewOffers(offerList, newOfferList);
        MyLogger.i(String.format("Only new: %d", onlyNewOffers.size()));

        if (onlyNewOffers.size() > 0) {
            MyLogger.i("Only new > 0");
            sniperDatabaseManager.insertOffers(onlyNewOffers);
            MyLogger.i("Only new > 0 -> After inserting to DB");
        } else {
            MyLogger.i("Checked Web for new offers, but nothing new found");
        }

        // MyLogger.i("sniperDatabase.close()");
        // sniperDatabase.close(); // TODO: Should I close it?

        return onlyNewOffers;
    }
}
