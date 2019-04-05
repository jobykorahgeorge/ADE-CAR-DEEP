package com.adobe.vehicletelemetry.model;

public class Wheel {

    private int acc;
    private int brake;
    private long timestamp;

    public Wheel(int acc, int brake, long timestamp) {
        this.acc = acc;
        this.brake = brake;
        this.timestamp = timestamp;
    }

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public int getBrake() {
        return brake;
    }

    public void setBrake(int brake) {
        this.brake = brake;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
