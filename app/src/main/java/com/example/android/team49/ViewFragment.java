package com.example.android.team49;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.internal.zzagz.runOnUiThread;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.or;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    private ListView lv;
    private ArrayAdapter<Ingredients> iAdapter;
    private MobileServiceClient msc;
    public static List<Ingredients> results;
    private MobileServiceTable<Ingredients> ingredientsTable;
    public String id;
    private ProgressDialog progressDialog;
    private FloatingActionButton fab;
    private ArrayList<Ingredients> toSort;

    private ViewFlipper viewFlipper;
    private TextView reload;

    final private int ASCENDING = 1;
    final private int DESCENDING = 2;

    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        id = InstanceID.getInstance(getContext()).getId();

        iAdapter = new ViewAdapter(getContext(), R.layout.row_list_view);
        lv = (ListView) view.findViewById(R.id.lvIngredients);
        lv.setAdapter(iAdapter);
        getIngredients(id);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortData();
            }
        });

        reload = (TextView) view.findViewById(R.id.ingredient_reload_list);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIngredients(id);
            }
        });
        viewFlipper = (ViewFlipper) view.findViewById(R.id.ingredient_view_flipper);


        return view;
    }

    private void sortData(){
        toSort = new ArrayList<>();
        for(int i = 0; i < iAdapter.getCount(); i++){
            toSort.add(iAdapter.getItem(i));
        }
        CharSequence[] sortOptions = new CharSequence[]{"By Name (a - z)",
                "By Name (z - a)", "Nearest Expiry Date", "Furthest Expiry Date",
                "Lowest Quantity", "Highest Quantity"};
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setCancelable(false);
        alert.setTitle("Choose Sorting Option");
        alert.setItems(sortOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: //Ascending by Name
                        Collections.sort(toSort, Ingredients.compareNameAscending);
                    break;
                    case 1: //Descending by Name
                        Collections.sort(toSort, Ingredients.compareNameDescending);
                    break;
                    case 2: //Ascending by Date
                        toSort = sortByDate(toSort, ASCENDING);
                    break;
                    case 3: //Descending by Date
                        toSort = sortByDate(toSort, DESCENDING);
                    break;
                    case 4: //Ascending by Quantity
                        Collections.sort(toSort, Ingredients.compareQuantityAscending);
                    break;
                    case 5: //Descending by Quantity
                        Collections.sort(toSort, Ingredients.compareQuantityDescending);
                    break;
                }
                iAdapter.clear();
                iAdapter.addAll(toSort);
                iAdapter.notifyDataSetChanged();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Empty
            }
        });
        alert.show();
    }

    @SuppressLint("SimpleDateFormat")
    private ArrayList<Ingredients> sortByDate(ArrayList<Ingredients> arrayList, int mode){
        ArrayList<Ingredients> sorted = new ArrayList<>();
        ArrayList<int[]> dates = new ArrayList<>();
        int[] order = new int[arrayList.size()];
        int[] max = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        int[] min = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};

        for(int i = 0; i < arrayList.size(); i++){
            String[] sdate = arrayList.get(i).getExpDate().replace(" ", "").split("/");
            int[] date = new int[3];
            date[0] = Integer.parseInt(sdate[0]);
            date[1] = Integer.parseInt(sdate[1]);
            date[2] = Integer.parseInt(sdate[2]);
            dates.add(date);
        }

        for(int j = 0; j < order.length; j++) {
            int checking = 0;
            for (int i = 0; i < dates.size(); i++) {
                int[] toCheck = dates.get(checking);
                if (i != checking) {
                    int[] current = dates.get(i);
                    if (compareDates(toCheck, current, mode) == 2) {
                        checking = i;
                    }
                }
            }
            order[j] = checking;
            if(mode == ASCENDING) {
                dates.set(checking, max);
            } else {
                dates.set(checking, min);
            }
        }

        for(int i = 0; i < order.length; i++) {
            sorted.add(arrayList.get(order[i]));
        }

        for(int j = 0; j < sorted.size(); j++) {
            System.out.println(sorted.get(j).toString());
        }

        return sorted;
    }

    private int compareDates(int[] a, int[] b, int mode){
        int toReturn = 0; // Both Equal
        //1 = a best, 2 = b best

        if(a[2] < b[2]){
            toReturn = 1;
        } else if(a[2] > b[2]){
            toReturn = 2;
        } else {
            if(a[1] < b[1]){
                toReturn = 1;
            } else if(a[1] > b[1]){
                toReturn = 2;
            } else {
                if(a[0] <= b[0]){
                    toReturn = 1;
                } else if(a[0] > b[0]){
                    toReturn = 2;
                }
            }
        }

        if(mode == DESCENDING){
            if(toReturn == 1){
                toReturn = 2;
            } else {
                toReturn = 1;
            }
        }

        return toReturn;
    }

    private void getIngredients(final String instanceId){
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);
            progressDialog = new ProgressDialog(getContext());

            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog.setMessage("Fetching your Inventory... Please wait.");
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    try{
                        results = ingredientsTable.where().field("instanceId").eq(instanceId)
                                .execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(results.size() != 0){
                                    viewFlipper.setDisplayedChild(0);
                                    iAdapter.addAll(results);
                                }
                                else{
                                    viewFlipper.setDisplayedChild(2);
                                }

                            }
                        });
                    } catch (final Exception e) {
                        e.printStackTrace();
                        viewFlipper.setDisplayedChild(1);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
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
