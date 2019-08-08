package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 29/12/17.
 */

public class UpdatePicture {

    @SerializedName("status")
    public int status;
    @SerializedName("cover_image_url")
    public String cover_image_url;
    @SerializedName("profile_image_url")
    public String profile_image_url;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
}
