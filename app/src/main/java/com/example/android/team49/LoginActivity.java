package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.w3c.dom.Text;

import java.util.List;


public class LoginActivity extends Activity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LOGIN_ACTIVITY";
    private Button registerButton;
    private Button loginButton;
    private EditText etPassword;
    private MobileServiceClient msc;
    private List<PinAccess> results;
    private MobileServiceTable<PinAccess> pinTable;
    private SharedPreferences login_state;
    private ProgressDialog progressDialog;
    private String instanceId;

    private ViewFlipper viewFlipper;
    private TextView reloadLogin;
    private TextView reloadLogin2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Build a GoogleSignInClient with the options specified by gso.
        registerButton = findViewById(R.id.button_register);
        loginButton = findViewById(R.id.button_login);
        etPassword = findViewById(R.id.etPassword);
        instanceId = InstanceID.getInstance(this).getId();
        viewFlipper = (ViewFlipper) findViewById(R.id.login_view_flipper);
        reloadLogin = (TextView) findViewById(R.id.login_error);
        reloadLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        reloadLogin2 = (TextView) findViewById(R.id.login_error_no_account);
        reloadLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        registerButton.setOnClickListener(LoginActivity.this);
        loginButton.setOnClickListener(LoginActivity.this);

        login_state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //TODO:REMEMBER TO ADD BACK AFTER DONE TESTING
        String state = login_state.getString("user", "");
        if(!state.equalsIgnoreCase("")){
            home();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_register:
                register();
                finish();
                break;
            case R.id.button_login:
                String pin = etPassword.getText().toString();
                if (pin.equals("")) {
                    signInError();
                } else {
                    int finalPin = Integer.parseInt(pin);
                    login(finalPin, instanceId);//pin input;
                }
                break;
        }
    }

    private void login(final int pin, final String instanceId) {

        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", this);
            pinTable = msc.getTable(PinAccess.class);
            progressDialog = new ProgressDialog(this);
            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog.setMessage("Logging you in... Please wait.");
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        //System.out.println(instanceId);
                        results = pinTable.where().field("instanceId").
                                eq(instanceId).execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mAdapter.clear();
                                if (results.size() != 0 && isValidAccount(results, pin)) {
                                    home();
                                    SharedPreferences.Editor editor = login_state.edit();
                                    editor.putString("user", "logged");
                                    editor.apply();
                                    finish();
                                } else {
                                    viewFlipper.setDisplayedChild(2);
                                    signInError();
                                }
                            }
                        });
                    } catch (Exception e) {
                        viewFlipper.setDisplayedChild(1);
                        e.printStackTrace();
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
            Toast.makeText(this, "There was an error signing you in, Please Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidAccount(List<PinAccess> users, int pin){
        boolean toReturn = false;

        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getPin() == pin){
                toReturn = true;
            }
        }

        return toReturn;
    }

    private void signInError() {
        Toast.makeText(LoginActivity.this, "Pin incorrect!", Toast.LENGTH_LONG).show();
        //refresh();
        //finish();
        etPassword.setText("");
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        rootLayout.clearFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            // Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // handleSignInResult(task);
        }
    }

    private void register() {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private void home() {
        Intent logged = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(logged);
        finish();
    }

    public void setLoginState(SharedPreferences login_state, String state){
        login_state.edit().putBoolean("state", true).apply();
        this.login_state = login_state;
    }

    public SharedPreferences getLoginState(){
        return this.login_state;
    }
}
