package com.pastew.olxsniper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class JSoupExperiments {
    static final String URL = "https://www.olx.pl/elektronika/komputery/akcesoria-i-czesci/q-gtx/";
    static final String URL_PRACA = "https://www.olx.pl/praca/krakow/";
    static final String URL_AUDI = "https://www.olx.pl/motoryzacja/samochody/audi/a3/krakow/?search%5Bfilter_float_enginesize%3Afrom%5D=1500&search%5Bfilter_float_milage%3Ato%5D=200000";
    static final String URL_EXCHANGE = "https://www.olx.pl/zamienie/";
    static final String URL_TMP = "https://www.olx.pl/zwierzeta/pozostale-zwierzeta/";

    @Test
    public void offerExperiments() {
        Document doc;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Elements elements = doc.getElementsByClass("offer");

        //System.out.println(elements.get(0));
        for (Element offerElement : elements) {
            String priceString = offerElement.getElementsByClass("price").first().getElementsByTag("strong").first().html();

            Element h3 = offerElement.getElementsByTag("h3").first();
            String title = h3.getElementsByTag("strong").first().html();

            String link = h3.getElementsByTag("a").first().attr("href");

            String city = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(0).getElementsByTag("span").first().html();

            String dateString = offerElement.getElementsByTag("tr").get(1).getElementsByTag("p").get(1).html();

            System.out.println("\n-----------");
            System.out.println(title);
            System.out.println(priceString);
            System.out.println(link);
            System.out.println(city);
            System.out.println(dateString);
        }
    }

}