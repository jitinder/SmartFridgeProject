package com.example.android.team49;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayAdapter<Ingredients> iAdapter;
    private MobileServiceClient msc;
    private List<Ingredients> results;
    private MobileServiceTable<Ingredients> ingredientsTable;
    private String instanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        getIngredients(instanceId);

        iAdapter = new OrderAdapter(this, R.layout.row_list_order);
        lv = (ListView) findViewById(R.id.lvIngredients);
        lv.setAdapter(iAdapter);
    }

    public void setId(String instanceId){
        this.instanceId = instanceId;
    }

    private void getIngredients(final String instanceId){
        try {

            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", this);
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);
            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try{
                        results = ingredientsTable.where().field("instanceId").eq(instanceId)
                                .select("name").orderBy("quantity", QueryOrder.Ascending).
                                        execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(results.size() != 0){
                                    iAdapter.addAll(results);
                                }
                                else{
                                    Toast.makeText(OrderActivity.this, "add some ingredients!", Toast.LENGTH_LONG).show();
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
