package com.pastew.olxsniper;

import com.pastew.olxsniper.db.Search;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchTests {


    @Test
    public void OnlyTextTest() {
        String expected = "https://www.olx.pl/oferty/q-xbox-pad";
        Search search = new Search("xbox pad", 0, 0, "");
        assertEquals(expected, search.getUrl());
    }


}