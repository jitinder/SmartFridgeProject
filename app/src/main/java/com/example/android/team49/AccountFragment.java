package com.example.android.team49;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.List;

import static com.google.android.gms.internal.zzagz.runOnUiThread;


/**
 * A {@link Fragment} that is shown when the user clicks on the "Account" button/icon on {@link HomeActivity}.
 * Allows basic account data Manipulation or Logging out.
 *
 * @author     Abdirahman Mohamed, Sidak Pasricha
 */
public class AccountFragment extends Fragment {


    private MobileServiceClient msc;
    private List<PinAccess> pinResults;
    private List<Ingredients> ingResults;
    private MobileServiceTable<PinAccess> pinTable;
    private MobileServiceTable<Ingredients> ingredientsTable;
    private PinAccess user; //For AsyncTask
    private String instanceId;

    private Button changePinButton;
    private Button resetAccountButton;
    private Button logoutButton;

    private TextView greetingText;
    private TextView pinText; //Secret Place where pin is Stored from async task

    private ViewFlipper viewFlipper;

    private SharedPreferences login_state;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        pinTable = msc.getTable(PinAccess.class);
        ingredientsTable = msc.getTable(Ingredients.class);
        greetingText = (TextView) view.findViewById(R.id.greeting_view);
        pinText = (TextView) view. findViewById(R.id.pin_view);
        changePinButton = (Button) view.findViewById(R.id.change_pin_button);
        resetAccountButton = (Button) view.findViewById(R.id.reset_account_button);
        logoutButton = (Button) view.findViewById(R.id.logout_button);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.account_view_flipper);

        login_state = PreferenceManager.getDefaultSharedPreferences(getContext());

        changePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePin();
            }
        });
        resetAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmReset();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        instanceId = InstanceID.getInstance(view.getContext()).getId();
        getUserData();
        return view;
    }

    /**
     * Shows the Dialog for Changing User's Pin
     */
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
                        pinResults.get(0).setPin(Integer.parseInt(newPin));
                        pinTable.update(pinResults.get(0));
                        Toast.makeText(getContext(), "Pin changed!", Toast.LENGTH_LONG).show();
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

    /**
     * Shows the Dialog for Resetting User's Password
     */
    private void showConfirmReset(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Reset Account");
        alert.setMessage("This will remove all your account data. Are you sure you want to do this?");
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Blank
            }
        });
        alert.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    for(Ingredients userIngs : ingResults){
                        ingredientsTable.delete(userIngs);
                        if(!ViewFragment.results.isEmpty()) {
                            ViewFragment.results.clear();
                        }
                    }
                    Toast.makeText(getContext(), "Account Reset", Toast.LENGTH_LONG).show();
                } catch (Exception e){
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));

    }

    /**
     * Logs out the user
     */
    private void logout(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Logout");
        alert.setMessage("Are you sure you want to logout?");
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Blank
            }
        });
        alert.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent out = new Intent(getContext(), LoginActivity.class);
                    login_state = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = login_state.edit();
                    editor.remove("user");
                    editor.apply();
                    startActivity(out);
                    getActivity().finish();
                } catch (Exception e){
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
    }

    /**
     * Fetches the logged in User's Data based on the InstanceID
     */
    private void getUserData(){
        try{
            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        pinResults = pinTable.where().field("instanceId").
                                eq(instanceId).execute().get();

                        ingResults = ingredientsTable.where().field("instanceId").
                                eq(instanceId).execute().get();

                        if(pinResults.size() != 0) {
                            user = pinResults.get(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewFlipper.setDisplayedChild(1);
                            }
                        });
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if(user != null) {
                        viewFlipper.setDisplayedChild(0);
                        String greetingString = "Account Settings";
                        greetingText.setText(greetingString);
                        String pin = "" + user.getPin();
                        pinText.setText(pin);
                    } else {
                        viewFlipper.setDisplayedChild(1);
                    }
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
