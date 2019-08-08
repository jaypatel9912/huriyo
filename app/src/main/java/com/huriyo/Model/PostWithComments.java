package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 26/12/17.
 */

public class PostWithComments {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("post")
    public Post post;
    @SerializedName("media_base_url")
    public String media_base_url;
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
        @SerializedName("user_type")
        public int user_type;
        @SerializedName("business_name")
        public String business_name;
    }



    public static class Comments {
        @SerializedName("_id")
        public String _id;
        @SerializedName("comment_text")
        public String comment_text;
        @SerializedName("post_id")
        public String post_id;
        @SerializedName("userInfo")
        public UserInfo userInfo;
        @SerializedName("is_like")
        public boolean is_like;
        @SerializedName("is_unlike")
        public boolean is_unlike;
        @SerializedName("total_likes")
        public int total_likes;
        @SerializedName("total_unlikes")
        public int total_unlikes;
        @SerializedName("timestamp")
        public String timestamp;
    }

    public static class Post {
        @SerializedName("_id")
        public String _id;
        @SerializedName("post_text")
        public String post_text;
        @SerializedName("comment_count")
        public int comment_count;
        @SerializedName("userInfo")
        public UserInfo userInfo;
        @SerializedName("post_media")
        public List<Feed.Post_media> post_media;
        @SerializedName("is_like")
        public boolean is_like;
        @SerializedName("is_unlike")
        public boolean is_unlike;
        @SerializedName("total_likes")
        public int total_likes;
        @SerializedName("total_unlikes")
        public int total_unlikes;
        @SerializedName("timestamp")
        public String timestamp;
        @SerializedName("comments")
        public List<Comments> comments;
    }
}
