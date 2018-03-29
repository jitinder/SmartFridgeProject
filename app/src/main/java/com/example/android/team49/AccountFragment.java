package com.example.android.team49;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    private MobileServiceClient msc;
    private List<PinAccess> results;
    private MobileServiceTable<PinAccess> pinTable;
    private PinAccess user; //For AsyncTask
    private String instanceId;

    private Button changePinButton;
    private Button myRecipesButton;
    private Button resetAccountButton;

    private TextView greetingText;
    private TextView pinText; //Secret Place where pin is Stored from async task


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        greetingText = (TextView) view.findViewById(R.id.greeting_view);
        pinText = (TextView) view. findViewById(R.id.pin_view);
        changePinButton = (Button) view.findViewById(R.id.change_pin_button);
        myRecipesButton = (Button) view.findViewById(R.id.my_recipes_button);
        resetAccountButton = (Button) view.findViewById(R.id.reset_account_button);

        changePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePin();
            }
        });

        instanceId = InstanceID.getInstance(view.getContext()).getId();
        getUserData();

        return view;
    }

    private void showChangePin(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.change_pin_layout, null);
        final EditText oldPinText = alertLayout.findViewById(R.id.etOldPassword);
        final EditText newPinText = alertLayout.findViewById(R.id.etNewPassword);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Set New Pin");
        alert.setView(alertLayout);
        alert.setCancelable(false); //Can not be canceled by outside click
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Blank
            }
        });

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String oldPin = oldPinText.getText().toString();
                    String newPin = newPinText.getText().toString();
                    String currentPin = pinText.getText().toString();

                    if(oldPin.equals(currentPin)){
                        // Change Pin using newPin @Venet
                    }
                } catch (Exception e){
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
    }

    private void getUserData(){
        try{
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
            pinTable = msc.getTable(PinAccess.class);
            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        results = pinTable.where().field("instanceId").
                                eq(instanceId).execute().get();

                        if(results.size() != 0) {
                            user = results.get(0);
                            Log.d("Lol", "getUserData: " +user.getName());
                        } else {
                            user = new PinAccess("name", 0, "lol");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    String greetingString = "Account Settings for " + user.getName();
                    greetingText.setText(greetingString);
                    String pin = "" +user.getPin();
                    pinText.setText(pin);
                }
            };
            runAsyncTask(task);
        } catch (Exception e){
            e.printStackTrace();
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
