package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private MobileServiceClient mClient;
    private EditText etName;
    private EditText etPin;
    private EditText etConfirmPin;
    private Button btRegister;
    private TextView tvRegistered;
    private Intent login;
    private MobileServiceClient msc;
    private List<PinAccess> results;
    private MobileServiceTable<PinAccess> pinTable;
    private MobileServiceTable<Ingredients> ingredientsTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etPin = findViewById(R.id.etPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);
        btRegister = findViewById(R.id.btRegR);
        btRegister.setOnClickListener(RegisterActivity.this);
        tvRegistered = findViewById(R.id.tvRegistered);
        tvRegistered.setOnClickListener(RegisterActivity.this);

        //AzureServiceAdapter.Initialize(RegisterActivity.this);

        /*try {
            mClient = new MobileServiceClient(
                    "https://smartfridgeteam49.azurewebsites.net",
                    this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        detailsTable = mClient.getTable(Details.class);*/

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btRegR:
                String name = etName.getText().toString();
                String pin = etPin.getText().toString();
                String confirm = etConfirmPin.getText().toString();
                if( name.equals("") || pin.equals("") || confirm.equals("")){
                    Toast.makeText(RegisterActivity.this, "Ensure all fields are filled in", Toast.LENGTH_LONG).show();
                    registerError();
                }
                else{
                    register(name, Integer.parseInt(pin), Integer.parseInt(confirm));
                }
                break;
            case R.id.tvRegistered:
                login = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
                break;
        }

    }

    public void register(final String name, final Integer pin, final Integer confirm) {
        final PinAccess p = new PinAccess(name, pin);
        final Ingredients bread = new Ingredients("bread", "22/10/15", 5);


        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", this);
            pinTable = msc.getTable(PinAccess.class);


            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try{
                        results = pinTable.where().field("pin").
                                eq(pin).execute().get();

                        //Offline Sync
                        //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mAdapter.clear();

                                if(results.size() == 0){

                                    if(pin.equals(confirm)) {
                                        pinTable.insert(p);
                                        Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_LONG).show();
                                        Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(login);
                                        finish();
                                    }

                                    else{
                                        Toast.makeText(RegisterActivity.this, "Ensure pin codes match", Toast.LENGTH_LONG)
                                                .show();
                                        registerError();
                                    }

                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, "Pin Taken!", Toast.LENGTH_LONG).show();
                                    registerError();
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

        /*if(password.equals(confirm)) {
            try {
                detailsTable.insert(user);
            } catch (Exception e) {

            }
            login = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(login);
        }
        else{
            Toast.makeText(this, "Please check passwords match", Toast.LENGTH_SHORT).show();
            Intent reload = new Intent(RegisterActivity.this, RegisterActivity.class);
            startActivity(reload);
        }*/

        //Robs code

    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private void registerError() {
        //refresh();
        //finish();
        etName.setText("");
        etPin.setText("");
        etConfirmPin.setText("");
        LinearLayout rootLayout = (LinearLayout) this.findViewById(R.id.root_layout);
        rootLayout.clearFocus();
    }
}
