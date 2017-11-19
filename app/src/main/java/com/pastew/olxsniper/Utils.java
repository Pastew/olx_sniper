package com.pastew.olxsniper;


import android.content.Context;

import com.pastew.olxsniper.db.Offer;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * @param priceString - expected format: "9 600 zł"
     * @return
     */
    public static String parsePrice(String priceString) {
        if (priceString.contains("Za darmo"))
            return "Za darmo";

        if (priceString.contains("Zamienię"))
            return "Zamienię";

        //TODO: Maybe change this to regexp
        priceString = priceString.replaceAll("\\s+","");
        priceString = priceString.replaceAll("zł","");
        priceString = priceString.replaceAll("&nbsp;","");
        priceString = priceString.replaceAll(",",".");
        return priceString;
    }


    /**
     * @param oldOfferList
     * @param newOfferList
     * @return Returns if at least one offer was added
     */
    public static List<Offer> getOnlyNewOffers(List<Offer> oldOfferList, List<Offer> newOfferList) {
        List<Offer> onlyNewOffers = new ArrayList<>();

        for (Offer newOffer : newOfferList){
            if(!offerIsAlreadyAdded(newOffer, oldOfferList)) {
                onlyNewOffers.add(newOffer);
            }
        }

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
