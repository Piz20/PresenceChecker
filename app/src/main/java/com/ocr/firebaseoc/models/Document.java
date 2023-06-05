package com.ocr.firebaseoc.models;

import com.google.firebase.Timestamp;

public class Document {

    private String reason;
    private Timestamp date;

    public Document(String reason, Timestamp date) {
        this.date = date;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public Timestamp getDate() {
        return date ;
    }
}
