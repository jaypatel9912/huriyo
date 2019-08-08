package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jai on 21/01/18.
 */

public class SearchPost {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("results")
    public List<Feed.Posts> results;
    @SerializedName("media_base_url")
    public String media_base_url;
    @SerializedName("user_media_base_url")
    public String user_media_base_url;

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

    public List<Feed.Posts> getResults() {
        return results;
    }

    public void setResults(List<Feed.Posts> results) {
        this.results = results;
    }

    public String getMedia_base_url() {
        return media_base_url;
    }

    public void setMedia_base_url(String media_base_url) {
        this.media_base_url = media_base_url;
    }

    public String getUser_media_base_url() {
        return user_media_base_url;
    }

    public void setUser_media_base_url(String user_media_base_url) {
        this.user_media_base_url = user_media_base_url;
    }

    public static class UserInfo {
        @SerializedName("_id")
        public String _id;
        @SerializedName("first_name")
        public String first_name;
        @SerializedName("last_name")
        public String last_name;

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
    }


}
