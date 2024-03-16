package com.pastew.olxsniper.downloaders;

import android.util.Log;
import com.pastew.olxsniper.Globals;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebDownloader {
    public static String downloadHtml(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            Log.e(Globals.TAG, "Can't download html from url: [" + url + "]. Returning null");
            return null;
        }
    }
}
