package com.example.android.team49;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.internal.zzagz.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    private ListView lv;
    private ArrayAdapter<Ingredients> iAdapter;
    private MobileServiceClient msc;
    private List<Ingredients> results;
    private MobileServiceTable<Ingredients> ingredientsTable;
    public String id;

    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        id = InstanceID.getInstance(getContext()).getId();
        System.out.println(id);
        getIngredients(id);

        iAdapter = new ViewAdapter(getContext(), R.layout.row_list_view);
        lv = (ListView) view.findViewById(R.id.lvIngredients);
        lv.setAdapter(iAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    private void getIngredients(final String instanceId){
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);

            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try{
                        results = ingredientsTable.where().field("instanceId").eq(instanceId)
                                .select("name").
                                        execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(results.size() != 0){
                                    iAdapter.addAll(results);
                                }
                                else{
                                    Toast.makeText(getContext(), "add some ingredients!", Toast.LENGTH_LONG).show();
                                }

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
