package com.pastew.olxsniper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyLogger {
    private static final MyLogger inst = new MyLogger();
    public static final String LOG_FILENAME = "mylog.txt";
    public static final String LOG = "SniperLog";
    private static FileOutputStream outputStream;
    private static Context context;

    private MyLogger() {
        super();
    }

    private static synchronized void logToFile(Context context, String str) {
        try {
            outputStream = context.openFileOutput("mylog.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
            outputStream.write(str.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void i(String str){
        if(context == null)
            Log.e("MyLogger", "MyLogger: context is null");

        Log.i(LOG, str);
        logToFile(context, "I: " + str);
    }

    public static void e(String str){
        if(context == null)
            Log.e(LOG, "MyLogger: context is null");

        Log.e("MyLogger", str);
        logToFile(context, "E: " + str);
    }

    public static void initialize(Context applicationContext) {
        context = applicationContext;
    }

    public void showLogsInDebugWindow(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput(LOG_FILENAME);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.i(LOG, line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}