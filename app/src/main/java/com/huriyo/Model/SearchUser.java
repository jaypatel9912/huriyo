package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jai on 21/01/18.
 */

public class SearchUser {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("results")
    public List<Results> results;
    @SerializedName("user_media_base_url")
    public String user_media_base_url;

    public static class Results {
        @SerializedName("_id")
        public String _id;
        @SerializedName("first_name")
        public String first_name;
        @SerializedName("last_name")
        public String last_name;
        @SerializedName("name")
        public String name;
        @SerializedName("request_status")
        public int request_status;
        @SerializedName("place")
        public String place;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("business_name")
        public String business_name;

        @SerializedName("business_description")
        public String business_description;

        public String getBusiness_name() {
            return business_name;
        }

        public void setBusiness_name(String business_name) {
            this.business_name = business_name;
        }

        public String getBusiness_description() {
            return business_description;
        }

        public void setBusiness_description(String business_description) {
            this.business_description = business_description;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRequest_status() {
            return request_status;
        }

        public void setRequest_status(int request_status) {
            this.request_status = request_status;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public String getUser_media_base_url() {
        return user_media_base_url;
    }

    public void setUser_media_base_url(String user_media_base_url) {
        this.user_media_base_url = user_media_base_url;
    }
}
