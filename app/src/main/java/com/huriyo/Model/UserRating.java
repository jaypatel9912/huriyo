package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jai on 23/01/18.
 */

public class UserRating {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("ratings")
    public List<Ratings> ratings;
    @SerializedName("rating")
    public Ratings rating;
    @SerializedName("user_media_base_url")
    public String user_media_base_url;

    public static class UserInfo {
        @SerializedName("_id")
        public String _id;
        @SerializedName("first_name")
        public String first_name;
        @SerializedName("last_name")
        public String last_name;
        @SerializedName("profile_image")
        public String profile_image;
    }

    public static class Ratings {
        @SerializedName("_id")
        public String _id;
        @SerializedName("rating")
        public float rating;
        @SerializedName("description")
        public String description;
        @SerializedName("userInfo")
        public UserInfo userInfo;
        @SerializedName("timestamp")
        public String timestamp;
    }
}
