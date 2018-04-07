package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ViewFlipper;

import com.google.android.gms.iid.InstanceID;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private MobileServiceClient mClient;
    private EditText etName;
    private EditText etPin;
    private EditText etConfirmPin;
    private Button btRegister;
    private TextView tvRegistered;
    private TextView reloadRegistration;
    private ViewFlipper viewFlipper;
    private Intent login;
    private MobileServiceClient msc;
    private List<PinAccess> results;
    private ProgressDialog progressDialog;
    private MobileServiceTable<PinAccess> pinTable;
    private String instanceId;

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
        instanceId = InstanceID.getInstance(this).getId();
        viewFlipper = (ViewFlipper) findViewById(R.id.registration_view_flipper);
        reloadRegistration = (TextView) findViewById(R.id.registration_error);
        reloadRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

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
                } else {
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

    public void register(final String name, final int pin, final int confirm) {
        final PinAccess p = new PinAccess(name, pin, instanceId);


        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", this);
            pinTable = msc.getTable(PinAccess.class);
            progressDialog = new ProgressDialog(this);

            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog.setMessage("Registering your account... Please wait.");
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    try{
                        results = pinTable.where().field("instanceId").
                                eq(instanceId).execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(results.size() == 0){

                                    if(pin == confirm){
                                        pinTable.insert(p);
                                        Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_LONG).show();
                                        Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(login);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Ensure pin codes match", Toast.LENGTH_LONG)
                                                .show();
                                        registerError();
                                    }

                                } else if(canCreateNewAccount(results, name, pin) && pin == confirm){
                                    pinTable.insert(p);
                                    Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_LONG).show();
                                    Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(login);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Pin or Name already taken on this Device. Please choose Another Value", Toast.LENGTH_LONG).show();
                                    registerError();
                                }
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewFlipper.setDisplayedChild(1);
                            }
                        });
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            };
            runAsyncTask(task);

        } catch (Exception e) {
            Toast.makeText(RegisterActivity.this, "There was an error registering your account, Please Try Again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean canCreateNewAccount(List<PinAccess> users, String name, int pin){
        boolean toReturn = true;

        for(int i = 0; i < users.size(); i++){
            //Check InstanceID
            if(users.get(i).getInstanceId().equalsIgnoreCase(instanceId)) {
                //Check all Names
                if (users.get(i).getName().equalsIgnoreCase(name)) {
                    toReturn = false;
                }
                //Check all Pins
                if (users.get(i).getPin() == pin) {
                    toReturn = false;
                }
            }
        }

        return toReturn;
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private void registerError() {
        etName.setText("");
        etPin.setText("");
        etConfirmPin.setText("");
        LinearLayout rootLayout = (LinearLayout) this.findViewById(R.id.root_layout);
        rootLayout.clearFocus();
    }
}
