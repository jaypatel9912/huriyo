package com.huriyo.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.huriyo.Model.BasicResponse;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    EditText edEmail;
    Button btnFp;
    LinearLayout llBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_forgot_password);

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);

        btnFp = (Button) findViewById(R.id.btnFp);
        btnFp.setOnClickListener(this);

        edEmail = (EditText) findViewById(R.id.edEmail);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFp:
                resetPassword();
                break;

            case R.id.llBack:
                finish();
                break;
        }
    }

    private void resetPassword() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_username, edEmail.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(ForgotPasswordActivity.this);
        Globals.initRetrofit(ForgotPasswordActivity.this).forgotPassword(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyPhoneActivity.class);
                    intent.putExtra(Constants.MM_username, edEmail.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
