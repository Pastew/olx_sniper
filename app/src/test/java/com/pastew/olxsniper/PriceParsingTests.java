package com.pastew.olxsniper;

import org.junit.Test;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PriceParsingTests {


    @Test
    public void parsePriceIntegerTest() {
        BigDecimal expected = new BigDecimal(9000);
        BigDecimal parsed = Utils.parsePrice("9 000 zł");
        assertEquals(expected, parsed);
    }

    @Test
    public void parsePriceFloatTest() {
        BigDecimal expected = new BigDecimal("9450.20");
        BigDecimal parsed = Utils.parsePrice("9 450,20 zł");
        assertEquals(expected, parsed);
    }

    @Test
    public void parsePriceZeroTest() {
        BigDecimal expected = new BigDecimal("0");
        BigDecimal parsed = Utils.parsePrice("Za darmo");
        assertEquals(expected, parsed);
    }
}