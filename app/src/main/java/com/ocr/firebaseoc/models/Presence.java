package com.ocr.firebaseoc.models;

import java.util.Date;

public class Presence {
    private Date date;
    private  String reason ;

    private boolean confirmed ;
    public Presence() {
    }

    public Presence(String reason ,Date date,boolean confirmed) {
        this.reason = reason ;
        this.date = date;
        this.confirmed=confirmed ;
    }

    public Date getDate() {
        return this.date;
    }

    public String getReason() {
        return reason;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setDate(Date date) {
        this.date = date ;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}

