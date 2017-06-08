package com.rdb.haxd.Presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rdb.haxd.Model.Hacker;
import com.rdb.haxd.R;

import java.util.List;

/**
 * Created by Randy Bruner on 6/7/2017.
 */

public class HackerAdapter extends ArrayAdapter<Hacker> {

    public HackerAdapter(Context context, List<Hacker> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //if not given a view, we need to make one
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_hacker, null);
        }
        //get the item at the position where we are
        Hacker hacker = getItem(position);

        //wire up our view
        TextView nameText = (TextView) convertView.findViewById(R.id.itemHacker_textView_name);
        TextView infoText = (TextView) convertView.findViewById(R.id.itemHacker_textView_info);
        TextView levelText = (TextView) convertView.findViewById(R.id.itemHacker_text_level);

        //put the text of the hero into the appropriate views
        nameText.setText(hacker.getUsername());
        infoText.setText("Replace This!");
        levelText.setText("" + hacker.getLevel());

        //return the view that you had edited
        return convertView;

    }
}
