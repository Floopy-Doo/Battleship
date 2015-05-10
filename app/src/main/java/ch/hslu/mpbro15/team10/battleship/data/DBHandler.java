package ch.hslu.mpbro15.team10.battleship.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Floopy-Doo on 10.05.2015.
 */
public class DBHandler {
    private static final String DATABASE_NAME = "highscore.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_HIGHSCORE = "highscore";
    public static final String TABLE_HIGHSCORE_COLUMN_ID = "_id";
    public static final String TABLE_HIGHSCORE_COLUMN_DATE = "date";
    public static final String TABLE_HIGHSCORE_COLUMN_SCORE = "score";

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public DBHandler(final Context context) {
        dbHelper = new DBHelper(context, DATABASE_NAME, DATABASE_VERSION);
    }

    /**
     * Open DB connection
     */
    public void open() {
        if (db == null || !db.isOpen()) {
            db = dbHelper.getWritableDatabase();
        }
    }

    /**
     * Close DB connection
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Insers new Highscore entry into database
     *
     * @param highscore object
     * @return true if highscore successfully inserted
     */
    public boolean insertHighscore(final HighscoreObject highscore) {
        final ContentValues value = new ContentValues();
        value.put(TABLE_HIGHSCORE_COLUMN_DATE, highscore.getDate().getTime());
        value.put(TABLE_HIGHSCORE_COLUMN_SCORE, highscore.getPoints());

        final long id = db.insert(TABLE_HIGHSCORE, null, value);
        highscore.setID(id);
        return true;
    }

    /**
     * returns a cursor for all highscores
     *
     * @return cursor
     */
    public Cursor getAllHighscoresCursor() {
        String[] columns =
                {TABLE_HIGHSCORE_COLUMN_ID,
                        TABLE_HIGHSCORE_COLUMN_DATE, TABLE_HIGHSCORE_COLUMN_SCORE};
        return db.query(TABLE_HIGHSCORE,                    // table
                columns,                                    //select
                null,
                null,
                null,
                null,
                TABLE_HIGHSCORE_COLUMN_SCORE + " DESC");     //order by
    }

    /**
     * deletes all existing highscores
     *
     * @return true if highscores were deleted
     */
    public boolean deleteAllHighscores() {
        return db.delete(TABLE_HIGHSCORE, null, null) > 0;
    }
}