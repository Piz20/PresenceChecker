package com.ocr.firebaseoc.models;

import java.util.Date;

public class Presence {
    private Date date;
    private  String reason ;
    public Presence() {
    }

    public Presence(String reason ,Date date) {
        this.reason = "" ;
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date ;
    }
}
