package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jai on 07/02/18.
 */

public class CommentRes {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("comment")
    public Comment comment;

    public static class Comment {
        @SerializedName("_id")
        public String _id;
        @SerializedName("comment_text")
        public String comment_text;
        @SerializedName("timestamp")
        public String timestamp;
    }
}
