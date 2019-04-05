package com.adobe.vehicletelemetry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.adobe.vehicletelemetry.adapter.ReportAdapter;
import com.adobe.vehicletelemetry.model.Report;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripReportsActivity extends AppCompatActivity
            implements ReportAdapter.ReportListener{

    private RecyclerView reportRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_reports);
        getSupportActionBar().setTitle("Trip Reports");

        initViews();
    }

    private void initViews() {

        reportRecyclerView = findViewById(R.id.report_recycler_view);

        ReportAdapter reportAdapter = new ReportAdapter(this, getReports());

        reportRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        reportRecyclerView.setAdapter(reportAdapter);
    }

    @Override
    public void onItemClick(int index) {
        Report report = getReports().get(index);
        Intent intent = new Intent(this, TripDetailActivity.class);
        if (index == 0) {
            intent.putExtra(TripDetailActivity.TRIP_TYPE, TripDetailActivity.TRIP_CURRENT);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.ENGLISH);
        intent.putExtra(TripDetailActivity.TRIP_DATE, dateFormat.format(report.getDate()));

        startActivity(intent);
    }

    private List<Report> getReports(){

        Date today = new Date();
        int day = 1000 * 60 * 60 * 24;

        Report r1 = new Report(true, new Date(today.getTime()), R.color.goodRide, R.drawable.map_current);
        Report r2 = new Report(false, new Date(today.getTime() - day), R.color.averageRide, R.drawable.map_1);
        Report r4 = new Report(false, new Date(today.getTime() - day), R.color.badRide, R.drawable.map_2);
        Report r6 = new Report(false, new Date(today.getTime() - day * 2), R.color.goodRide, R.drawable.map_3);

        return Arrays.asList(r1, r2, r4, r6);
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
