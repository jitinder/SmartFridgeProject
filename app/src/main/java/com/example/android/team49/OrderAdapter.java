package com.example.android.team49;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by venet on 26/03/2018.
 */

public class OrderAdapter extends ArrayAdapter<Ingredients> {

    private Context context;
    private int resource;

    private static class ListViewHolder {
        private TextView name;
        private TextView quantity;
        private ImageButton order;

        ListViewHolder() {
            // nothing to do here
        }
    }

    public OrderAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        resource = layoutResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Ingredients ingredient = getItem(position);
        final ListViewHolder holder = new ListViewHolder();

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(resource, parent, false);

        holder.name = convertView.findViewById(R.id.tvIngredient);
        holder.name.setText(ingredient.getName());

        holder.quantity = convertView.findViewById(R.id.tvQuantity);
        holder.quantity.setText(""+ingredient.getQuantity());

        holder.order = convertView.findViewById(R.id.ibOrder);
        holder.order.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = "https://www.amazon.co.uk/s/ref=nb_sb_noss_2?url=search-alias%3Daps&field-keywords="+ingredient.getName();
                System.out.println(ingredient.getName());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        return convertView;
    }
}
