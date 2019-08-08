package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 21/11/17.
 */

public class User {
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(String is_verified) {
        this.is_verified = is_verified;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int is_email_phone;
    public String username;
    @SerializedName("_id")
    public String _id;
    @SerializedName("phone_no")
    public String phone_no;
    @SerializedName("country_code")
    public String country_code;
    @SerializedName("email")
    public String email;
    @SerializedName("first_name")
    public String first_name;
    @SerializedName("about_me")
    public String about_me;
    @SerializedName("last_name")
    public String last_name;
    @SerializedName("is_verified")
    public String is_verified;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("location")
    public String location;
    @SerializedName("profile_image")
    public String profile_image;
    @SerializedName("cover_image")
    public String cover_image;
    @SerializedName("profile_image_url")
    public String profile_image_url;
    @SerializedName("cover_image_url")
    public String cover_image_url;
    @SerializedName("token")
    public String token;
    public String place;
    public String company_name;
    @SerializedName("facebook_id")
    public String facebook_id;
    @SerializedName("google_id")
    public String google_id;
    @SerializedName("social_login_type")
    public String social_login_type;
    @SerializedName("avg_rating")
    public float avg_rating;
    @SerializedName("total_friends")
    public int total_friends;
    @SerializedName("total_post")
    public int total_post;
    @SerializedName("user_type")
    public int user_type;
    @SerializedName("business_name")
    public String business_name;

    @SerializedName("business_description")
    public String business_description;
    @SerializedName("business_address")
    public String business_address;
    @SerializedName("businessCategoryInfo")
    public List<BussinessUser.BusinessCategoryInfo> businessCategoryInfo;

    @SerializedName("notification_count")
    public long notification_count;
    @SerializedName("total_friend_requests")
    public int total_friend_requests;

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public float getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(float avg_rating) {
        this.avg_rating = avg_rating;
    }

    public int getTotal_friends() {
        return total_friends;
    }

    public void setTotal_friends(int total_friends) {
        this.total_friends = total_friends;
    }

    public int getTotal_post() {
        return total_post;
    }

    public void setTotal_post(int total_post) {
        this.total_post = total_post;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

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

    public String getBusiness_address() {
        return business_address;
    }

    public void setBusiness_address(String business_address) {
        this.business_address = business_address;
    }

    public List<BussinessUser.BusinessCategoryInfo> getBusinessCategoryInfo() {
        return businessCategoryInfo;
    }

    public void setBusinessCategoryInfo(List<BussinessUser.BusinessCategoryInfo> businessCategoryInfo) {
        this.businessCategoryInfo = businessCategoryInfo;
    }

    public long getNotification_count() {
        return notification_count;
    }

    public void setNotification_count(long notification_count) {
        this.notification_count = notification_count;
    }

    public int getTotal_friend_requests() {
        return total_friend_requests;
    }

    public void setTotal_friend_requests(int total_friend_requests) {
        this.total_friend_requests = total_friend_requests;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public String getSocial_login_type() {
        return social_login_type;
    }

    public void setSocial_login_type(String social_login_type) {
        this.social_login_type = social_login_type;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIs_email_phone() {
        return is_email_phone;
    }

    public void setIs_email_phone(int is_email_phone) {
        this.is_email_phone = is_email_phone;
    }

    public String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
