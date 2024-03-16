package com.pastew.olxsniper.downloaders;


import com.pastew.olxsniper.Globals;
import com.pastew.olxsniper.db.Offer;

import java.util.List;

abstract class AbstractDownloader {

    static final boolean IGNORE_PROMOTED_OFFERS = false;
    //TODO: move this to user prefs.
    static final String TAG = Globals.TAG;

    abstract public List<Offer> getOffersFromUrl(String url);
    abstract public List<Offer> getOffersFromHtml(String html);

    abstract boolean canHandleLink(String url);
}
