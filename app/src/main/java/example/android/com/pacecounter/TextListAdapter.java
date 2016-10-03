package example.android.com.pacecounter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TextListAdapter extends BaseAdapter {

    private Context mContext;

    private List<TextItem> mItems = new ArrayList<TextItem>();

    public TextListAdapter(Context context) {
        mContext = context;
    }

    public void addItem(TextItem it) {
        mItems.add(it);
    }

    public void setListItems(List<TextItem> lit) {
        mItems = lit;
    }

    public List<TextItem> getListItems() {
        return mItems;
    }

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isSelectable(int position) {
        try {
            return mItems.get(position).isSelectable();
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CustomTextView itemView;
        if (convertView == null) {
            itemView = new CustomTextView(mContext, mItems.get(position));
        } else {
            itemView = (CustomTextView) convertView;

            itemView.setText(0, mItems.get(position).getData(0));
            itemView.setText(1, mItems.get(position).getData(1));
            itemView.setText(2, mItems.get(position).getData(2));
        }
        return itemView;
    }

    public void clear() {
        if ((mItems != null) && (!mItems.isEmpty())) {
            mItems.clear();
        }
    }

}
