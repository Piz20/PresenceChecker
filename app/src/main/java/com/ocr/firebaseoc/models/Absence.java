package com.ocr.firebaseoc.models;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.Objects;

public class Absence {
    private String reason;
    private Date date;


    public Absence(){ }

    public Absence(String reason, Date date) {
        this.reason = reason;
        this.date = date;

    }
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static String reasonToFrench(String reason){
        if(Objects.equals(reason, "Sickness")){
            return "Maladie" ;
        }
       else if(Objects.equals(reason, "Maternity")){
           return "Maternité" ;
        }
        else if(Objects.equals(reason, "Course")){
            return "Formation" ;
        }
        else
            return "Autre" ;
    }



}
