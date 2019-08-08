package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 06/12/17.
 */

public class MediaRequest {

    @SerializedName("base64")
    public String base64;
    @SerializedName("extension")
    public String extension;
    @SerializedName("media_type")
    public String media_type;
    @SerializedName("user_id")
    public String user_id;
    @SerializedName("height")
    public double height;
}
