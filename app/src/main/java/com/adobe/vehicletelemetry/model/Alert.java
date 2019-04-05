package com.adobe.vehicletelemetry.model;

import java.util.Date;

public class Alert {

    private Date date;
    private String detail;

    public Alert(Date date, String detail) {
        this.date = date;
        this.detail = detail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
