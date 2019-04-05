package com.adobe.vehicletelemetry.model;

public class Nearby {

    public static int TYPE_SERVICE_CENTER = 1;
    public static int TYPE_PETROL_PUMP = 2;
    public static int TYPE_ENTERTAINMENT = 3;
    public static int TYPE_RESTAURANT = 4;

    private String poiName;
    private String address;
    private double latitude;
    private double longitude;
    private int type;

    public Nearby() {
    }

    public Nearby(String poiName, String address, double latitude, double longitude, int type) {
        this.poiName = poiName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {

        String string = " [poiName:" + poiName
                + ", address:" + address
                + ", latitude:" + latitude
                + ", longitude:" + longitude
                + ", type:" + type + "] ";

        return string;
    }
}
