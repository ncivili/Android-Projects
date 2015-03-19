package com.android.npcf34.recommendmemusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.npcf34.recommendmemusic.app.AppController;


public class ListActivity extends Activity {

    protected ListView resultsList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        TextView artistText = (TextView) findViewById(R.id.artistSearchedForText);
        artistText.setText(AppController.artist);

        resultsList = (ListView) findViewById(R.id.artistDisplayView);

        resultsList.setAdapter(AppController.itemsAdapter);
        findViewById(R.id.backButton).setOnClickListener(onClickListener);
        resultsList.setOnItemLongClickListener(onItemLongClickListener);
        resultsList.setOnItemClickListener(itemClickListener);



    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    ListView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            String artistKey = parent.getItemAtPosition(position).toString();
            String lastFmUrl ="http://" + AppController.artistMap.get(artistKey);

            intent.setData(Uri.parse(lastFmUrl));
            startActivity(intent);
        }
    };

    ListView.OnItemLongClickListener onItemLongClickListener = new ListView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return false;
        }
    };

}
