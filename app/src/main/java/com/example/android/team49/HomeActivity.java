package com.example.android.team49;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

/**
 * The Main {@link Activity} which connects various fragments together
 *
 * @authors     Abdirahman Mohamed, Sidak Pasricha
 */

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private int fragmentNumber = 1; // 0 = Data Entry | 1 = Stock | 2 = Recipes
    final DataEntryFragment dataEntryFragment = new DataEntryFragment();
    final ViewFragment viewFragment = new ViewFragment();
    final RecipesFragment recipesFragment = new RecipesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            AccountFragment accountFragment = new AccountFragment();
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()){
                    case R.id.account_action:
                    case R.id.account_string_action:
                        if(getSupportFragmentManager().findFragmentById(frameLayout.getId()) != accountFragment) {
                            fragmentTransaction.replace(frameLayout.getId(), accountFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            fragmentNumber = 4;
                        }

                        final Menu menu = bottomNavigationView.getMenu();
                        for(int i = 0; i < menu.size(); i++) {
                            menu.getItem(i).setCheckable(false);
                        }
                        break;
                }
                return true;
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                final Menu menu = bottomNavigationView.getMenu();
                for(int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setCheckable(true);
                }

                switch (item.getItemId()) {
                    case R.id.item_action:
                        if(getSupportFragmentManager().findFragmentById(frameLayout.getId()) != dataEntryFragment) {
                            fragmentTransaction.replace(frameLayout.getId(), dataEntryFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        fragmentNumber = 0;
                        break;

                    case R.id.stock_action:
                        if(getSupportFragmentManager().findFragmentById(frameLayout.getId()) != viewFragment) {
                            fragmentTransaction.replace(frameLayout.getId(), viewFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        fragmentNumber = 1;
                        break;

                    case R.id.recipes_action:
                        if(getSupportFragmentManager().findFragmentById(frameLayout.getId()) != recipesFragment) {
                            fragmentTransaction.replace(frameLayout.getId(), recipesFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        fragmentNumber = 2;
                        break;
                }
                return true;
            }
        });

        if(savedInstanceState != null){
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("CURRENT_FRAGMENT", R.id.stock_action));
        } else {
            bottomNavigationView.setSelectedItemId(R.id.stock_action);
        }

    }

    /**
     * Used to Store the Current Fragment in use before Orientation Change
     * @param outState      Data that stores the state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fragmentNumber < 4) {
            outState.putInt("CURRENT_FRAGMENT", bottomNavigationView.getSelectedItemId());
        } else {
            outState.putInt("CURRENT_FRAGMENT", R.id.account_action);
        }
    }

    /**
     * Handles changes in Configuration. In this case, helps in handling Orientation Changes.
     * Overrides {@link Activity#onConfigurationChanged(Configuration)}
     *
     * @param newConfig     The device configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        switch(fragmentNumber){
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.item_action);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.stock_action);
                break;
            case 2:
                bottomNavigationView.setSelectedItemId(R.id.recipes_action);
                break;
            case 4:
                final Menu menu = bottomNavigationView.getMenu();
                for(int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setCheckable(false);
                }
                break;
        }
    }

    /**
     * Inflate the menu, this adds items to the action bar if it is present.
     * Overrides {@link Activity#onCreateOptionsMenu(android.view.Menu)}
     *
     * @param menu      The menu being implemented in the action bar
     * @return          "true" to display selected menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar, menu);
        return true;
    }

    /**
     * Disables Back Button in this activity.
     * Overrides {@link Activity#onBackPressed()}
     */
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
