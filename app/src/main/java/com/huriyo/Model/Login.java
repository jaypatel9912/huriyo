package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 21/11/17.
 */

public class Login {
    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;
    public String place;


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
