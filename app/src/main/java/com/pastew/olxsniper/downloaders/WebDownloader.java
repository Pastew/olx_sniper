package com.pastew.olxsniper.downloaders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebDownloader {
    public static Document downloadDocumentJsoup(String url) throws IOException {
        return Jsoup.connect(url)
                .get();
    }

    public static String okHttpRequest(String url) throws IOException {
        try {
            doGetRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static void doGetRequest(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // For the example, you can show an error dialog or a toast
//                                // on the main UI thread
//                            }
//                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();

                        // Do something with the response
                    }
                });
    }
}
