package com.npcf34.android.cannongame;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.npcf34.android.cannongame.Util.DatabaseConnector;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        switch(id) {
            case R.id.action_about:
                //about screen
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_reset:
                //reset game stats
                DatabaseConnector databaseConnector = new DatabaseConnector(getApplicationContext());
                databaseConnector.open();
                databaseConnector.deleteScores();
                databaseConnector.close();
                break;
            case R.id.action_scores:
                StringBuilder sb = new StringBuilder();
                sb.append("Scores\n").append("\nLevel 1:\n");

                DatabaseConnector databaseConnector1 = new DatabaseConnector(getApplicationContext());
                databaseConnector1.open();

                Cursor l1Cursor = databaseConnector1.getScoresByLevel(1);
                Cursor l2Cursor = databaseConnector1.getScoresByLevel(2);
                Cursor l3Cursor = databaseConnector1.getScoresByLevel(3);

                for(int i = 0; i < l1Cursor.getCount(); i++) {
                    sb.append(l1Cursor.getInt(i)).append("\n");
                }

                sb.append("\nLevel 2:\n");

                for(int i = 0; i < l2Cursor.getCount(); i++) {
                    sb.append(l2Cursor.getInt(i)).append("\n");
                }

                sb.append("\nLevel 3:\n");

                for(int i = 0; i < l3Cursor.getCount(); i++) {
                    sb.append(l1Cursor.getInt(i)).append("\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Scores");
                builder.setMessage(sb.toString());
                builder.create().show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
