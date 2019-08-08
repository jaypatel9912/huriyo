package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 06/12/17.
 */

public class MediaResponse {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("media")
    public Media media;

    public static class Media {
        @SerializedName("file_name")
        public String file_name;
        @SerializedName("media_type")
        public String media_type;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("_id")
        public String _id;
    }
}
