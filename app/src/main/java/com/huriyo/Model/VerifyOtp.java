package com.huriyo.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 06/12/17.
 */

public class VerifyOtp {

    @SerializedName("phone_no")
    public String phone_no;
    @SerializedName("otp")
    public String otp;

    public VerifyOtp(String phone_no, String otp) {
        this.phone_no = phone_no;
        this.otp = otp;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
