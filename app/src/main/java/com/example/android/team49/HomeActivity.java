package com.example.android.team49;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        //disableShiftMode(bottomNavigationView);

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


        final DataEntryFragment dataEntryFragment = new DataEntryFragment();
        final ViewFragment viewFragment = new ViewFragment();
        final RecipesFragment recipesFragment = new RecipesFragment();
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
                        break;

                    case R.id.stock_action:
                        if(getSupportFragmentManager().findFragmentById(frameLayout.getId()) != viewFragment) {
                            fragmentTransaction.replace(frameLayout.getId(), viewFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        break;

                    case R.id.recipes_action:
                        if(getSupportFragmentManager().findFragmentById(frameLayout.getId()) != recipesFragment) {
                            fragmentTransaction.replace(frameLayout.getId(), recipesFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.stock_action);
    }

    // Method for disabling ShiftMode of BottomNavigationView
    @SuppressLint("RestrictedApi")
    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_bar, menu);
        return true;
    }
}
