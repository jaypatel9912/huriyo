package com.huriyo.Ui;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsActivity extends BaseActivity {

    LinearLayout llBack;
    EditText edMsg, edTItle;
    Button btnSend;
    User userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        userDetails = new Gson().fromJson(Utils.getPreference(ContactUsActivity.this, Constants.MM_UserDate), User.class);

        llBack = (LinearLayout) findViewById(R.id.llBack);
        edMsg = (EditText) findViewById(R.id.edMsg);
        edTItle = (EditText) findViewById(R.id.edTItle);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edTItle.getText().toString().isEmpty()) {
                    edTItle.setError(getString(R.string.type_msg_title));
                    edTItle.requestFocus();
                    return;
                }

                if (edMsg.getText().toString().isEmpty()) {
                    edMsg.setError(getString(R.string.type_msg_err));
                    edMsg.requestFocus();
                    return;
                }
                addContactUs();
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addContactUs() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_title, edTItle.getText().toString().trim());
            object.addProperty(Constants.MM_message, edMsg.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(ContactUsActivity.this);
        Globals.initRetrofit(ContactUsActivity.this).contactUs(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    try {
                        Toast.makeText(ContactUsActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        edMsg.setText("");
                        edTItle.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ContactUsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(ContactUsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
