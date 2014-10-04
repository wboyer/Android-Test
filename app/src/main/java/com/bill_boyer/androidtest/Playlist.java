package com.bill_boyer.androidtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Playlist extends ArrayAdapter<Playlist.Item>
{
    private ArrayList<Item> mItems;

    public Playlist(Context context, int resource, List list)
    {
        super(context, resource);

        mItems = new ArrayList<Item>();

        Iterator iterator = list.iterator();
        while (iterator.hasNext())
            mItems.add(new Item(iterator.next().toString()));

        addAll(mItems);
    }

    public static class Item
    {
        private String mLabel;
        private boolean mIsPlaying;

        public Item(String label) {
            setLabel(label);
        }

        public String getLabel()
        {
            return mLabel;
        }

        public void setLabel(String label)
        {
            mLabel = label;
        }

        public boolean getIsPlaying()
        {
            return mIsPlaying;
        }

        public void setIsPlaying(boolean isPlaying)
        {
            mIsPlaying = isPlaying;
        }

        public String toString()
        {
            return mLabel;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Item item = mItems.get(position);
        TextView view = (TextView)convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            view = (TextView)inflater.inflate(R.layout.playlist_row, parent, false);

            if (item.getIsPlaying())
                view.setTextAppearance(getContext(), R.style.VideoListRowHighlighted);
        }
        else
            view.setTextAppearance(getContext(),
                    item.getIsPlaying() ? R.style.VideoListRowHighlighted : R.style.VideoListRow);

        view.setText(item.getLabel());

        return view;
    }

    public void selectItem(int position)
    {
        Item selectedItem = getItem(position);

        Iterator iterator = mItems.iterator();

        while (iterator.hasNext()) {
            Item item = (Item)iterator.next();
            item.setIsPlaying(item == selectedItem);
        }

        notifyDataSetChanged();
    }
}
