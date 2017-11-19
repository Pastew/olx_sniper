package com.pastew.olxsniper.olx;


import android.util.Log;

import com.pastew.olxsniper.MainActivity;
import com.pastew.olxsniper.db.Offer;

import java.util.ArrayList;
import java.util.List;

abstract class WebDownloader {

    protected static final boolean IGNORE_PROMOTED_OFFERS = true;
    //TODO: move this to user prefs.
    protected static final String TAG = MainActivity.TAG;

    abstract public List<Offer> downloadOffersFromWeb(String url);

    abstract boolean canHandleLink(String url);
}
