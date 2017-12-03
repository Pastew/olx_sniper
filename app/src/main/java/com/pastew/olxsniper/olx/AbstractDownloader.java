package com.pastew.olxsniper.olx;


import com.pastew.olxsniper.Globals;
import com.pastew.olxsniper.db.Offer;

import java.util.List;

abstract class AbstractDownloader {

    static final boolean IGNORE_PROMOTED_OFFERS = true;
    //TODO: move this to user prefs.
    static final String TAG = Globals.TAG;

    abstract public List<Offer> downloadOffersFromWeb(String url);

    abstract boolean canHandleLink(String url);
}
