package com.adobe.vehicletelemetry;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adobe.vehicletelemetry.fragments.EngineFragment;
import com.adobe.vehicletelemetry.fragments.FuelFragment;
import com.adobe.vehicletelemetry.model.Points;
import com.adobe.vehicletelemetry.model.Wheel;
import com.adobe.vehicletelemetry.network.VolleyHandler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripDetailActivity extends AppCompatActivity implements VolleyHandler.NetworkListener, FuelFragment.FuelListener {

    public static final String TRIP_TYPE = "TRIP_TYPE";
    public static final String TRIP_DATE = "TRIP_DATE";
    public static final int TRIP_CURRENT = 100;

    private MapView mapView;
    private GoogleMap googleMap;

    private ImageView fuelTabImage;
    private ImageView engineTabImage;

    private EngineFragment engineFragment;
    private FuelFragment fuelFragment;


    private VolleyHandler mVolleyHandler;

    private FragmentTransaction fragmentTransaction;
    private int i=0;

    private int mapLoadInterval = 0;
    private double goodDist = 0;
    private double avgDist = 0;
    private double badDist = 0;
    private double fuel = 0;
    private int time = 0;

    private View badView;
    private View avgView;
    private View goodView;

    private View badView1;
    private View avgView1;
    private View goodView1;

    LinearLayout.LayoutParams params;
    private boolean isFirstLoad = true;

    int newVal = 0;
    int statVal = 0;
    double seconds = 0;
    int delta = 0;
    double redCount = 0;
    double greenCount = 0;
    double yellowCount = 0;

    private Preferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        preferences = new Preferences(this);

        SimpleDateFormat ddFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat mmmFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        SimpleDateFormat yyyyFormat = new SimpleDateFormat("YYYY", Locale.ENGLISH);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tolbar);
        TextView textView = (TextView)findViewById(getResources().getIdentifier("action_bar_title","id",getPackageName()));
        textView.setText(mmmFormat.format(new Date())+" "+ddFormat.format(new Date())+","+yyyyFormat.format(new Date()));

        mVolleyHandler = VolleyHandler.getInstance(this);

        getSupportActionBar().setTitle(getIntent().getStringExtra(getIntent().getStringExtra(TRIP_DATE)));

        initViews(savedInstanceState);
    }

    private void initViews(Bundle savedInstanceState) {

        mapView = findViewById(R.id.trip_detail_map);
        mapView.onCreate(savedInstanceState);

        fuelTabImage = findViewById(R.id.trip_detail_fuel);
        engineTabImage = findViewById(R.id.trip_detail_engine);

        engineFragment = EngineFragment.newInstance(null, null);
        fuelFragment = FuelFragment.newInstance(null, null);

        badView = findViewById(R.id.meter_bad_range);
        avgView = findViewById(R.id.meter_avg_range);
        goodView = findViewById(R.id.meter_good_range);

        badView1 = findViewById(R.id.meter_bad_range1);
        avgView1 = findViewById(R.id.meter_avg_range1);
        goodView1 = findViewById(R.id.meter_good_range1);


        final int colorDark = ContextCompat.getColor(TripDetailActivity.this, R.color.colorAccent);
        final int colorWhite = ContextCompat.getColor(TripDetailActivity.this, android.R.color.white);

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.trip_detail_fuel:
                        fuelTabImage.setBackgroundResource(R.drawable.tab_highlight_left_background);
                        engineTabImage.setBackgroundResource(android.R.color.transparent);

                        ImageViewCompat.setImageTintList(fuelTabImage, ColorStateList.valueOf(colorDark));
                        ImageViewCompat.setImageTintList(engineTabImage, ColorStateList.valueOf(colorWhite));

                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.replace(R.id.trip_detail_fragment_container, fuelFragment);
                        fragmentTransaction.commit();
                        break;

                    case R.id.trip_detail_engine:
                        fuelTabImage.setBackgroundResource(android.R.color.transparent);
                        engineTabImage.setBackgroundResource(R.drawable.tab_highlight_right_background);

                        ImageViewCompat.setImageTintList(fuelTabImage, ColorStateList.valueOf(colorWhite));
                        ImageViewCompat.setImageTintList(engineTabImage, ColorStateList.valueOf(colorDark));

                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.replace(R.id.trip_detail_fragment_container, engineFragment);
                        fragmentTransaction.commit();

//                        final Handler handler = new Handler();
                        engineTabImage.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                engineFragment.loadHistory();
                            }
                        }, 1000);
                        break;

                }

                int padding = getResources().getDimensionPixelSize(R.dimen.tab1_padding);
                fuelTabImage.setPadding(padding, padding, padding, padding);
                engineTabImage.setPadding(padding, padding, padding, padding);
            }
        };

        fuelTabImage.setOnClickListener(ocl);
        engineTabImage.setOnClickListener(ocl);

        //loadMapView();
        engineTabImage.performClick();
        fuelTabImage.performClick();

        if (getIntent().getIntExtra(TRIP_TYPE, 0) != TRIP_CURRENT) {
            engineTabImage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    engineFragment.loadHistory();
                }
            }, 1000);
        }
    }

    private void fetchWheelData() {

        if (getIntent().getIntExtra(TRIP_TYPE, 0) == TRIP_CURRENT) {

            mapLoadInterval = 2000;

            mVolleyHandler.makeRequestArray("wheel_data", this, VolleyHandler.GET,
                    "https://api.mlab.com/api/1/databases/demo/collections/iot?apiKey=Yy5PdPkKAZbEEhGiQPYNpa5HoGbK5GbU&l=1&s={timestamp:-1}&region=USA", null);
        } else {
            loadMapView();
            fuelFragment.loadHistory();
        }
    }

    private void loadMapView() {
        if (mapView != null) {
            mapView.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(final GoogleMap gMap) {
                    googleMap = gMap;
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                    if (ActivityCompat.checkSelfPermission(TripDetailActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(TripDetailActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }

                    googleMap.setMyLocationEnabled(false);
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = locationManager.getBestProvider(criteria, true);
                    Location location = locationManager.getLastKnownLocation(provider);


                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng coordinate = new LatLng(latitude, longitude);
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 13);
                        googleMap.moveCamera(yourLocation);
                    }

                    final List<Points> routeList = getDummyRoute();
                    for(i=0; i<routeList.size()-1; i++){
                        final Points point1 = routeList.get(i);
                        final Points point2 = routeList.get(i+1);
                        final int counter = i;

                        mapView.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                LatLng from = new LatLng(point1.getLat(), point1.getLng());
                                LatLng to = new LatLng(point2.getLat(), point2.getLng());

                                double milage = 0;
                                time += (int) (point1.getDistance() / 50 * 60); //50km/h

                                int color = getResources().getColor(R.color.colorAccent);
                                if(counter%3 == 0){
                                    badDist += point1.getDistance();
                                    color = getResources().getColor(R.color.badRide);
                                    milage = 17.89;
                                }
                                else if(counter%2 == 0){
                                    avgDist += point1.getDistance();
                                    color = getResources().getColor(R.color.averageRide);
                                    milage = 18.45;
                                }
                                else{
                                    goodDist += point1.getDistance();
                                    color = getResources().getColor(R.color.goodRide);
                                    milage = 19.32;
                                }

                                double totalDist = badDist + avgDist + goodDist;
                                fuel += point1.getDistance() / milage;

                                Log.d("METERz", "badDist:" + badDist + " avgDist:" + avgDist + " goodDist:" + goodDist + " totalDist:" + totalDist);

                                params = (LinearLayout.LayoutParams) badView.getLayoutParams();
                                params.weight = (float) (badDist / totalDist * 100);
                                badView.setLayoutParams(params);

                                params = (LinearLayout.LayoutParams) avgView.getLayoutParams();
                                params.weight = (float) (avgDist / totalDist * 100);
                                avgView.setLayoutParams(params);

                                params = (LinearLayout.LayoutParams) goodView.getLayoutParams();
                                params.weight = (float) (goodDist / totalDist * 100);
                                goodView.setLayoutParams(params);


                                Log.d("WEIGHTS","bad#:"+(badDist / totalDist * 100)
                                        + " avg#:" +(avgDist / totalDist * 100)
                                        + " good#:" +( goodDist / totalDist * 100));

                                /*Log.d("WEIGHTS","bad:"+((LinearLayout.LayoutParams) badView.getLayoutParams()).weight
                                        + " avg:" +((LinearLayout.LayoutParams) avgView.getLayoutParams()).weight
                                        + " good:" +((LinearLayout.LayoutParams) goodView.getLayoutParams()).weight);*/

                                params = (LinearLayout.LayoutParams) goodView.getLayoutParams();

                                LatLng coordinate = new LatLng(point2.getLat(), point2.getLng());

                                if (getIntent().getIntExtra(TRIP_TYPE, 0) == TRIP_CURRENT) {
                                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 13);
                                    googleMap.moveCamera(yourLocation);
                                }

                                CircleOptions circleOptions = new CircleOptions();
                                circleOptions.center(to);
                                circleOptions.radius(getResources().getDimension(R.dimen.route_width));
                                circleOptions.strokeColor(getApplicationContext().getResources().getColor(R.color.blue_map));
                                circleOptions.fillColor(getApplicationContext().getResources().getColor(R.color.blue_map));
                                circleOptions.zIndex(10);
                                circleOptions.strokeWidth(getResources().getDimension(R.dimen.route_width));
                                googleMap.addCircle(circleOptions);

                                googleMap.addPolyline(new PolylineOptions()
                                        .add(from, to)
                                        .jointType(JointType.BEVEL)
                                        .geodesic(true)
                                        .width(getResources().getDimension(R.dimen.route_width))
                                        .color(getApplicationContext().getResources().getColor(R.color.blue_map)));

                                engineFragment.showStats(point1.getRpm(), point1.getSpeed(), point1.getRpmOvershoot(), point1.getLoad(), point1.getCoolantTemp());


                                /*double acceleration = 0;
                                Log.d("ACC", "wheelData.size(): "+wheelData.size() + ", counter:"+counter);
                                if(wheelData.size() > counter) {
                                    acceleration = wheelData.get(counter).getAcc();
                                }*/
//                                fuelFragment.updateStats(totalDist, milage, fuel, time);



                            }
                        }, mapLoadInterval * i);
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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

    @Override
    public void onRequest(String requestId) {

    }

    @Override
    public void onResponse(String requestId, JSONObject joResponse) {
        try {

            if(requestId.equals("*#1234")) {
                JSONArray routeArray = joResponse.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes
                        .getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                List<LatLng> list = decodePoly(encodedString);

                PolylineOptions options = new PolylineOptions()
                        .width(getResources().getDimension(R.dimen.route_width))
                        .color(Color.BLUE)
                        .geodesic(true)
                        .zIndex(9);
                for (int z = 0; z < list.size(); z++) {
                    LatLng point = list.get(z);
                    options.add(point);
                }

                googleMap.addPolyline(options);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int idleCount = 0;
    long latestTimestamp = 0;
    @Override
    public void onResponse(String requestId, JSONArray jaResponse) {
        try {
            if (requestId.equals("wheel_data")) {

                Wheel wheel = null;

                for (int i = 0; i < jaResponse.length(); i++) {
                    int acc = jaResponse.getJSONObject(i).getInt("acceleration");
                    int brake = jaResponse.getJSONObject(i).getInt("brake");
                    long timestamp = jaResponse.getJSONObject(i).getLong("timestamp");

                    if (timestamp > latestTimestamp) {
                        wheel = new Wheel(acc, brake, timestamp);
                        latestTimestamp = timestamp;
                        idleCount = 0;
                    } else if (timestamp == latestTimestamp) {
                        idleCount++;
                        if (idleCount == 3) {
                            wheel = new Wheel(0, brake, timestamp);
                        }
                    }

                    if (isFirstLoad) {
                        isFirstLoad = false;
                        loadMapView();
                    }

                    if (wheel != null) {
                        fuelFragment.updateWheelStats(wheel);
                        //jkg codes
                        showlayout(wheel);
                        //nd
                    }
                }

                mapView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TEST", "Fetching wheel data after idle.");
                        fetchWheelData();
                    }
                }, 500);

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showlayout(Wheel wheel) {

        seconds = seconds+1;
        newVal = wheel.getAcc();

//        preferences.setData(Preferences.ACC_DATA,Integer.toString(newVal));
//        preferences.setData(Preferences.ACC_SECONDS,Integer.toString(seconds));

        delta = newVal - statVal;

        if(delta<0)
            delta = delta * (-1);

        if(delta>=50){
           redCount = redCount+1;
        }
        if(delta>=25 && delta<50){
            yellowCount = yellowCount+1;
        }
        if(delta<25){
            greenCount = greenCount+1;
        }

        params = (LinearLayout.LayoutParams) badView1.getLayoutParams();
        params.weight = (float) (redCount/seconds * 100);
        badView1.setLayoutParams(params);

//        Log.d("param red",(redCount/seconds * 100));


        params = (LinearLayout.LayoutParams) avgView.getLayoutParams();
        params.weight = (float) (yellowCount/seconds * 100);
        avgView1.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) goodView1.getLayoutParams();
        params.weight = (float) (greenCount/seconds * 100);

//        Log.d("s",);
        goodView1.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) goodView1.getLayoutParams();

        statVal = newVal;

        Log.d("newss",redCount + "red " +yellowCount +"yellow" + greenCount + "green" + seconds +"sec");


        fuelFragment.updateStats(redCount+yellowCount+greenCount, 18, fuel, seconds,(double)wheel.getAcc());

//        double totalDist = badDist + avgDist + goodDist;
//
//        Log.d("METERz", "badDist:" + badDist + " avgDist:" + avgDist + " goodDist:" + goodDist + " totalDist:" + totalDist);
//
//        params = (LinearLayout.LayoutParams) badView.getLayoutParams();
//        params.weight = (float) (badDist / totalDist * 100);
//        badView.setLayoutParams(params);
//
//        params = (LinearLayout.LayoutParams) avgView.getLayoutParams();
//        params.weight = (float) (avgDist / totalDist * 100);
//        avgView.setLayoutParams(params);
//
//        params = (LinearLayout.LayoutParams) goodView.getLayoutParams();
//        params.weight = (float) (goodDist / totalDist * 100);
//        goodView.setLayoutParams(params);
    }


    @Override
    public void onError(String requestId, JSONObject joError) {

    }

    @Override
    public void onNoInternetConnection(String requestId) {

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    private List<Points> getDummyRoute(){

        Points r0 = new Points(  -115.17051,  36.12118,     0.32,      20,     13,                  47, 20,    0 );
        Points r1 = new Points(  -115.17124,  36.12219,     0.81,      80,     13,                  47, 30.59, 0 );
        Points r2 = new Points(  -115.17061,  36.12333,     0.44,      100,    13,                  47, 30.59, 0 );
        Points r3 = new Points(  -115.17003,  36.1256,      1.52,      15,     13,                  48, 30.59, 0 );
        Points r4 = new Points(  -115.17814,  36.12617,     0.89,      60,     13,                  48, 30.59, 0 );
        Points r5 = new Points(  -115.18076,  36.12332,     0.97,      88,     12.8333333333333,    48, 35.69, 0 );
        Points r6 = new Points(  -115.18085,  36.0957,      0.83,      44,     12.7142857142857,    48, 35.69, 0 );
        Points r7 = new Points(  -115.18263,  36.0662,      1.29,      50,     12.625,              48, 35.69, 0 );
        Points r8 = new Points(  -115.18076,  36.06135,     0.77,      60,     12.5555555555556,    48, 35.69, 1 );
        Points r9 = new Points(  -115.17369,  36.06333,     1.6,       70,     12.6,                48, 30.2,  2 );
        Points r10 = new Points( -115.14963,  36.06266,     1.5,       80,     12.6363636363636,    48, 30.2,  3 );
        Points r11 = new Points( -115.14094,  36.05788,     1.76,      100,    12.6666666666667,    48, 30.2,  4 );
        Points r12 = new Points( -115.13356,  36.0367,      1.82,      90,     12.6923076923077,    49, 30.2,  4 );
        Points r13 = new Points( -115.12383,  36.02408,     1.31,      95,     12.3571428571429,    49, 21.18, 4 );
        Points r14 = new Points( -115.06281,  36.02732,     1.07,      50,     12.0666666666667,    49, 21.18, 4 );
        Points r15 = new Points( -115.02938,  36.03184,     1.13,      40,     11.8125,             49, 21.18, 4 );
        Points r16 = new Points( -115.01608,  36.03317,     1.11,      55,     11.5882352941176,    49, 21.18, 4 );
        Points r17 = new Points( -115.01148,  36.0306,      1.06,      70,     11.3888888888889,    49, 19.22, 4 );
        Points r18 = new Points( -114.9885,   36.00916,     1.23,      10,     11.2105263157895,    49, 19.22, 4 );
        Points r19 = new Points( -114.96424,  35.9999,      0.95,      30,     11.05,               49, 19.22, 4 );
        Points r20 = new Points( -114.93232,  35.99452,     1.02,      40,     10.952380952381,     49, 19.22, 4 );
        Points r21 = new Points( -114.92457,  35.9882,      1.14,      50,     10.8636363636364,    49, 56.47, 4 );
        Points r22 = new Points( -114.91565,  35.97706,     0.9,       60,     10.7826086956522,    49, 56.47, 4 );
        Points r23 = new Points( -114.90948,  35.95753,     1.19,      70,     10.7083333333333,    50, 56.47, 4 );
        Points r24 = new Points( -114.90433,  35.9532,      1.57,      100,    11.16,               50, 23.53, 4 );
        Points r25 = new Points( -114.86682,  35.93614,     0.73,      55,     11.5769230769231,    50, 23.53, 4 );
        Points r26 = new Points( -114.862724, 35.934858,    1.17,      66,     11.962962962963,     50, 23.53, 4 );
        Points r27 = new Points( -114.850178, 35.933730,    1.15,      77,     12.3214285714286,    50, 23.53, 4 );
        Points r28 = new Points( -114.818687, 35.934850,    0.96,      45,     12.448275862069,     50, 21.18, 4 );
        Points r29 = new Points( -114.814728, 35.936631,    0.97,      45,     12.5666666666667,    50, 21.18, 4 );
        Points r30 = new Points( -114.812496, 35.938273,    0.68,      45,     12.6774193548387,    50, 21.18, 4 );
        Points r31 = new Points( -114.794161, 35.971737,    1.06,      45,     12.78125,            50, 21.18, 4 );
        Points r32 = new Points( -114.792409, 35.975813,    0.63,      45,     12.6060606060606,    50, 21.18, 4 );
        Points r33 = new Points( -114.792188, 35.978111,    0.39,      80,     12.4411764705882,    50, 17.25, 4 );
        Points r34 = new Points( -114.793008, 35.981671,    0.54,      80,     12.2857142857143,    50, 17.25, 4 );
        Points r35 = new Points( -114.799611, 35.992803,    0.84,      85,     12.1388888888889,    51, 17.25, 4 );
        Points r36 = new Points( -114.798331, 35.998259,    0.32,      60,     12.1351351351351,    51, 17.25, 4 );
        Points r37 = new Points( -114.795292, 36.001771,    0.85,      70,     12.1315789473684,    51, 38.04, 4 );
        Points r38 = new Points( -114.793017, 36.002786,    0.97,      75,     12.1282051282051,    51, 38.04, 4 );
        Points r39 = new Points( -114.791204, 36.003098,    0.7,       100,    12.125,              51, 38.04, 4 );
        Points r40 = new Points( -114.786655, 36.003098,    1.38,      95,     12.1951219512195,    51, 27.06, 5 );
        Points r41 = new Points( -114.784101, 36.003758,    1.05,      80,     12.2619047619048,    51, 27.06, 6 );
        Points r42 = new Points( -114.781402, 36.005872,    1.19,      70,     12.3255813953488,    51, 27.06, 7 );
        Points r43 = new Points( -114.779336, 36.008863,    1.28,      60,     12.3863636363636,    52, 27.06, 8 );
        Points r44 = new Points( -114.777920, 36.010000,    0.87,      50,     12.4,                52, 27.06, 9 );
        Points r45 = new Points( -114.775624, 36.011050,    1.22,      40,     12.4130434782609,    52, 40.39, 10);
        Points r46 = new Points( -114.773307, 36.011189,    0.93,      35,     12.4255319148936,    52, 40.39, 11);
        Points r47 = new Points( -114.770407, 36.011068,    1.14,      30,     12.4375,             52, 40.39, 12);
        Points r48 = new Points( -114.768186, 36.010287,    0.92,      25,     12.4285714285714,    52, 40.39, 12);
        Points r49 = new Points( -114.766877, 36.009931,    1.21,      20,     12.42,               52, 34.51, 12);
        Points r50 = new Points( -114.764238, 36.009890,    0.6,       15,     12.4117647058824,    52, 34.51, 12);
        Points r51 = new Points( -114.758044, 36.013563,    1.12,      10,     12.4038461538462,    53, 34.51, 12);
        Points r52 = new Points( -114.753061, 36.015174,    0.83,      0,      12.3962264150943,    53, 34.51, 12);
        Points r53 = new Points( -114.747634, 36.014943,    0.89,      0,      12.2962962962963,    53, 21.96, 12);
        Points r54 = new Points( -114.739052, 36.011296,    0.41,      20,     12.2,                53, 21.96, 12);
        Points r55 = new Points( -114.735399, 36.010045,    1.02,      25,     12.1071428571429,    53, 21.96, 12);

        Points r56 = new Points( -112.138,    35.66286,     0.63,      35,     12.0175438596491,    53, 21.96, 12);
        Points r57 = new Points( -112.13339,  35.69667,     0.89,      47,     11.9310344827586,    53, 21.96, 12);
        Points r58 = new Points( -112.13107,  35.73649,     1.3,       65,     11.8474576271186,    53, 21.18, 12);
        Points r59 = new Points( -112.12965,  35.78509,     1.25,      88,     11.7666666666667,    53, 21.18, 12);
        Points r60 = new Points( -112.13209,  35.82218,     1.11,      99.9,   11.6885245901639,    53, 21.18, 12);
        Points r61 = new Points( -112.13331,  35.86232,     0.85,      59,     11.6129032258065,    54, 21.18, 12);
        Points r62 = new Points( -112.13085,  35.93632,     1.09,      63,     11.6507936507937,    54, 62.35, 12);
        Points r63 = new Points( -112.12876,  35.96787,     0.73,      67,     11.6875,             54, 62.35, 12);
        Points r64 = new Points( -112.12477,  35.97678,     1.84,      69,     11.7230769230769,    54, 62.35, 12);
        Points r65 = new Points( -112.1214,   36.0009,      1.48,      70,     11.7575757575758,    54, 62.35, 12);
        Points r66 = new Points( -112.12303,  36.03122,     1.38,      30,     11.9850746268657,    54, 62.35, 13);
        Points r67 = new Points( -112.12544,  36.04283,     0.96,      25,     12.2058823529412,    54, 62.45, 14);
        Points r68 = new Points( -112.12249,  36.05193,     1,         15,     12.4202898550725,    54, 62.55, 15);
        Points r69 = new Points( -112.11769,  36.06544,     0.98,      20,     12.6285714285714,    54, 62.75, 15);


        Points r70 = new Points( -112.11769,  36.06544,     1,         30,     12.6285714285714,    54, 62.35, 15);
        Points r71 = new Points( -112.11769,  36.06544,     1.05,      40,     13.6285714285714,    57, 62.35, 15);
        Points r72 = new Points( -112.11769,  36.06544,     1.09,      50,     13.6285714285714,    58, 62.45, 15);
        Points r73 = new Points( -112.11769,  36.06544,     1.25,      55,     13.7285714285714,    58, 63.35, 17);
        Points r74 = new Points( -112.11769,  36.06544,     1.09,      60,     14.8285714285714,    58, 64.25, 17);
        Points r75 = new Points( -112.11769,  36.06544,     1.48,      65,     14.6285714285714,    58, 64.25, 17);
        Points r76 = new Points( -112.11769,  36.06544,     0.85,      70,     14.7285714285714,    58, 64.35, 17);
        Points r77 = new Points( -112.11769,  36.06544,     1.3,       75,     14.8285714285714,    59, 63.25, 17);
        Points r78 = new Points( -112.11769,  36.06544,     1.38,      80,     14.8285714285714,    59, 65.25, 17);
        Points r79 = new Points( -112.11769,  36.06544,     1.09,      85,     15.3285714285714,    60, 65.25, 17);
        Points r80 = new Points( -112.11769,  36.06544,     0.87,      90,     15.6285714285714,    60, 63.25, 17);
        Points r81 = new Points( -112.11769,  36.06544,     1,         30,     15.6285714285714,    60, 63.35, 18);
        Points r82 = new Points( -112.11769,  36.06544,     1.09,      50,     15.7285714285714,    61, 62.45, 18);
        Points r83 = new Points( -112.11769,  36.06544,     1.25,      55,     16.7185714285714,    61, 63.35, 18);
        Points r84 = new Points( -112.11769,  36.06544,     1.09,      60,     16.8285714285714,    61, 64.25, 18);
        Points r85 = new Points( -112.11769,  36.06544,     1.48,      65,     16.9285714285714,    61, 64.25, 18);
        Points r86 = new Points( -112.11769,  36.06544,     0.85,      70,     17.7285714285714,    62, 64.35, 18);
        Points r87 = new Points( -112.11769,  36.06544,     1.3,       75,     17.8285714285714,    63, 63.25, 18);
        Points r88 = new Points( -112.11769,  36.06544,     1.38,      80,     17.8285714285714,    63, 65.25, 18);
        Points r89 = new Points( -112.11769,  36.06544,     1.09,      85,     17.9285714285714,    63, 65.25, 18);
        Points r90 = new Points( -112.11769,  36.06544,     0.87,      90,     17.9185714285714,    63, 63.25, 18);
        Points r91 = new Points( -112.11769,  36.06544,     1.05,      40,     18.6285714285714,    63, 62.35, 18);
        Points r92 = new Points( -112.11769,  36.06544,     1.09,      50,     18.6485714285714,    63, 62.45, 18);
        Points r93 = new Points( -112.11769,  36.06544,     1.25,      55,     18.6585714285714,    63, 63.35, 18);
        Points r94 = new Points( -112.11769,  36.06544,     1.09,      60,     18.6685714285714,    64, 64.25, 18);
        Points r95 = new Points( -112.11769,  36.06544,     1.48,      65,     18.6785714285714,    64, 64.25, 18);
        Points r96 = new Points( -112.11769,  36.06544,     0.85,      70,     18.7785714285714,    64, 64.35, 18);
        Points r97 = new Points( -112.11769,  36.06544,     1.3,       75,     19.8485714285714,    64, 63.25, 18);
        Points r98 = new Points( -112.11769,  36.06544,     1.38,      80,     19.8485714285714,    65, 65.25, 18);
        Points r99 = new Points( -112.11769,  36.06544,     1.09,      85,     19.8585714285714,    65, 65.25, 19);
        Points r100 = new Points(-112.11769,  36.06544,     0.87,      90,     19.8685714285714,    65, 63.25, 19);
        Points r101 = new Points(-112.11769,  36.06544,     1,         30,     19.8785714285714,    65, 63.35, 19);
        Points r102 = new Points(-112.11769,  36.06544,     1.09,      50,     19.8885714285714,    66, 62.45, 19);
        Points r103 = new Points(-112.11769,  36.06544,     1.25,      55,     19.8985714285714,    66, 63.35, 19);
        Points r104 = new Points(-112.11769,  36.06544,     1.09,      60,     19.9185714285714,    66, 64.25, 19);
        Points r105 = new Points(-112.11769,  36.06544,     1.48,      65,     20.1285714285714,    66, 64.25, 19);
        Points r106 = new Points(-112.11769,  36.06544,     0.85,      70,     20.5285714285714,    66, 64.35, 19);
        Points r107 = new Points(-112.11769,  36.06544,     1.3,       75,     20.8285714285714,    67, 63.25, 20);
        Points r108 = new Points(-112.11769,  36.06544,     1.38,      80,     20.8385714285714,    67, 65.25, 20);
        Points r109 = new Points(-112.11769,  36.06544,     1.09,      85,     20.9285714285714,    67, 65.25, 20);
        Points r110 = new Points(-112.11769,  36.06544,     0.87,      90,     19.8685714285714,    67, 63.25, 20);
        Points r111 = new Points(-112.11769,  36.06544,     1,         30,     19.8785714285714,    68, 63.35, 20);
        Points r112 = new Points(-112.11769,  36.06544,     1.09,      50,     19.8885714285714,    68, 62.45, 20);
        Points r113 = new Points(-112.11769,  36.06544,     1.25,      55,     19.8985714285714,    68, 63.35, 20);
        Points r114 = new Points(-112.11769,  36.06544,     1.09,      60,     19.9185714285714,    68, 64.25, 21);
        Points r115 = new Points(-112.11769,  36.06544,     1.48,      65,     20.1285714285714,    68, 64.25, 21);
        Points r116 = new Points(-112.11769,  36.06544,     0.85,      70,     20.5285714285714,    67, 64.35, 21);
        Points r117 = new Points(-112.11769,  36.06544,     1.3,       75,     20.8285714285714,    67, 63.25, 21);
        Points r118 = new Points(-112.11769,  36.06544,     1.38,      80,     20.8385714285714,    67, 65.25, 21);
        Points r119 = new Points(-112.11769,  36.06544,     1.09,      85,     20.9285714285714,    67, 65.25, 21);
        Points r120 = new Points(-112.11769,  36.06544,     0,         95,     20.9385714285714,    67, 65.25, 21);

        return Arrays.asList(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9,
                r10, r11, r12, r13, r14, r15, r16, r17, r18, r19,
                r20, r21, r22, r23, r24, r25, r26, r27, r28, r29,
                r30, r31, r32, r33, r34, r35, r36, r37, r38, r39,
                r40, r41, r42, r43, r44, r45, r46, r47, r48, r49,
                r50, r51, r52, r53, r54, r55, r56, r57, r58, r59,
                r60, r61, r62, r63, r64, r65, r66, r67, r68, r69,
                r70, r71, r72, r73, r74, r75, r76, r77, r78, r79,
                r80, r81, r82, r83, r84, r85, r86, r87, r88, r89,
                r90, r91, r92, r93, r94 ,r95, r96 ,r97, r98 ,r99,
                r100, r101, r102, r103, r104 ,r105 ,r106 ,r107, r108,
                r109, r110, r111, r112, r113, r114, r115, r116, r117,
                r118,r119,r120);
    }

    /*@Override
    public void onWheelDataConsumed() {
        Log.d("TEST", "Fetching wheel data after existing data consumed.");
        fetchWheelData();
    }*/

    @Override
    public void onFuelInitialized() {
        Log.d("TEST", "Fetching wheel data on initialization.");
        fetchWheelData();
    }
}
