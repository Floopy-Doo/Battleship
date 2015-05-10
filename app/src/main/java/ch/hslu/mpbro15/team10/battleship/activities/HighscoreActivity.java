package ch.hslu.mpbro15.team10.battleship.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hslu.mpbro15.team10.battleship.R;
import ch.hslu.mpbro15.team10.battleship.data.DBHandler;


public class HighscoreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListViewData();


        findViewById(R.id.btnResetHighscores)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBHandler dbHandler = new DBHandler(HighscoreActivity.this);
                        dbHandler.open();
                        dbHandler.deleteAllHighscores();
                        dbHandler.close();

                        loadListViewData();
                    }
                });
    }

    private void loadListViewData() {
        DBHandler dbHandler = new DBHandler(this);
        dbHandler.open();


        List<Map<String, String>> scores = new ArrayList<>();
        Cursor cur = dbHandler.getAllHighscoresCursor();
        if (cur.moveToFirst()) {
            do {
                Map<String, String> item = new HashMap<>();
                Date date = new Date();
                date.setTime(cur.getLong(cur.getColumnIndex(DBHandler.TABLE_HIGHSCORE_COLUMN_DATE)));

                item.put(DBHandler.TABLE_HIGHSCORE_COLUMN_DATE,
                        new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(date));
                item.put(DBHandler.TABLE_HIGHSCORE_COLUMN_SCORE,
                        "" + cur.getInt(cur.getColumnIndex(DBHandler.TABLE_HIGHSCORE_COLUMN_SCORE)));
                scores.add(item);
            } while (cur.moveToNext());
        }


        String[] fields = {DBHandler.TABLE_HIGHSCORE_COLUMN_DATE,
                DBHandler.TABLE_HIGHSCORE_COLUMN_SCORE};
        int[] viewIds = {R.id.scoreDate, R.id.scorePoints};
        ListView lvHigscores = (ListView) findViewById(R.id.highscoreList);
        lvHigscores.setAdapter(new SimpleAdapter(
                this
                , scores
                , R.layout.highscore_list_item
                , fields
                , viewIds
        ));
        dbHandler.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_highscore, menu);
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
}

