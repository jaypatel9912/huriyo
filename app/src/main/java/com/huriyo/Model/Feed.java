package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 06/11/17.
 */

public class Feed {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("posts")
    public List<Posts> posts;
    @SerializedName("media_base_url")
    public String media_base_url;
    @SerializedName("user_media_base_url")
    public String user_media_base_url;

    public static class Post_media {
        @SerializedName("_id")
        public String _id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("file_name")
        public String file_name;
        @SerializedName("media_type")
        public String media_type;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("updated_at")
        public String updated_at;
        @SerializedName("height")
        public double height;
    }

    public static class UserInfo {
        @SerializedName("_id")
        public String _id;
        @SerializedName("first_name")
        public String first_name;
        @SerializedName("last_name")
        public String last_name;
        @SerializedName("cover_image")
        public String cover_image;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("user_type")
        public int user_type;
        @SerializedName("business_name")
        public String business_name;
    }

    public static class Posts {
        @SerializedName("_id")
        public String _id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("post_text")
        public String post_text;
        @SerializedName("post_media")
        public List<Post_media> post_media;
        @SerializedName("location")
        public double location;
        @SerializedName("comment_count")
        public int comment_count;
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
}
