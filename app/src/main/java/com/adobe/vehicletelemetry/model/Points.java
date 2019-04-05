package com.adobe.vehicletelemetry.model;

public class Points {
    double lat;
    double lng;
    double distance;
    double rpm;
    double speed;
    double coolantTemp;
    double load;
    double rpmOvershoot;

    public Points(double lat, double lng, double distance) {
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
    }

    public Points(double lat, double lng, double distance, double rpm, double speed, double coolantTemp, double load, double rpmOvershoot) {
        this.lat = lng;
        this.lng = lat;
        this.distance = distance;
        this.rpm = rpm;
        this.speed = speed;
        this.coolantTemp = coolantTemp;
        this.load = load;
        this.rpmOvershoot = rpmOvershoot;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getRpm() {
        return rpm;
    }

    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCoolantTemp() {
        return coolantTemp;
    }

    public void setCoolantTemp(double coolantTemp) {
        this.coolantTemp = coolantTemp;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public double getRpmOvershoot() {
        return rpmOvershoot;
    }

    public void setRpmOvershoot(double rpmOvershoot) {
        this.rpmOvershoot = rpmOvershoot;
    }
}
