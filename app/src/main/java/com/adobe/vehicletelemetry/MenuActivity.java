package com.adobe.vehicletelemetry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class MenuActivity extends AppCompatActivity {

    private LinearLayout homeLayout;
    private LinearLayout tripReportsLayout;
    private LinearLayout nearbyLayout;
    private LinearLayout alertsLayout;
    private LinearLayout settingsLayout;
    private LinearLayout syncLayout;
    private LinearLayout logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initView();
    }

    private void initView() {

        homeLayout = findViewById(R.id.menu_home);
        tripReportsLayout = findViewById(R.id.menu_trip_report);
        nearbyLayout = findViewById(R.id.menu_nearby);
        alertsLayout = findViewById(R.id.menu_alerts);
        settingsLayout = findViewById(R.id.menu_settings);
        syncLayout = findViewById(R.id.menu_sync);
        logoutLayout = findViewById(R.id.menu_logout);

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.menu_home:
                        Intent homeIntent = new Intent(MenuActivity.this, MainActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(homeIntent);
                        finish();
                        break;

                    case R.id.menu_trip_report:
                        Intent reportsIntent = new Intent(MenuActivity.this, TripReportsActivity.class);
                        startActivity(reportsIntent);
                        finish();
                        break;

                    case R.id.menu_nearby:
                        Intent nearByIntent = new Intent(MenuActivity.this, NearbyActivity.class);
                        startActivity(nearByIntent);
                        finish();
                        break;

                    case R.id.menu_alerts:
                        Intent alertsIntent = new Intent(MenuActivity.this, AlertsActivity.class);
                        startActivity(alertsIntent);
                        finish();
                        break;

                    case R.id.menu_settings:
                        break;

                    case R.id.menu_sync:
                        break;

                    case R.id.menu_logout:
                        break;

                }

            }
        };

        homeLayout.setOnClickListener(ocl);
        tripReportsLayout.setOnClickListener(ocl);
        nearbyLayout.setOnClickListener(ocl);
        alertsLayout.setOnClickListener(ocl);
        settingsLayout.setOnClickListener(ocl);
        syncLayout.setOnClickListener(ocl);
        logoutLayout.setOnClickListener(ocl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_close:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
