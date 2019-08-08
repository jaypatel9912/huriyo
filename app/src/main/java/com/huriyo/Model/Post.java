package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 06/12/17.
 */

public class Post {
    @SerializedName("post_text")
    public String post_text;
    @SerializedName("user_id")
    public String user_id;
    @SerializedName("post_media")
    public List<String> post_media;

    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;
}
