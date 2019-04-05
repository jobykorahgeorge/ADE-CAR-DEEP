package com.adobe.vehicletelemetry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.adobe.vehicletelemetry.adapter.AlertAdapter;
import com.adobe.vehicletelemetry.model.Alert;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {

    private RecyclerView alertRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        getSupportActionBar().setTitle("Car Alerts");

        initViews();
    }

    private void initViews() {

        alertRecyclerView = findViewById(R.id.alert_recycler_view);

        AlertAdapter statsAdapter = new AlertAdapter(getAlerts());

        alertRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        alertRecyclerView.setAdapter(statsAdapter);
    }

    private List<Alert> getAlerts(){
        Date today = new Date();
        int day = 1000 * 60 * 60 * 24;

        Alert a10 = new Alert(new Date(today.getTime()), "Your car will be due for servicing in the next 325km or 32 days whichever comes first.");
        Alert a11 = new Alert(new Date(today.getTime() - day), "Register with PSS motors - Authorized Service Center and get a free engine check and replacement.");
        Alert a1 = new Alert(new Date(today.getTime() - day * 2), "Rash acceleration detected at 12:23:00. Steady acceleration can save you money by reducing the wear and tear on your car.");
        Alert a12 = new Alert(new Date(today.getTime() - day * 3), "Your car is low on fuel. Refuel within the next 10km.");
        Alert a13 = new Alert(new Date(today.getTime() - day * 3), "2 Fuel stations found within the next 2km.");
        Alert a6 = new Alert(new Date(today.getTime() - day * 6), "Wasted fuel due to idling detected at 00:47:00. Prolonged engine idling leads to fuel wastage.");
        Alert a7 = new Alert(new Date(today.getTime() - day * 7), "Fuel-inefficient driving detected at 20:40:00. Release accelerator when approaching a coasting zone. Follow green speed limits.");

        return Arrays.asList(a10, a11, a1, a12, a13, a6, a7);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
