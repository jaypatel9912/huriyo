package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 29/12/17.
 */

public class UserExperience {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("experience")
    public UserDetails.ExperienceInfo experience;


}
