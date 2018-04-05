package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        String expiryDate = "";
        Date currentDate = new Date();
        Date itemExpiryDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            itemExpiryDate = format.parse(ingredient.getExpDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(resource, parent, false);

        //quantity_str = Integer.toString(q);

        holder.name = convertView.findViewById(R.id.tvIngredient);
        holder.name.setText(name);

        holder.expiryDate = convertView.findViewById(R.id.tvExpiryDate);
        if(itemExpiryDate.before(currentDate)){
            expiryDate = "Expired: " + ingredient.getExpDate();
            holder.expiryDate.setTextColor(convertView.getResources().getColor(R.color.red));
        } else {
            expiryDate = "Expires: " + ingredient.getExpDate();
        }
        holder.expiryDate.setText(expiryDate);

        holder.minus = convertView.findViewById(R.id.bMinus);
        holder.minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ingredient.getQuantity() - 1 == 0) {
                    AlertDialog.Builder reminder = new AlertDialog.Builder(getContext());
                    reminder.setTitle("Remember to buy");
                    reminder.setMessage("Would you like us to remind you to buy " + ingredient.getName());
                    reminder.setCancelable(false);
                    reminder.setNeutralButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    reminder.setPositiveButton("Yes, remind me tomorrow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                scheduleNotification(getNotification("Reminder",
                                        "remember to buy " + ingredient.getName()), 86400000);
                            } catch (Exception e) {
                                dialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    });
                    reminder.setNegativeButton("Yes, remind me in an hour", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                scheduleNotification(getNotification("Reminder",
                                        "remember to buy " + ingredient.getName()), 3600000);
                            } catch (Exception e) {
                                dialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    });
                    AlertDialog dialog = reminder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.red));
                }

                if (ingredient.getQuantity() - 1 == -1) {
                    AlertDialog.Builder delete = new AlertDialog.Builder(getContext());
                    delete.setTitle("Delete item");
                    delete.setMessage("Are you sure you want to delete " + ingredient.getName());
                    delete.setCancelable(false);

                    delete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                remove(ingredient);
                                notifyDataSetChanged();
                                ingredientsTable.delete(ingredient);
                            } catch (Exception e) {
                                dialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    });
                    delete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                holder.quantity.setText("" + 0);
                                update(ingredient, 0);
                            } catch (Exception e) {
                                dialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    });
                    AlertDialog dialog = delete.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.red));
                }

                if(ingredient.getQuantity() - 1 >= 0) {
                    holder.quantity.setText("" + (ingredient.getQuantity() - 1));
                    update(ingredient, ingredient.getQuantity() - 1);
                }
            }
        });

        holder.quantity = convertView.findViewById(R.id.tvQuantity);
        holder.quantity.setText("" + ingredient.getQuantity());

        holder.add = convertView.findViewById(R.id.bPlus);
        holder.add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.quantity.setText("" + (ingredient.getQuantity() + 1));
                update(ingredient, ingredient.getQuantity() + 1);
            }
        });

        holder.orderButton = convertView.findViewById(R.id.order_button);
        holder.orderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.amazon.co.uk/s?url=search-alias%3Daps&field-keywords=" + ingredient.getName().replace(" ", "+");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        return convertView;
    }


    private void update(final Ingredients ingredient, final int quantity) {
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);
            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        //System.out.println(name);
                        final List<Ingredients> results = ingredientsTable.where().field("name").eq(ingredient.getName()).
                                execute().get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ingredient.setQuantity(quantity);
                                ingredientsTable.update(ingredient);
                            }
                        });
                    } catch (final Exception e) {
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

    private Notification getNotification(String content, String title) {
        Notification.Builder builder = new Notification.Builder(getContext());
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.fruit_icon_black);
        return builder.build();
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(getContext(), Notifications.class);
        notificationIntent.putExtra(Notifications.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(Notifications.NOTIFICATION, notification);
        PendingIntent pending = PendingIntent.getBroadcast(getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pending);
    }
}


