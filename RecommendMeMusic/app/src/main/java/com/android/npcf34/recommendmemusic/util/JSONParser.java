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

    public int parseJSON() {
        try {
            //get the JSONObject from the response
            JSONObject similarArtists = response.getJSONObject("similarartists");
            //grab the JSON array containing the artists
            JSONArray artistArray = similarArtists.getJSONArray("artist");
            //get the JSONObject and get the string of the artist user searched for
            AppController.artist = similarArtists.getJSONObject("@attr").getString("artist");
            //get the number of returned responses
            int numResponses = similarArtists.length();

            //initialize the ArrayList for the adapter and Hashmap for storing the artist/url
            //key value pairs
            ArrayList<String> artistList = new ArrayList<>();
            AppController.artistMap = new HashMap<>();

            if(numResponses > 0) {
                //iterate over the JSON Array and get each individual artist
                for (int i = 0; i < numListItems; i++) {
                    String artist, lastFmLink;

                    artist = artistArray.getJSONObject(i).getString("name");
                    lastFmLink = artistArray.getJSONObject(i).getString("url");

                    //add each similar artist to the arraylist and hashmap
                    artistList.add(artist);
                    AppController.artistMap.put(artist, lastFmLink);
                }

                //set the adapter for the ListView
                AppController.itemsAdapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1, artistList);
                AppController.itemsAdapter.notifyDataSetChanged();
            }

            //return the number of responses
            return numResponses;
        } catch (JSONException e) {
            //if we have a json error we need to return 0 and show the alert dialog
            e.printStackTrace();
            return 0;
        }
    }
}
