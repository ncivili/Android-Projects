package com.npcf34.android.cannongame.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nick on 4/24/15.
 */
public class DatabaseConnector {

    // database name
    private static final String DATABASE_NAME = "npcf34CannonGame";

    private SQLiteDatabase database; // for interacting with the database
    private DatabaseOpenHelper databaseOpenHelper; // creates the database

    // public constructor for DatabaseConnector
    public DatabaseConnector(Context context)
    {
        // create a new DatabaseOpenHelper
        databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    // open the database connection
    public void open() throws SQLException
    {
        // create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    }

    // close the database connection
    public void close()
    {
        if (database != null)
            database.close(); // close the database connection
    }

    // inserts a new score into the database
    public long insertScore(double score, int level)
    {
        ContentValues newScore = new ContentValues();
        newScore.put("score", score);
        newScore.put("level", level);

        open(); // open the database
        long rowID = database.insert("scores", null, newScore);
        close(); // close the database
        return rowID;
    }

    // updates an existing contact in the database
    public void updateScore(long id, double score, int level)
    {
        ContentValues editScore = new ContentValues();
        editScore.put("score", score);
        editScore.put("level", level);

        open(); // open the database
        database.update("scores", editScore, "_id=" + id, null);
        close(); // close the database
    } // end method updateScore

    // return a Cursor with all contact names in the database
    public Cursor getAllScores()
    {
        return database.query("scores", new String[] {"score", "level"}, null, null,
                null, null, "score");
    }

    public Cursor getScoresByLevel(int level) {
        return database.query("scores", null, "level=" + level, null,
                null, null, "score");
    }

    // delete the contact specified by the given String name
    public void deleteScores()
    {
        open(); // open the database
        database.delete("scores", null, null);
        close(); // close the database
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        // constructor
        public DatabaseOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        // creates the scores table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // query to create a new table named scores
            String createQuery = "CREATE TABLE scores" +
                    "(_id integer primary key autoincrement," +
                    "score REAL," + " level INTEGER);";

            db.execSQL(createQuery); // execute query to create the database
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
        }
    } // end class DatabaseOpenHelper

}// end class DatabaseConnector
