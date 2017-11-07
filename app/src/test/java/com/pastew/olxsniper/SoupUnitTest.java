package com.pastew.olxsniper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SoupUnitTest {

    String TAG = "SoupUnitTest";

    static final String URL = "https://www.olx.pl/oferty/q-gtx-1060/";

    @Test
    public void addition_isCorrect() throws Exception {
        Document doc;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Elements elements = doc.getElementsByClass("offer");

        for (Element element : elements)
            System.out.println(element);
    }
}