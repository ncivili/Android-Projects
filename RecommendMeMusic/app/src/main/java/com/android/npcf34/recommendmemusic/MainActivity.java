package com.android.npcf34.recommendmemusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.npcf34.recommendmemusic.app.AppController;
import com.android.npcf34.recommendmemusic.util.AppConstants;
import com.android.npcf34.recommendmemusic.util.JSONParser;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


public class MainActivity extends Activity {

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
                            artistName.replace(" ", "+") + "&autocorrect=1&limit=" +
                            AppController.numListItems +
                            "&api_key=" + AppConstants.API_KEY + "&format=json";

            //search last fm with api web call
            Request request = new JsonObjectRequest(
                    requestString, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            JSONParser parser = new JSONParser(response, AppController.numListItems,
                                    getApplicationContext());
                            int numResponses = parser.parseJSON();

                            if(numResponses > 0) {
                                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("The search returned no results!\n\nYou may have misspelled the " +
                                        "artist's name,\nor your artist may not be in the Last.Fm database.")
                                        .setTitle("Oops!");
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    }, errorListener
            );

            //Add request to queue
            AppController.addRequestToQueue(request);
            AppController.i().getRequestQueue().start();
    }

    };


    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            AppController.numListItems = seekBar.getProgress() + 1;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Do Nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            AppController.numListItems = seekBar.getProgress();
            Toast.makeText(getApplicationContext(), "Number of Results: " + AppController.numListItems,
                    Toast.LENGTH_SHORT).show();
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("Volley Error", error.getLocalizedMessage());
        }
    };



}
