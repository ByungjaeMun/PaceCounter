package example.android.com.pacecounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

    /*Sample data to check*/
    private final String[] fake_date = {"2016.09.23", "2016.09.24", "2016.09.25", "2016.09.26", "2016.09.27"};
    private final int[] fake_walk = {200, 1000, 3900, 4890, 5000};
    private final int[] fake_distance = {150, 1300, 2800, 3500, 3200};

    BroadcastReceiver receiver;

    int countValue;
    int distanceValue;

    SharedPreferences record;
    SharedPreferences.Editor editor;


    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page2, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        adapter = new TextListAdapter(getContext());

        receiver = new SensorEventRecever();
        IntentFilter intentFilter = new IntentFilter("example.android.com.pacecounter");
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        getActivity().registerReceiver(receiver, intentFilter);

        createDB();
        makeList();
        mListView.setAdapter(adapter);

        return rootView;
    }

    private void createDB() {
        try {
            sampleDB = getActivity().openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
                    + " (date text not null, walk INTEGER, distance INTEGER );");

            sampleDB.execSQL("DELETE FROM " + tableName);

            for (int i = 0; i < fake_date.length; i++) {
                sampleDB.execSQL("INSERT INTO " + tableName
                        + " (date, walk, distance) Values ( '" + fake_date[i] + "', '" + fake_walk[i] + "', '" + fake_distance[i] + "' );");
                Log.v("HistoryFragment", fake_date[i] + "added");
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

    private void updateDB(String date, int walk, int distance) {
        SQLiteDatabase readDB = getActivity().openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        readDB.execSQL("INSERT INTO " + tableName
                + " (date, walk, distance) Values ( '" + date + "', '" + walk + "', '" + distance + "' );");
    }


    class SensorEventRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("example.android.com.pacecounter")) {
                Log.v("HistoryFragment", "receive sensor event intent");
                countValue = intent.getIntExtra("count", 0);
                distanceValue = intent.getIntExtra("distance", 0);

            } else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
//                record = getActivity().getSharedPreferences("record", 0);
//                editor = record.edit();
//                String current_date = record.getString("current_date", "");
//                updateDB(current_date, countValue, distanceValue);
            }
        }
    }


}

