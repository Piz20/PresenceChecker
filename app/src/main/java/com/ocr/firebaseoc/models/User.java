package com.ocr.firebaseoc.models;

import androidx.annotation.Nullable;

public class User {

    private String uid ;
    private String username ;
    @Nullable
    private String urlPicture ;

    public User() { }

    public User(String uid , String username , @Nullable String urlPicture) {
        this.uid = uid ;
        this.username = username ;
        this.urlPicture = urlPicture ;
    }

    public String getUid() { return uid ;}

    public String getUsername() { return username ;}
    @Nullable
    public String getUrlPicture() { return urlPicture;}

    public void setUsername(String username) { this.username = username ;}
    public void setUid(String uid) { this.uid = uid ;}
    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture ;}

}
