package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by venet on 23/03/2018.
 */

public class ViewAdapter extends ArrayAdapter<Ingredients> implements View.OnClickListener {

    private Context context;
    private int resource;
    private LayoutInflater inflater;
    private Ingredients ingredient;
    private int q;
    private String quantity_str;
    private MobileServiceClient msc;
    private MobileServiceTable<Ingredients> ingredientsTable;

    private static class ListViewHolder {
        private TextView name;
        private ImageButton minus;
        private TextView quantity;
        private ImageButton add;


        ListViewHolder() {
            // nothing to do here
        }
    }

    public ViewAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        resource = layoutResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = ((Activity) context).getLayoutInflater();
        ListViewHolder holder = new ListViewHolder();
        convertView = (RelativeLayout) inflater.inflate(resource, null);

        ingredient = getItem(position);
        q = ingredient.getQuantity();
        quantity_str = Integer.toString(q);


        holder.name = convertView.findViewById(R.id.tvIngredient);
        holder.name.setText(ingredient.getName());

        holder.minus = convertView.findViewById(R.id.ibMinus);
        holder.minus.setOnClickListener(this);


        holder.quantity = convertView.findViewById(R.id.tvQuantity);
        holder.quantity.setText(quantity_str);

        holder.add = convertView.findViewById(R.id.ibAdd);
        holder.add.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        ListViewHolder holder = new ListViewHolder();
        switch(v.getId()){
            case R.id.ibMinus:
                q--;
                holder.quantity.setText(quantity_str);
                ingredient.setQuantity(q);
                update();
                break;
            case R.id.ibAdd:
                q++;
                holder.quantity.setText(quantity_str);
                ingredient.setQuantity(q);
                update();
                break;
        }
    }

    private void update(){
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", context);
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);
            ingredientsTable.update(ingredient);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

