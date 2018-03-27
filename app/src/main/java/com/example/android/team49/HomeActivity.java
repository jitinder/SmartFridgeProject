package com.example.android.team49;

import android.annotation.SuppressLint;
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

        disableShiftMode(bottomNavigationView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_action:
                        //ChooseMethodFragment chooseMethodFragment = new ChooseMethodFragment();
                        DataEntryFragment dataEntryFragment = new DataEntryFragment();

                        FragmentTransaction itemTransaction = getSupportFragmentManager().beginTransaction();
                        itemTransaction.add(R.id.frame_layout, dataEntryFragment);
                        itemTransaction.commit();
                        break;

                    case R.id.reorder_action:
                        break;

                    case R.id.stock_action:
                        ViewFragment viewFragment = new ViewFragment();

                        FragmentTransaction stockTransaction = getSupportFragmentManager().beginTransaction();
                        stockTransaction.add(R.id.frame_layout, viewFragment);
                        stockTransaction.commit();
                        break;

                    case R.id.recipes_action:
                        break;

                    case R.id.account_action:
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

    private void logOut(){
    }
}
