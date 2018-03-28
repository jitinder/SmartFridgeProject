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
        final Ingredients ingredient = getItem(position);
        final ListViewHolder holder = new ListViewHolder();
        final String name = ingredient.getName();

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(resource, parent, false);

        q = (int) ingredient.getQuantity();
        System.out.println(q+" "+ingredient.getName());
        //q =5;

        holder.name = convertView.findViewById(R.id.tvIngredient);
        holder.name.setText(ingredient.getName());

        holder.minus = convertView.findViewById(R.id.ibMinus);
        holder.minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                q--;
                holder.quantity.setText("" + q);
                update(name, q);
            }
        });

        holder.quantity = convertView.findViewById(R.id.tvQuantity);
        holder.quantity.setText("" + q);

        holder.add = convertView.findViewById(R.id.ibAdd);
        holder.add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                q++;
                holder.quantity.setText("" + q);
                update(name, q);
            }
        });

        return convertView;
    }

    private void update(final String name, final Integer quantity){
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);
            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try{
                        //System.out.println(name);
                        final List<Ingredients> results = ingredientsTable.where().field("name").eq(name).
                                        execute().get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //System.out.println(results.get(0).getName());
                                results.get(0).setQuantity(quantity);
                                ingredientsTable.update(results.get(0));
                                //Ingredients ingredient = new Ingredients(InstanceID.getInstance(context).getId(),"juice",2323, "22/10/12", 123123 );
                                //Ingredients ingredient2 = new Ingredients(InstanceID.getInstance(context).getId(),"cheese",79789, "23/11/12", 3 );
                                //ingredientsTable.insert(ingredient);
                                //ingredientsTable.insert(ingredient2);
                            }
                        });
                    } catch (final Exception e) {
                        //createAndShowDialogFromTask(e, "Error");
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            runAsyncTask(task);
        } catch (Exception e) {

        }
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}

