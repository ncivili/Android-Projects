package com.android.npcf34.recommendmemusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.npcf34.recommendmemusic.app.AppController;


public class SavedArtistsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_artists);

        findViewById(R.id.backToMainButton).setOnClickListener(onClickListener);
        ListView savedList = (ListView) findViewById(R.id.savedListView);
        savedList.setAdapter(AppController.savedItemsAdapter);
        savedList.setOnItemClickListener(itemClickListener);

    }

    ListView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //create and launch the implicit intent, the last.fm page for the selected artist
            //using a web browser (or the last.fm app if the user has it installed).
            Intent intent = new Intent(Intent.ACTION_VIEW);

            String artistKey = parent.getItemAtPosition(position).toString();
            String lastFmUrl = AppController.savedArtistMap.get(artistKey);

            intent.setData(Uri.parse(lastFmUrl));
            startActivity(intent);
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Go to the main activity
            Intent intent = new Intent(SavedArtistsActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

}
