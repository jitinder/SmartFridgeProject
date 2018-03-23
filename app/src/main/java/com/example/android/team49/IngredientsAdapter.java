package com.example.android.team49;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by venet on 23/03/2018.
 */

public class IngredientsAdapter extends ArrayAdapter<Ingredients> {

    private Context context;
    private int resource;
    private LayoutInflater inflater;

    private static class ListViewHolder {
        private TextView name;
        private TextView quantity;
        private ImageButton order;

        ListViewHolder() {
            // nothing to do here
        }
    }

    public IngredientsAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        resource = layoutResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        ListViewHolder holder = new ListViewHolder();
        convertView = (RelativeLayout) inflater.inflate(resource, null);
        final Ingredients ingredient = getItem(position);

        holder.name = convertView.findViewById(R.id.tvIngredient);
        holder.name.setText(ingredient.getName());

        holder.quantity = convertView.findViewById(R.id.tvQuantity);
        holder.quantity.setText(String.format(Locale.getDefault(), "%d", ingredient.getQuantity()));
        //TODO: FIX INTEGER CONVERSION

        holder.order = convertView.findViewById(R.id.ibOrder);
        holder.order.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String item = ingredient.getName();
                String url = "https://www.amazon.co.uk/s/ref=nb_sb_noss_2?url=search-alias%3Daps&field-keywords="+item;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        return convertView;
    }
}