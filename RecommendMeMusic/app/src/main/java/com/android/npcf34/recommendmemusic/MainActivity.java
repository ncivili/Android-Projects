package com.android.npcf34.recommendmemusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.npcf34.recommendmemusic.app.AppController;
import com.android.npcf34.recommendmemusic.util.AppConstants;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    protected int numListItems = 1;
    private EditText artistText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton searchButton = (ImageButton) findViewById(R.id.lastFmButton);
        SeekBar numItemsBar = (SeekBar) findViewById(R.id.numResultsBar);
        artistText = (EditText) findViewById(R.id.editText);

        searchButton.setOnClickListener(onClickListener);
        numItemsBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String artistName = artistText.getText().toString();
            //build the JSON request
            String requestString =
                    "http://ws.audioscrobbler.com/2.0/?method=artist.getsimilar&artist=" +
                    artistName.replace(" ", "+") + "&autocorrect=1&limit=" + numListItems +
                    "&api_key=" + AppConstants.API_KEY + "&format=json";

            //TODO search last fm with api web call
            Request request = new JsonObjectRequest(
                    requestString, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                                try {
                                    JSONObject similarArtists = response.getJSONObject("similarartists");
                                    JSONArray artistArray = similarArtists.getJSONArray("artist");
                                    ArrayList<String> artistList = new ArrayList<>();
                                    AppConstants.artistMap = new HashMap<>();

                                    for(int i = 0; i < numListItems; i++) {
                                        String artist, lastFmLink;

                                        artist = artistArray.getJSONObject(i).getString("name");
                                        lastFmLink = artistArray.getJSONObject(i).getString("url");

                                        artistList.add(artist);
                                        AppConstants.artistMap.put(artist, lastFmLink);

                                    }

                                    AppConstants.itemsAdapter = new ArrayAdapter<>(getApplicationContext(),
                                           android.R.layout.simple_list_item_1, artistList);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        };
                    }, errorListener
            );

            //Add request to queue
            AppController.addRequestToQueue(request);

            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        };
    };


    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            numListItems = progress + 1;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //TODO: Auto generated method stub
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Toast.makeText(getApplicationContext(), "Number of Results: " + numListItems, Toast.LENGTH_SHORT).show();
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };



}
