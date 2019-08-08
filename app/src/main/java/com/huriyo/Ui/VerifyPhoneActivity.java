package com.huriyo.Ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.Feed;
import com.huriyo.Model.VerifyOtp;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout llBack, llResend;
    Button btnverify;
    EditText edVc;
    String otp, phone_no;
    String username;
    TextView tvusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        if (getIntent().getExtras().containsKey(Constants.MM_username))
            username = getIntent().getExtras().getString(Constants.MM_username);

        tvusername = (TextView) findViewById(R.id.username);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        llResend = (LinearLayout) findViewById(R.id.llResend);
        llResend.setOnClickListener(this);
        edVc = (EditText) findViewById(R.id.edVc);
        btnverify = (Button) findViewById(R.id.btnverify);
        btnverify.setOnClickListener(this);

//        if (!getIntent().getExtras().containsKey(Constants.MM_otp)) {
//            finish();
//        }
        phone_no = getIntent().getExtras().getString(Constants.MM_phone_no);
        tvusername.setText("We have sent code to " + username == null ? phone_no : username);
//        otp = getIntent().getExtras().getString(Constants.MM_otp);

//        edVc.setText(otp);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                finish();
                break;

            case R.id.llResend:
                Utils.hideSoftKeyboard(VerifyPhoneActivity.this);
                if (username == null)
                    resendOtp();
                else
                    resetPassword();
                break;

            case R.id.btnverify:
                Utils.hideSoftKeyboard(VerifyPhoneActivity.this);
                if (username == null)
                    verifyOpt();
                else
                    verifyForgotPasswordOtp();
                break;

        }
    }

    public void verifyOpt() {
        Utils.showProgressDialog(VerifyPhoneActivity.this);
        Globals.initRetrofit(VerifyPhoneActivity.this).verifyOtp(new VerifyOtp(phone_no, otp)).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().response_code <= 0) {
                        Toast.makeText(VerifyPhoneActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(VerifyPhoneActivity.this, SelectCategoryActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                Toast.makeText(VerifyPhoneActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void verifyForgotPasswordOtp() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_username, username);
            object.addProperty(Constants.MM_otp, edVc.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(VerifyPhoneActivity.this);
        Globals.initRetrofit(VerifyPhoneActivity.this).verifyForgotPasswordOtp(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().response_code != 200) {
                        Toast.makeText(VerifyPhoneActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(VerifyPhoneActivity.this, ResetPasswordActivity.class);
                        i.putExtra(Constants.MM_username, username);
                        startActivity(i);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                Toast.makeText(VerifyPhoneActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void resendOtp() {
        Utils.showProgressDialog(VerifyPhoneActivity.this);
        Globals.initRetrofit(VerifyPhoneActivity.this).resendOtp(new VerifyOtp(username == null ? phone_no : username, "")).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().response_code <= 0) {
                        Toast.makeText(VerifyPhoneActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
//                        otp = response.body().otp;
//                        edVc.setText(otp);
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                Toast.makeText(VerifyPhoneActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetPassword() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_username, username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(VerifyPhoneActivity.this);
        Globals.initRetrofit(VerifyPhoneActivity.this).forgotPassword(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();

            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(VerifyPhoneActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
