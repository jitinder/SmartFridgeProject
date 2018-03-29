package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private MobileServiceClient msc;
    private MobileServiceTable<Ingredients> ingredientsTable;

    private static class ListViewHolder {
        private TextView name;
        private TextView minus;
        private TextView quantity;
        private TextView expiryDate;
        private TextView add;
        private Button orderButton;


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
        final String expiryDate = "Expiry Date: " + ingredient.getExpDate();
        final

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(resource, parent, false);

        //quantity_str = Integer.toString(q);

        holder.name = convertView.findViewById(R.id.tvIngredient);
        holder.name.setText(name);

        holder.expiryDate = convertView.findViewById(R.id.tvExpiryDate);
        holder.expiryDate.setText(expiryDate);

        holder.minus = convertView.findViewById(R.id.bMinus);
        holder.minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.quantity.setText(""+(ingredient.getQuantity()-1));
                update(ingredient, ingredient.getQuantity()-1);
            }
        });

        holder.quantity = convertView.findViewById(R.id.tvQuantity);
        holder.quantity.setText(""+ingredient.getQuantity());

        holder.add = convertView.findViewById(R.id.bPlus);
        holder.add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.quantity.setText(""+(ingredient.getQuantity()+1));
                update(ingredient, ingredient.getQuantity()+1);
            }
        });

        holder.orderButton = convertView.findViewById(R.id.order_button);
        holder.orderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.amazon.co.uk/s?url=search-alias%3Daps&field-keywords="+ingredient.getName().replace(" ", "+");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        return convertView;
    }

    private void update(final Ingredients ingredient, final int quantity){
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);
            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try{
                        //System.out.println(name);
                        final List<Ingredients> results = ingredientsTable.where().field("name").eq(ingredient.getName()).
                                        execute().get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //System.out.println(results.get(0).getName());
                                //results.get(0).setQuantity(quantity);
                                ingredient.setQuantity(quantity);
                                ingredientsTable.update(ingredient);
                                //ingredientsTable.update(results.get(0));

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

