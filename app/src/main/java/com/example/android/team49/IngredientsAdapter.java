package com.example.android.team49;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by venet on 23/03/2018.
 */

public class IngredientsAdapter extends ArrayAdapter<Ingredients> {
    private ListView listView;
    private Context mContext;
    private int mLayoutResourceId;

    private static class ListViewHolder {
        private TextView ingredient;
        private TextView quantity;
        private ImageButton order;

        ListViewHolder() {
            // nothing to do here
        }
    }

    public IngredientsAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final Ingredients currentItem = getItem(position);
        final ListViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ListViewHolder();
            holder.ingredient = (TextView) row.findViewById(R.id.tvIngredient);
        }

        row.setTag(currentItem);



        return row;
    }
}