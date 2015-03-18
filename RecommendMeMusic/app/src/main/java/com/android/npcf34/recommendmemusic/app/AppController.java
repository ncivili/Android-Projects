package com.android.npcf34.recommendmemusic.app;

import android.app.Application;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

/**
 * Created by Nick on 3/15/2015.
 */
public class AppController extends Application {
    private static AppController instance;
    private static RequestQueue requestQueue;
    public static ArrayAdapter<String> itemsAdapter = null;
    public static HashMap<String, String> artistMap = null;
    public static int numListItems = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppController i() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }

    public static void addRequestToQueue (Request request) {
        i().getRequestQueue().add(request);
    }
}
