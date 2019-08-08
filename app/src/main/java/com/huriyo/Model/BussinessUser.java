package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jai on 14/02/18.
 */

public class BussinessUser {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("businesses")
    public List<Businesses> businesses;
    @SerializedName("user_media_base_url")
    public String user_media_base_url;

    public static class BusinessCategoryInfo {
        @SerializedName("_id")
        public String _id;
        @SerializedName("category_name")
        public String category_name;
    }

    public static class Businesses {
        @SerializedName("_id")
        public String _id;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("business_description")
        public String business_description;
        @SerializedName("business_address")
        public String business_address;
        @SerializedName("cover_image")
        public String cover_image;
        @SerializedName("businessCategoryInfo")
        public List<BusinessCategoryInfo> businessCategoryInfo;
        @SerializedName("total_rating")
        public int total_rating;
        @SerializedName("rating_average")
        public int rating_average;
    }
}
