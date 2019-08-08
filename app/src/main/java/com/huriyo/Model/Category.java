package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 22/12/17.
 */

public class Category {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("response_code")
    public int response_code;
    @SerializedName("categories")
    public List<Categories> categories;

    public static class Categories {
        @SerializedName("_id")
        public String _id;
        @SerializedName("category_name")
        public String category_name;

        public boolean cheked;
    }
}
