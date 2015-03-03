package com.npcf34.android.twittersearches;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends ListActivity {

    // name of SharedPreferences XML file that stores the saved searches
    private static final String SEARCHES = "searches";

    private EditText queryEditText; // EditText where user enters a query
    private EditText tagEditText; // EditText where user tags a query
    private SharedPreferences savedSearches; // user's favorite searches
    private ArrayList<String> tags; // list of tags for saved searches
    private ArrayAdapter<String> adapter; // binds tags to ListView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get references to the EditTexts //TODO
        queryEditText = (EditText) findViewById(R.id.queryEditText);
        tagEditText = (EditText) findViewById(R.id.tagEditText);

        // get the SharedPreferences containing the user's saved searches //TODO
        savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);

        // store the saved tags in an ArrayList then sort them //TODO
        tags = new ArrayList<String>(savedSearches.getAll().keySet());
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);

        // create ArrayAdapter and use it to bind tags to the ListView //TODO
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, tags);
        setListAdapter(adapter);

        // register listener to save a new or edited search //TODO
        ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveButtonListener);

        // register listener that searches Twitter when user touches a tag //TODO
        getListView().setOnItemClickListener(itemClickListener);

        // set listener that allows user to delete or edit a search //TODO
        getListView().setOnItemLongClickListener(itemLongClickListener);

    } // end method onCreate

    // saveButtonListener saves a tag-query pair into SharedPreferences
    public View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addTaggedSearch(queryEditText.getText().toString(), tagEditText.getText().toString());
        }
    };//TODO

    // add new search to the shared preferences, then refresh all Buttons
    private void addTaggedSearch(String query, String tag) //TODO
    {

        if(tag.length() == 0 || query.length() == 0) {
            Toast.makeText(getBaseContext(), "Invalid query or tag", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = savedSearches.edit();
        editor.putString(tag, query);
        editor.apply();
        if(!tags.contains(tag)) {
            tags.add(tag);
            adapter.notifyDataSetChanged();
        }

    }

    // itemClickListener launches a web browser to display search results
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView tagView = (TextView)view;
            String tag = tagView.getText().toString();
            String query = savedSearches.getString(tag, "");
            String url = "http://mobile.twitter.com/search/" + query;
            /*Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);*/
            //Instantiate the RequestQue.
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            //String url = "http://www.google.com

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            mTextView.setText("Response is: "+ response.substring(0,500));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mTextView.setText("That didn't work!");
                    mTextView.setText(new String(error.networkResponse.data));
                }
            });
// Add the request to the RequestQueue.
            queue.add(stringRequest);


        }
    };

    // itemLongClickListener displays a dialog allowing the user to delete
    // or edit a saved search
    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            TextView tagView = (TextView)view;
            final String tag = tagView.getText().toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Please choose your action for " + tag);
            String items[] = new String[]{"Edit", "Share", "Delete"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int index) {
                    switch (index) {
                        case 0: //edit
                            queryEditText.setText(savedSearches.getString(tag, ""));
                            tagEditText.setText(tag);
                            break;
                        case 1: //share
                            shareSearch(tag);
                            break;
                        case 2: //delete
                            deleteSearch(tag);
                            break;
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); //hides dialog
                }
            });
            builder.create().show();
            return true;
        }
    };//TODO

    // allows user to choose an app for sharing a saved search's URL
    private void shareSearch(String tag)
    {

        // create the URL representing the search
        String query = savedSearches.getString(tag, "");
        String url = "http://mobile.twitter.com/search/" + query;
        Uri uri = Uri.parse(url);

        // create Intent to share urlString //TODO
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share the tag " + tag);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the query " + query);
        shareIntent.setType("text/plain");
        Intent chooser = Intent.createChooser(shareIntent, "Message");
        startActivity(chooser);

        // display apps that can share text //TODO
    }

    // deletes a search after the user confirms the delete operation
    private void deleteSearch(final String tag)
    {
        // create a new AlertDialog
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);//TODO
        confirmBuilder.setMessage("Are you sure?");

        // set the AlertDialog's message //TODO

        // set the AlertDialog's negative Button //TODO
        confirmBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // set the AlertDialog's positive Button //TODO
        confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savedSearches.edit().remove(tag).apply();
                tags.remove(tag);
                adapter.notifyDataSetChanged();
            }
        });

        // display AlertDialog //TODO
        confirmBuilder.create().show();

    } // end method deleteSearch

} // end class MainActivity