package example.android.com.pacecounter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomTextView extends LinearLayout {

    public TextView mText_date;
    public TextView mText_pace;
    public TextView mText_distance;

    public boolean mDiff;
    private int originColor;

    public CustomTextView(Context context, TextItem aItem) {
        super(context);

        // Layout Inflation
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.my_simple_list, this, true);

        mText_date = (TextView) findViewById(R.id.text_date);
        mText_pace = (TextView) findViewById(R.id._text_total_pace);
        mText_distance = (TextView) findViewById(R.id.text_total_distance);

        mText_date.setText(aItem.getData(0));
        mText_pace.setText(aItem.getData(1));
        mText_distance.setText(aItem.getData(2));
    }

    /**
     * set Text
     *
     * @param index
     * @param data
     */


    public void setText(int index, String data) {
        if (index == 0) {
            mText_date.setText(data);
        } else if (index == 1) {
            mText_pace.setText(data);
        } else if (index == 2) {
            mText_distance.setText(data);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * set Icon
     *
     * @param icon
     */

}
