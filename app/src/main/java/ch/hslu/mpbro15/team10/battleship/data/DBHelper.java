package ch.hslu.mpbro15.team10.battleship.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Floopy-Doo on 10.05.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE =
                "CREATE TABLE " + DBHandler.TABLE_HIGHSCORE + "("
                        + DBHandler.TABLE_HIGHSCORE_COLUMN_ID + " INTEGER PRIMARY KEY,"
                        + DBHandler.TABLE_HIGHSCORE_COLUMN_DATE + " INTEGER ,"
                        + DBHandler.TABLE_HIGHSCORE_COLUMN_SCORE + " INTEGER"
                        + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBHandler.TABLE_HIGHSCORE);
        onCreate(db);
    }
}
