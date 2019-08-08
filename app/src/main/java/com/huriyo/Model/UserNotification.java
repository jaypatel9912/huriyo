package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jai on 22/01/18.
 */

public class UserNotification {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("notifications")
    public List<Notifications> notifications;
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

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notifications> notifications) {
        this.notifications = notifications;
    }

    public String getUser_media_base_url() {
        return user_media_base_url;
    }

    public void setUser_media_base_url(String user_media_base_url) {
        this.user_media_base_url = user_media_base_url;
    }


    public static class Notifications {
        @SerializedName("_id")
        public String _id;
        @SerializedName("notification_type")
        public int notification_type;
        @SerializedName("status")
        public int status;
        @SerializedName("post_id")
        public String post_id;
        @SerializedName("comment_id")
        public String comment_id;
        @SerializedName("userInfo")
        public Feed.UserInfo userInfo;
        @SerializedName("timestamp")
        public String timestamp;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getNotification_type() {
            return notification_type;
        }

        public void setNotification_type(int notification_type) {
            this.notification_type = notification_type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
        }

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public Feed.UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(Feed.UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
