package com.adobe.vehicletelemetry.model;

import java.util.Date;

public class Report {

    private boolean isLive;
    private Date date;
    private int ratingColorId;
    private int mapId;

    public Report(boolean isLive, Date date, int ratingColorId, int mapId) {
        this.isLive = isLive;
        this.date = date;
        this.ratingColorId = ratingColorId;
        this.mapId = mapId;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRatingColorId() {
        return ratingColorId;
    }

    public void setRatingColorId(int ratingColorId) {
        this.ratingColorId = ratingColorId;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
