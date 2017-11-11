package com.pastew.olxsniper;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * @param priceString - expected format: "9 600 zł"
     * @return
     */
    public static BigDecimal parsePrice(String priceString) {
        if (priceString.equals("Za darmo"))
            return new BigDecimal(0);

        if (priceString.equals("Zamienię"))
            return new BigDecimal(-1);

        //TODO: Maybe change this to regexp
        priceString = priceString.replaceAll("\\s+","");
        priceString = priceString.replaceAll("zł","");
        priceString = priceString.replaceAll(",",".");
        return new BigDecimal(priceString);
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
        for (Offer oldOffer : offerList){
            if (newOffer.isTheSameOffer(oldOffer))
                return true;
        }
        return false;
    }
}
