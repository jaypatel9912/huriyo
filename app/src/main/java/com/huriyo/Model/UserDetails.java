package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 29/12/17.
 */

public class UserDetails {

    @SerializedName("user")
    public User user;
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;

    public static class BusinessCategoryInfo {
            @SerializedName("_id")
            public String _id;
            @SerializedName("category_name")
            public String category_name;
    }

    public static class ExperienceInfo {
        @SerializedName("_id")
        public String _id;
        @SerializedName("job_title")
        public String job_title;
        @SerializedName("company_name")
        public String company_name;
        @SerializedName("start_date")
        public String start_date;
        @SerializedName("is_currently_work")
        public long is_currently_work;
        @SerializedName("end_date")
        public String end_date;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("updated_at")
        public String updated_at;
        @SerializedName("user_id")
        public String user_id;
    }

    public static class User {
        @SerializedName("_id")
        public String _id;
        @SerializedName("first_name")
        public String first_name;
        @SerializedName("last_name")
        public String last_name;
        @SerializedName("total_post")
        public int total_post;
        @SerializedName("cover_image")
        public String cover_image;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("about_me")
        public String about_me;
        @SerializedName("businessCategoryInfo")
        public List<BussinessUser.BusinessCategoryInfo> businessCategoryInfo;
        @SerializedName("experienceInfo")
        public List<ExperienceInfo> experienceInfo;
        @SerializedName("total_friends")
        public int total_friends;
        @SerializedName("request_status")
        public int request_status;
        @SerializedName("avg_rating")
        public float avg_rating;
        @SerializedName("is_rating_submitted")
        public boolean is_rating_submitted;
        @SerializedName("user_type")
        public int user_type;
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("business_description")
        public String business_description;
        @SerializedName("business_address")
        public String business_address;
    }
}
