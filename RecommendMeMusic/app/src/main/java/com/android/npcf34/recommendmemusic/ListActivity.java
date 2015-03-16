package com.android.npcf34.recommendmemusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.npcf34.recommendmemusic.util.AppConstants;


public class ListActivity extends Activity {

    protected ListView resultsList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        resultsList = (ListView) findViewById(R.id.artistDisplayView);

        resultsList.setAdapter(AppConstants.itemsAdapter);
        findViewById(R.id.backButton).setOnClickListener(onClickListener);
        resultsList.setOnLongClickListener(onLongClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    ListView.OnLongClickListener onLongClickListener = new ListView.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //TODO: set up intent to take user to last.fm page for artist

            Intent intent = new Intent();
            startActivity(intent);
            return true;
        };
    };

}
