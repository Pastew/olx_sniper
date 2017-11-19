package com.pastew.olxsniper;

import org.junit.Test;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PriceParsingTests {


    @Test
    public void parsePriceIntegerTest() {
        String expected = "9000";
        String parsed = Utils.parsePrice("9 000 zł");
        assertEquals(expected, parsed);
    }

    @Test
    public void parsePriceFloatTest() {
        String expected = "9450.20";
        String parsed = Utils.parsePrice("9 450,20 zł");
        assertEquals(expected, parsed);
    }

    @Test
    public void parsePriceZeroTest() {
        String expected = "Za darmo";
        String parsed = Utils.parsePrice("Za darmo");
        assertEquals(expected, parsed);
    }

    @Test
    public void parsePriceExchangeTest() {
        String expected = "Zamienię";
        String parsed = Utils.parsePrice("Zamienię");
        assertEquals(expected, parsed);
    }

    @Test
    public void parsePricenbspTest() {
        String expected = "1000";
        String parsed = Utils.parsePrice("1&nbsp;000 zł");
        assertEquals(expected, parsed);
    }

}