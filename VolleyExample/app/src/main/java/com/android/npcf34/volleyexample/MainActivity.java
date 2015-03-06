package com.android.npcf34.volleyexample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        volleyJsonRequest();
        volleyJsonArrayRequest();
        //volleyNetworkRequest();

    }

    private void volleyJsonRequest() {

        final TextView mTextView = (TextView) findViewById(R.id.text);

        // Instantiate the RequestQueue.
        String url ="http://www.w3schools.com/website/Customers_MYSQL.php";

        Response.Listener<JSONObject> jsonResponseListener = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                mTextView.setText("Response is: " + response.toString());
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        };

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, null, jsonResponseListener, errorListener);


        // Add the request to the RequestQueue.
        queue.add(jsonRequest);

    }

    private void volleyJsonArrayRequest() {

        final TextView mTextView = (TextView) findViewById(R.id.text);

        // Instantiate the RequestQueue.
        String url ="http://www.w3schools.com/website/Customers_MYSQL.php";

        Response.Listener<JSONArray> jsonResponseListener = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                mTextView.setText("Response is: " + response.toString());
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        };

        // Request a string response from the provided URL.
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, jsonResponseListener, errorListener);


        // Add the request to the RequestQueue.
        queue.add(jsonRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void volleyNetworkRequest() {

        final TextView mTextView = (TextView) findViewById(R.id.text);

        // Instantiate the RequestQueue.
        String url ="http://www.google.com";

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
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
