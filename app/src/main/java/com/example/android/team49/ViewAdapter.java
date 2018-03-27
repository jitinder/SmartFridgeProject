package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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

import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

/**
 * Created by venet on 23/03/2018.
 */

public class ViewAdapter extends ArrayAdapter<Ingredients> {

    private Context context;
    private int resource;
    private LayoutInflater inflater;
    private Integer q;
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
        final Ingredients ingredient = getItem(position);
        final ListViewHolder holder = new ListViewHolder();

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(resource, parent, false);

        q = (Integer) ingredient.getQuantity();

        holder.name = convertView.findViewById(R.id.tvIngredient);
        holder.name.setText(ingredient.getName());

        holder.minus = convertView.findViewById(R.id.ibMinus);
        holder.minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                q--;
                holder.quantity.setText(Integer.toString(q));
                updateItem(ingredient, q);
            }
        });

        holder.quantity = convertView.findViewById(R.id.tvQuantity);
        holder.quantity.setText(Integer.toString(q));

        holder.add = convertView.findViewById(R.id.ibAdd);
        holder.add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                q++;
                holder.quantity.setText(Integer.toString(q));
                updateItem(ingredient, q);
            }
        });

        return convertView;
    }

    private void updateItem(final Ingredients ingredient, final Integer quantity) {
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", context);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);
        ingredient.setQuantity(quantity);
        ingredientsTable.update(ingredient);
    }
}

