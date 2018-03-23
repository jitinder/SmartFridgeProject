package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.facebook.login.Login;

//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;


public class LoginActivity extends Activity implements View.OnClickListener{

    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LOGIN_ACTIVITY";
    //private GoogleSignInClient mGoogleSignInClient;
    private Button registerButton;
    private Button loginButton;
    private EditText etUsername;
    private EditText etPassword;
    private MobileServiceClient msc;
    private List<PinAccess> results;
    private MobileServiceTable<PinAccess> pinTable;
    private SharedPreferences login_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Build a GoogleSignInClient with the options specified by gso.
        registerButton = findViewById(R.id.button_register);
        loginButton = findViewById(R.id.button_login);
        mGoogleBtn = findViewById(R.id.button_google);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        mGoogleBtn.setOnClickListener(LoginActivity.this);
        registerButton.setOnClickListener(LoginActivity.this);
        loginButton.setOnClickListener(LoginActivity.this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        login_state = getSharedPreferences("login",MODE_PRIVATE);
        //if(login_state.getBoolean("logged",false)){
            //home();
        //}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_google:
                //signIn();
                break;
            case R.id.button_register:
                register();
                break;
            case R.id.button_login:
                String pin = etPassword.getText().toString();
                String username = etUsername.getText().toString();
                if(pin.equals("") || username.equals("")){
                    signInError();
                } else {
                    int finalPin = Integer.parseInt(pin);
                    login(finalPin, username);//pin input;
                }
                break;
        }
    }

    private void login(final int pin, final String username) {

        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", this);
            pinTable = msc.getTable(PinAccess.class);
            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
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
                                if(results.size() != 0 && results.get(0).getName().equalsIgnoreCase(username)){
                                    home();
                                    login_state.edit().putBoolean("logged",true).apply();
                                }
                                else{
                                    signInError();
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

    private void signInError() {
        Toast.makeText(LoginActivity.this, "Pin incorrect!", Toast.LENGTH_LONG).show();
        //refresh();
        //finish();
        etUsername.setText("");
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null){
            Intent logged = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(logged);
        }
        else {
            Toast.makeText(LoginActivity.this, "Google Login Error, Ensure details are correct", Toast.LENGTH_LONG).show();
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
        Intent logged = new Intent(LoginActivity.this, ViewActivity.class);
        startActivity(logged);
        finish();
    }
}
