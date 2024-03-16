package com.pastew.olxsniper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Helpers {
    public static String readFromFile(String fileName) throws IOException {
        ClassLoader classLoader = Helpers.class.getClassLoader();
        assert classLoader != null;
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader
                (inputStream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        return textBuilder.toString();
    }
}
