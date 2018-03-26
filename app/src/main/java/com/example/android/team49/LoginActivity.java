package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;


public class LoginActivity extends Activity implements View.OnClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Build a GoogleSignInClient with the options specified by gso.
        registerButton = findViewById(R.id.button_register);
        loginButton = findViewById(R.id.button_login);
        etPassword = findViewById(R.id.etPassword);
        instanceId = InstanceID.getInstance(this).getId();

        registerButton.setOnClickListener(LoginActivity.this);
        loginButton.setOnClickListener(LoginActivity.this);

        login_state = getSharedPreferences("login",MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_register:
                register();
                break;
            case R.id.button_login:
                String pin = etPassword.getText().toString();
                if(pin.equals("")){
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

                    try{
                        results = pinTable.where().field("instanceId").
                                eq(instanceId).execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mAdapter.clear();
                                if(results.size() != 0 && results.get(0).getPin().equals(pin)){
                                    home();
                                    login_state.edit().putBoolean("logged",true).apply();
                                }
                                else{
                                    signInError();
                                }
                            }
                        });
                    } catch (final Exception e) {
                        Toast.makeText(LoginActivity.this, "There was an error Signing you in, Please make sure you have registered.", Toast.LENGTH_SHORT).show();
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

    private void register(){
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

    private void refresh(){
        Intent refresh = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(refresh);
        finish();
    }

    private void home(){
        Intent logged = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(logged);
        finish();
    }
}
