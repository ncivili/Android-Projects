package com.android.npcf34.recommendmemusic.util;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.android.npcf34.recommendmemusic.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nick on 3/17/2015.
 */
public class JSONParser {
    JSONObject response;
    int numListItems;
    Context context;

    public JSONParser(JSONObject response, int numListItems, Context context) {
        this.response = response;
        this.numListItems = numListItems;
        this.context = context;
    }

    public void parseJSON() {
        try {
            JSONObject similarArtists = response.getJSONObject("similarartists");
            JSONArray artistArray = similarArtists.getJSONArray("artist");
            ArrayList<String> artistList = new ArrayList<>();
            AppController.artistMap = new HashMap<>();

            for (int i = 0; i < numListItems; i++) {
                String artist, lastFmLink;

                artist = artistArray.getJSONObject(i).getString("name");
                lastFmLink = artistArray.getJSONObject(i).getString("url");

                artistList.add(artist);
                AppController.artistMap.put(artist, lastFmLink);
            }

            AppController.itemsAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, artistList);
            AppController.itemsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
