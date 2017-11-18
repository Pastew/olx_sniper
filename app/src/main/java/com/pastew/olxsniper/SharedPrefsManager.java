package com.pastew.olxsniper;
import android.content.SharedPreferences;



import android.content.Context;
import android.preference.PreferenceManager;

public class SharedPrefsManager {

    private final String LAST_TIME_USER_SEEN_OFFERS = "LAST_TIME_USER_SEEN_OFFERS";
    private SharedPreferences sharedPref;

    public SharedPrefsManager(Context context){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public long getLastTimeUserSawOffers(){
        return sharedPref.getLong(LAST_TIME_USER_SEEN_OFFERS, System.currentTimeMillis() * 2);
    }

    public void setLastTimeUserSawOffersToNow(){
        sharedPref.edit().putLong(LAST_TIME_USER_SEEN_OFFERS, System.currentTimeMillis()).apply();
    }
}
