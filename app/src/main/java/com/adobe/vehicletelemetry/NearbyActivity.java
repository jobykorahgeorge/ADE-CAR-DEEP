package com.adobe.vehicletelemetry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adobe.vehicletelemetry.adapter.NearbyAdapter;
import com.adobe.vehicletelemetry.model.Nearby;
import com.adobe.vehicletelemetry.network.VolleyHandler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearbyActivity extends AppCompatActivity implements VolleyHandler.NetworkListener {

    private LinearLayout petrolLayout;
    private LinearLayout entertainmentLayout;
    private LinearLayout restaurantLayout;
    private LinearLayout serviceCenterLayout;

    private TextView petrolText;
    private TextView entertainmentText;
    private TextView restaurantText;
    private TextView serviceCenterText;

    private ImageView petrolImage;
    private ImageView entertainmentImage;
    private ImageView restaurantImage;
    private ImageView serviceCenterImage;

    private MapView mapView;
    private GoogleMap googleMap;

    private RecyclerView nearbyRecyclerView;
    private NearbyAdapter nearbyAdapter;
    private VolleyHandler mVolleyHandler;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        getSupportActionBar().setTitle("Nearby Services");

        if (ActivityCompat.checkSelfPermission(NearbyActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(NearbyActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);

        mVolleyHandler = VolleyHandler.getInstance(this);

        initViews(savedInstanceState);
    }

    private void initViews(Bundle savedInstanceState) {
        petrolLayout = findViewById(R.id.nearby_category_petrol);
        entertainmentLayout = findViewById(R.id.nearby_category_entertainment);
        restaurantLayout = findViewById(R.id.nearby_category_restaurants);
        serviceCenterLayout = findViewById(R.id.nearby_category_service_center);

        petrolText = findViewById(R.id.nearby_category_petrol_text);
        entertainmentText = findViewById(R.id.nearby_category_entertainment_text);
        restaurantText = findViewById(R.id.nearby_category_restaurants_text);
        serviceCenterText = findViewById(R.id.nearby_category_service_center_text);

        petrolImage = findViewById(R.id.nearby_category_petrol_img);
        entertainmentImage = findViewById(R.id.nearby_category_entertainment_img);
        restaurantImage = findViewById(R.id.nearby_category_restaurants_img);
        serviceCenterImage = findViewById(R.id.nearby_category_service_center_img);

        mapView = findViewById(R.id.nearby_map);
        mapView.onCreate(savedInstanceState);

        nearbyRecyclerView = findViewById(R.id.nearby_recycler_view);
        nearbyAdapter = new NearbyAdapter();
        nearbyRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        final int colorDark = ContextCompat.getColor(this, R.color.colorAccent);
        final int colorWhite = ContextCompat.getColor(this, android.R.color.white);

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                petrolText.setTextColor(colorWhite);
                entertainmentText.setTextColor(colorWhite);
                restaurantText.setTextColor(colorWhite);
                serviceCenterText.setTextColor(colorWhite);

                ImageViewCompat.setImageTintList(petrolImage, ColorStateList.valueOf(colorWhite));
                ImageViewCompat.setImageTintList(entertainmentImage, ColorStateList.valueOf(colorWhite));
                ImageViewCompat.setImageTintList(restaurantImage, ColorStateList.valueOf(colorWhite));
                ImageViewCompat.setImageTintList(serviceCenterImage, ColorStateList.valueOf(colorWhite));

                petrolLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                entertainmentLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                restaurantLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                serviceCenterLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                switch (view.getId()){

                    case R.id.nearby_category_service_center:
                        performSearch(Nearby.TYPE_SERVICE_CENTER);

                        serviceCenterText.setTextColor(colorDark);
                        ImageViewCompat.setImageTintList(serviceCenterImage, ColorStateList.valueOf(colorDark));
                        serviceCenterLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_highlight_left_background));
                        break;

                    case R.id.nearby_category_petrol:
                        performSearch(Nearby.TYPE_PETROL_PUMP);

                        petrolText.setTextColor(colorDark);
                        ImageViewCompat.setImageTintList(petrolImage, ColorStateList.valueOf(colorDark));
                        petrolLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_highlight_center_background));
                        break;

                    case R.id.nearby_category_entertainment:
                        performSearch(Nearby.TYPE_ENTERTAINMENT);

                        entertainmentText.setTextColor(colorDark);
                        ImageViewCompat.setImageTintList(entertainmentImage, ColorStateList.valueOf(colorDark));
                        entertainmentLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_highlight_center_background));
                        break;

                    case R.id.nearby_category_restaurants:
                        performSearch(Nearby.TYPE_RESTAURANT);

                        restaurantText.setTextColor(colorDark);
                        ImageViewCompat.setImageTintList(restaurantImage, ColorStateList.valueOf(colorDark));
                        restaurantLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_highlight_right_background));
                        break;
                }
            }
        };

        petrolLayout.setOnClickListener(ocl);
        entertainmentLayout.setOnClickListener(ocl);
        restaurantLayout.setOnClickListener(ocl);
        serviceCenterLayout.setOnClickListener(ocl);

        loadMapView();

        serviceCenterLayout.performClick();
    }

    private void loadMapView() {
        if (mapView != null) {
            mapView.getMapAsync(new OnMapReadyCallback() {

                @SuppressLint("MissingPermission")
                @Override
                public void onMapReady(GoogleMap gmap) {
                    googleMap = gmap;

                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    googleMap.setMyLocationEnabled(true);

                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng coordinate = new LatLng(latitude, longitude);
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 13);
                        googleMap.moveCamera(yourLocation);
                    }
                }
            });
        }
    }

    private void loadMapMarkers(ArrayList<Nearby> nearbyList, int type) {

        googleMap.clear();

        int iconResId;
        if (type == Nearby.TYPE_PETROL_PUMP) {
            iconResId = R.drawable.ic_petrol_station;
        } else if (type == Nearby.TYPE_ENTERTAINMENT) {
            iconResId = R.drawable.ic_entertainment;
        } else if (type == Nearby.TYPE_RESTAURANT) {
            iconResId = R.drawable.ic_restaurants;
        } else {
            iconResId = R.drawable.ic_service_center;
        }

        for (Nearby nearby : nearbyList) {

            googleMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(iconResId))
                    .anchor(0.0f, 1.0f)
                    .position(new LatLng(nearby.getLatitude(), nearby.getLongitude())));
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


    private void performSearch(int nearbyType) {

        if(location == null){
            location = new Location("");
            location.setLatitude(19.077421);
            location.setLongitude(72.8491243);
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        int radius_meters = 5000;

        String type = "";
        if (nearbyType == Nearby.TYPE_SERVICE_CENTER) {
            type = "car_repair";
        } else if (nearbyType == Nearby.TYPE_PETROL_PUMP) {
            type = "gas_station";
        } else if (nearbyType == Nearby.TYPE_ENTERTAINMENT) {
            type = "movie_theater";
        } else if (nearbyType == Nearby.TYPE_RESTAURANT) {
            type = "restaurant";
        }

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(radius_meters);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getString(R.string.google_android_map_api_key));

        Log.d("NearbyActivity", "Search googlePlacesUrl: " + googlePlacesUrl);
        mVolleyHandler.makeRequest(nearbyType + "", this, VolleyHandler.GET, googlePlacesUrl.toString(), null);


        mVolleyHandler.makeRequest("*#1234", this, VolleyHandler.GET,
                "https://maps.googleapis.com/maps/api/directions/json?origin=19.077421,72.8491243&destination=28.5203403,76.9647963&sensor=false&mode=driving", null);
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

    @Override   // VolleyHandler.NetworkListener
    public void onRequest(String requestId) {

    }

    @Override   // VolleyHandler.NetworkListener
    public void onResponse(String requestId, JSONObject joResponse) {

        if(requestId.equals("*#1234")){

            try {
                JSONArray jaSample = joResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

                for (int i = 0; i < jaSample.length(); i++) {
                    Log.d("LCOATIONS", "Points r"+i+" = new Points( " + jaSample.getJSONObject(i).getJSONObject("start_location").getDouble("lat") + ", " +
                            jaSample.getJSONObject(i).getJSONObject("start_location").getDouble("lng") + ", " + jaSample.getJSONObject(i).getJSONObject("distance").getInt("value") + " );");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            ArrayList<Nearby> nearbyList = parseResponse(requestId, joResponse);

            nearbyAdapter.setNearbyList(nearbyList);
            nearbyRecyclerView.setAdapter(nearbyAdapter);
            nearbyRecyclerView.invalidate();

            loadMapMarkers(nearbyList, Integer.parseInt(requestId));
        }
    }

    @Override
    public void onResponse(String requestId, JSONArray jaResponse) {

    }

    private ArrayList<Nearby> parseResponse(String requestId, JSONObject joResponse) {

        ArrayList<Nearby> itemsList = null;
        try {
            JSONArray jaItems = joResponse.optJSONArray("results");

            if (jaItems != null) {

                int type = Integer.parseInt(requestId);
                itemsList = new ArrayList<>();

                for (int i = 1; i < jaItems.length(); i++) {

                    JSONObject joItem = jaItems.getJSONObject(i);

                    JSONObject joLocation = joItem.getJSONObject("geometry").getJSONObject("location");
                    double latitude = joLocation.getDouble("lat");
                    double longitude = joLocation.getDouble("lng");

                    String poiName = joItem.getString("name");
                    String address = joItem.getString("vicinity");

                    Nearby nearby = new Nearby(poiName, address, latitude, longitude, type);

                    itemsList.add(nearby);
                }

                Log.d("NearbyActivity", "ITEMS LIST " + itemsList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return itemsList;
    }

    @Override   // VolleyHandler.NetworkListener
    public void onError(String requestId, JSONObject joError) {

    }

    @Override   // VolleyHandler.NetworkListener
    public void onNoInternetConnection(String requestId) {

    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {

        int size = (int) getResources().getDimension(R.dimen.marker_size);

        Drawable vectorDrawable = ContextCompat.getDrawable(this, vectorResId);
        vectorDrawable.setBounds(0, 0, size, size);

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
