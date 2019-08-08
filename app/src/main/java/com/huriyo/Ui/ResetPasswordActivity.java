package com.huriyo.Ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout llBack;
    Button btnResetPassword;
    EditText edNPass, edCPass;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras().containsKey(Constants.MM_username))
            username = getIntent().getExtras().getString(Constants.MM_username);

        setContentView(R.layout.activity_reset_password);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);

        btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(this);

        edNPass = (EditText) findViewById(R.id.edNPass);
        edCPass = (EditText) findViewById(R.id.edCPass);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                finish();
                break;

            case R.id.btnResetPassword:

                String newpass = edNPass.getText().toString();
                if (newpass == null || newpass.isEmpty()) {
                    edNPass.setError(getString(R.string.error_password));
                    edNPass.requestFocus();
                    return;
                }

                if (newpass.length() < 8) {
                    edNPass.setError(getString(R.string.error_password2));
                    edNPass.requestFocus();
                    return;
                }


                String newcpass = edCPass.getText().toString();
                if (newcpass == null || newcpass.isEmpty()) {
                    edCPass.setError(getString(R.string.error_password));
                    edCPass.requestFocus();
                    return;
                }

                if (newcpass.length() < 8) {
                    edCPass.setError(getString(R.string.error_password2));
                    edCPass.requestFocus();
                    return;
                }

                if (!newcpass.equalsIgnoreCase(newpass)) {
                    edCPass.setError(getString(R.string.error_new_confirm_password));
                    edCPass.requestFocus();
                    return;
                }
                resetPassword();
                break;

        }
    }


    private void resetPassword() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_username, username);
            object.addProperty(Constants.MM_new_password, edNPass.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(ResetPasswordActivity.this);
        Globals.initRetrofit(ResetPasswordActivity.this).resetPassword(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    Toast.makeText(ResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
