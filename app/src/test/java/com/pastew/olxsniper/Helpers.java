package com.pastew.olxsniper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Helpers {
    public static String readFromFile(Class runtimeClass, String filePath) throws IOException {
        InputStream inputStream = Objects.requireNonNull(runtimeClass.getClassLoader()).getResourceAsStream(filePath);
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
