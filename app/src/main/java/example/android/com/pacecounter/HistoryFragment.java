package example.android.com.pacecounter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import static android.content.Context.MODE_PRIVATE;
import static example.android.com.pacecounter.R.id.container;

/**
 * Created by mbj94 on 2016-10-02.
 */
public class HistoryFragment extends android.support.v4.app.Fragment {

    protected final String dbName = "pacecounter";
    protected final String tableName = "history";
    protected SQLiteDatabase sampleDB;

    private ListView mListView;
    private TextListAdapter adapter;


    private final String[] fake_date = {"2016.09.23", "2016.09.24", "2016.09.25", "2016.09.26", "2016.09.27"};
    private final int[] fake_walk = {200, 1000, 3900, 4890, 5000};
    private final int[] fake_distance = {150, 1300, 2800, 3500, 3200};

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page2, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        adapter = new TextListAdapter(getContext());

        creteDB();
        makeList();
        mListView.setAdapter(adapter);

        return rootView;
    }

    private void creteDB() {
        try {
            sampleDB = getActivity().openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
                    + " (date text not null, walk INTEGER, distance INTEGER );");

            sampleDB.execSQL("DELETE FROM " + tableName);

            for (int i = 0; i < fake_date.length; i++) {
                sampleDB.execSQL("INSERT INTO " + tableName
                        + " (date, walk, distance) Values ( '" + fake_date[i] + "', '" + fake_walk[i] + "', '" + fake_distance[i] + "' );");
                Log.v("ffff", fake_date[i] + "added");
            }

            sampleDB.close();
        } catch (SQLiteException e) {

        }
    }

    private void makeList() {
        SQLiteDatabase readDB = getActivity().openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        Cursor c = readDB.rawQuery("SELECT * FROM " + tableName, null);

        if (c.moveToFirst()) {

            do {
                String date = c.getString(c.getColumnIndex("date"));
                int walk = c.getInt(c.getColumnIndex("walk"));
                int distance = c.getInt(c.getColumnIndex("distance"));

                String s_distance = Utils.transToKM(distance);

                adapter.addItem(new TextItem(date, String.valueOf(walk), String.valueOf(s_distance)));
                Log.v("ffff", date + "added to item");
            } while (c.moveToNext());
        }
        c.close();
        readDB.close();
    }
}

