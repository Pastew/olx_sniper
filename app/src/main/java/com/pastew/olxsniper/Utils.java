package com.pastew.olxsniper;


import java.math.BigDecimal;

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
}
