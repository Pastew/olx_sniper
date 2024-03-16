package com.pastew.olxsniper;


import android.content.Context;
import android.util.Log;

import com.pastew.olxsniper.db.Offer;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * @param priceString - expected format: "9 600 zł"
     * @return
     */
    public static String parsePrice(String priceString) {
        if (priceString == null)
            return "?";
        
        if (priceString.contains("Za darmo"))
            return "Za darmo";

        if (priceString.contains("Zamienię"))
            return "Zamienię";

        //TODO: Maybe change this to regexp
        priceString = priceString.replaceAll("\\s+","");
        priceString = priceString.replaceAll("zł","");
        priceString = priceString.replaceAll("&nbsp;","");
        priceString = priceString.replaceAll(",",".");
        priceString = priceString.replaceAll("donegocjacji","");
        return priceString;
    }


    public static List<Offer> getOnlyNewOffers(List<Offer> oldOfferList, List<Offer> newOfferList) {
        long startTime = System.nanoTime();
        List<Offer> onlyNewOffers = new ArrayList<>();

        for (Offer newOffer : newOfferList){
            if(!offerIsAlreadyAdded(newOffer, oldOfferList)) {
                onlyNewOffers.add(newOffer);
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        Log.e("OlxSniper", String.format("getOnlyNewOffers took %d seconds!", duration/1000000/1000));
        return onlyNewOffers;
    }

    private static boolean offerIsAlreadyAdded(Offer newOffer, List<Offer> offerList) {
        for (Offer oldOffer : offerList) {
            if (newOffer.isTheSameOffer(oldOffer))
                return true;
        }
        return false;
    }

    public static boolean checkIfOfferWasSeenByUser(Context context, Offer offer) {
        long lastTimeUserSeasOffers = new SharedPrefsManager(context).getLastTimeUserSawOffers();
        return offer.date < lastTimeUserSeasOffers;
    }
}
