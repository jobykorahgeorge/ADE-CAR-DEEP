package com.adobe.vehicletelemetry;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    private TextView currentTrip;
    private TextView manufacturerTextView;
    private TextView modelTextView;
    private TextView regNumberTextView;

    private TextView ratingTextView;
    private ImageView ratingImageView;

    private RelativeLayout mileageLayout;
    private RelativeLayout batteryLayout;
    private RelativeLayout speedLayout;
    private RelativeLayout loadLayout;

    private TextView bestDdTextView;
    private TextView bestMmmTextView;
    private TextView bestHhmmTextView;
    private TextView bestAmpmTextView;

    private RelativeLayout bestRpmLayout;
    private RelativeLayout bestLoadLayout;
    private RelativeLayout bestMileageLayout;

    private TextView ddTextView;
    private TextView mmmTextView;

    private TextView ddTextView1;
    private TextView mmmTextView1;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkLocationPermission()){
            SimpleDateFormat ddFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
            SimpleDateFormat mmmFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
            SimpleDateFormat yyyyFormat = new SimpleDateFormat("YYYY", Locale.ENGLISH);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.tolbar);
            TextView textView = (TextView)findViewById(getResources().getIdentifier("action_bar_title","id",getPackageName()));
            textView.setText(mmmFormat.format(new Date())+" "+ddFormat.format(new Date())+","+yyyyFormat.format(new Date()));
//        getSupportActionBar().setTitle(mmmFormat.format(new Date())+" "+ddFormat.format(new Date())+","+yyyyFormat.format(new Date()));

            initViews();
        }




    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:

                        Intent i = new Intent(this,MainActivity.class);
                        startActivity(i);

                    }

                } else {

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.title_location_permission)
                            .setMessage(R.string.text_location_permission)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            })
                            .create()
                            .show();

                }
                return;
            }

        }
    }

    private void initViews() {

        currentTrip = findViewById(R.id.home_current_trip);
        manufacturerTextView = findViewById(R.id.home_manufacturer_text);
        modelTextView = findViewById(R.id.home_model_text);
        regNumberTextView = findViewById(R.id.home_regnumber_text);

//        ratingTextView = findViewById(R.id.home_rating_text);
        ratingImageView = findViewById(R.id.home_rating_image);

        mileageLayout = findViewById(R.id.home_mileage_layout);
        batteryLayout = findViewById(R.id.home_battery_layout);
        speedLayout = findViewById(R.id.home_speed_layout);
        loadLayout = findViewById(R.id.home_load_layout);

        bestDdTextView = findViewById(R.id.home_best_dd_text);
        bestMmmTextView = findViewById(R.id.home_best_mmm_text);
        bestHhmmTextView = findViewById(R.id.home_best_hhmm_text);
        bestAmpmTextView = findViewById(R.id.home_best_ampm_text);

        bestRpmLayout = findViewById(R.id.home_best_rpm_layout);
        bestLoadLayout = findViewById(R.id.home_best_load_layout);
        bestMileageLayout = findViewById(R.id.home_best_mileage_layout);

        ddTextView = findViewById(R.id.item_alert_dd);
        mmmTextView = findViewById(R.id.item_alert_mmm);
        ddTextView1 = findViewById(R.id.item_alert_dd1);
        mmmTextView1 = findViewById(R.id.item_alert_mmm1);

        currentTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TripDetailActivity.class);
                intent.putExtra(TripDetailActivity.TRIP_TYPE, TripDetailActivity.TRIP_CURRENT);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.ENGLISH);
                intent.putExtra(TripDetailActivity.TRIP_DATE, dateFormat.format(new Date()));
                startActivity(intent);
            }
        });

        loadData();
    }

    private void loadData() {

        Date date =new Date();
        long DAY_IN_MS = 1000 * 60 * 60 * 24;

        SimpleDateFormat ddFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat mmmFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);

        ddTextView.setText(ddFormat.format(new Date()));
        mmmTextView.setText(mmmFormat.format(new Date()));
        ddTextView1.setText(ddFormat.format(new Date(date.getTime() - (3 * DAY_IN_MS))));
        mmmTextView1.setText(mmmFormat.format(new Date()));

//        manufacturerTextView.setText("Volkswagen");
//        modelTextView.setText("Polo");
//        regNumberTextView.setText("KA 05 J 1557");

//        ratingTextView.setText("Watch-out");
//        ratingTextView.setTextColor(getResources().getColor(R.color.averageRide));
        int color = ContextCompat.getColor(this, R.color.averageRide);
        ImageViewCompat.setImageTintList(ratingImageView, ColorStateList.valueOf(color));

        displayStat("Fuel Economy", R.drawable.ic_milage, "18.04", "mpg", mileageLayout);
        displayStat("Battery", R.drawable.ic_battery, "11.98", "Volts", batteryLayout);
        displayStat("Avg. RPM", R.drawable.ic_speed, "1862", "rpm", speedLayout);
        displayStat("Engine Load", R.drawable.ic_engine_load, "30.3", "%", loadLayout);

        displayStat("RPM Overshoot", R.drawable.ic_rpm_overshoot, "10", "Times", bestRpmLayout);
        displayStat("Engine Load", R.drawable.ic_engine_load, "25.8", "%", bestLoadLayout);
        displayStat("Mileage", R.drawable.ic_milage, "14.91", "km/L", bestMileageLayout);
    }

    private void displayStat(String statName, int iconResId, String statValue, String unit, RelativeLayout statLayout) {

        TextView nameText = statLayout.findViewById(R.id.stat_name_text);
        TextView valueText = statLayout.findViewById(R.id.stat_value_text);
        TextView unitText = statLayout.findViewById(R.id.stat_unit_text);
        ImageView iconView = statLayout.findViewById(R.id.stat_icon_image);

        nameText.setText(statName);
        valueText.setText(statValue+"");
        unitText.setText(unit);
        iconView.setImageResource(iconResId);

        if(unitText.getText().toString().equals("mpg")){
            valueText.setTextColor(Color.YELLOW);
        }
        if(unitText.getText().toString().equals("rpm")){
            valueText.setTextColor(Color.GREEN);
        }
        if(unitText.getText().toString().equals("%")){
            valueText.setTextColor(Color.GREEN);
        }
        if(unitText.getText().toString().equals("Volts")){
            valueText.setTextColor(Color.RED);
        }

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
