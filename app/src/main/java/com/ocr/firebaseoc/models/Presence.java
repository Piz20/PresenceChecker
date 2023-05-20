package com.ocr.firebaseoc.models;

import java.util.Date;

public class Presence {
    private Date date;

    public Presence() {
    }

    public Presence(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date ;
    }
}
