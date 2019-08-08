package com.huriyo.Ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity {

    Button btnChangePassword;
    EditText edConfirm, edNww, edOld;
    LinearLayout llBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

        edConfirm = (EditText) findViewById(R.id.edConfirm);
        edNww = (EditText) findViewById(R.id.edNww);
        edOld = (EditText) findViewById(R.id.edOld);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(ChangePasswordActivity.this);

                String currOld = new Gson().fromJson(Utils.getPreference(ChangePasswordActivity.this,
                        Constants.MM_UserDate), User.class).password;
                String current = edOld.getText().toString();
                if (!current.equalsIgnoreCase(currOld)) {
                    edOld.setError(getString(R.string.error_old_password));
                    edOld.requestFocus();
                    return;
                }

                String newpass = edNww.getText().toString();
                if (newpass == null || newpass.isEmpty()) {
                    edNww.setError(getString(R.string.error_password));
                    edNww.requestFocus();
                    return;
                }

                if (newpass.length() < 8) {
                    edNww.setError(getString(R.string.error_password2));
                    edNww.requestFocus();
                    return;
                }

                String newcpass = edConfirm.getText().toString();
                if (newcpass == null || newcpass.isEmpty()) {
                    edConfirm.setError(getString(R.string.error_password));
                    edConfirm.requestFocus();
                    return;
                }

                if (newcpass.length() < 8) {
                    edConfirm.setError(getString(R.string.error_password2));
                    edConfirm.requestFocus();
                    return;
                }

                if (!newcpass.equalsIgnoreCase(newpass)) {
                    edConfirm.setError(getString(R.string.error_new_confirm_password));
                    edConfirm.requestFocus();
                    return;
                }
                doCHangePassword();
            }
        });
    }

    private void doCHangePassword(){
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_current_password, edOld.getText().toString().trim());
            object.addProperty(Constants.MM_new_password, edNww.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(ChangePasswordActivity.this);
        Globals.initRetrofit(ChangePasswordActivity.this).changePassword(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    gotoLogin();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
        builder.setTitle(getString(R.string.password_change1));
        builder.setMessage(getString(R.string.password_change2));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Utils.setPreference(ChangePasswordActivity.this, Constants.MM_UserDate, "");
                Utils.setPreference(ChangePasswordActivity.this, Constants.MM_token, "");
                Intent i = new Intent(ChangePasswordActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
